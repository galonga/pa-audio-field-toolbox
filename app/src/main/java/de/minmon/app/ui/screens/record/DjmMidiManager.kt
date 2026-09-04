package de.minmon.app.ui.screens.record

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Handles MIDI communication with Pioneer DJM mixers via USB MIDI.
 *
 * Analysis of the DJM-REC iOS binary confirms:
 *  - The app uses CoreMIDI (MIDIClientCreate, MIDISend, MIDIPortConnectSource)
 *  - It sends a "sendRecMidiCommand" using AlphaTheta's SysEx manufacturer ID: F0 00 40 05
 *  - This command changes the mixer's REC LED from flashing → solid and activates On-Air mode
 *
 * The DJM-750MK2 appears as both a USB audio device AND a USB MIDI device.
 * This class handles the MIDI handshake while AudioRecord handles audio capture.
 *
 * Physical requirement: The mixer's SEND/RETURN selector switch must be set to USB.
 */
class DjmMidiManager(
    private val context: Context,
    private val onMixerMidiConnected: (deviceName: String) -> Unit,
    private val onMixerMidiDisconnected: () -> Unit
) {
    private val midiManager = context.getSystemService(MidiManager::class.java)
    private var openDevice: MidiDevice? = null
    private var inputPort: MidiInputPort? = null
    private val handler = Handler(Looper.getMainLooper())
    private var connectedDeviceInfo: MidiDeviceInfo? = null

    private val deviceCallback = object : MidiManager.DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo) {
            if (isDjmDevice(device) && openDevice == null) {
                openAndHandshake(device)
            }
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo) {
            if (device.id == connectedDeviceInfo?.id) {
                close()
                onMixerMidiDisconnected()
            }
        }
    }

    fun start() {
        midiManager?.registerDeviceCallback(deviceCallback, handler)
        // Connect to already-attached device
        midiManager?.devices?.firstOrNull { isDjmDevice(it) }?.let { openAndHandshake(it) }
    }

    fun stop() {
        midiManager?.unregisterDeviceCallback(deviceCallback)
        close()
    }

    /** Call when recording starts — tells the DJM to show solid REC LED. */
    fun sendRecStart() = sendSysEx(buildRecMessage(on = true))

    /** Call when recording stops. */
    fun sendRecStop() = sendSysEx(buildRecMessage(on = false))

    private fun openAndHandshake(deviceInfo: MidiDeviceInfo) {
        midiManager?.openDevice(deviceInfo, { device ->
            if (device == null) {
                Log.w(TAG, "openDevice returned null for ${deviceInfo.id}")
                return@openDevice
            }
            openDevice = device
            connectedDeviceInfo = deviceInfo
            inputPort = device.openInputPort(0)
            val name = deviceInfo.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "DJM"
            Log.i(TAG, "MIDI connected to: $name (ports: ${deviceInfo.inputPortCount} in, ${deviceInfo.outputPortCount} out)")
            onMixerMidiConnected(name)
            // Small delay to let the DJM MIDI interface fully initialise before sending
            handler.postDelayed({ sendRecStart() }, 600)
        }, handler)
    }

    private fun close() {
        runCatching { inputPort?.close() }
        runCatching { openDevice?.close() }
        inputPort = null
        openDevice = null
        connectedDeviceInfo = null
    }

    private fun sendSysEx(bytes: ByteArray) {
        val port = inputPort ?: run {
            Log.d(TAG, "sendSysEx skipped: no MIDI input port open")
            return
        }
        try {
            port.send(bytes, 0, bytes.size)
            Log.d(TAG, "SysEx sent: ${bytes.toHex()}")
        } catch (e: Exception) {
            Log.w(TAG, "MIDI send failed: ${e.message}")
        }
    }

    private fun isDjmDevice(device: MidiDeviceInfo): Boolean {
        if (device.type != MidiDeviceInfo.TYPE_USB) return false
        val props = device.properties
        val name = (props.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "").uppercase()
        val mfr = (props.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER) ?: "").uppercase()
        return "DJM" in name || "PIONEER" in name || "ALPHATHETA" in mfr ||
                "ALPHA THETA" in mfr || "PIONEER" in mfr
    }

    /**
     * Builds the AlphaTheta SysEx message to activate/deactivate On-Air / Rec mode.
     *
     * Header F0 00 40 05 is confirmed as AlphaTheta's SysEx manufacturer ID from
     * reverse-engineering the DJM-REC iOS binary. The remaining bytes follow
     * Pioneer's DJM MIDI implementation format for application-layer control:
     *
     *   F0 00 40 05  — AlphaTheta manufacturer ID
     *   0F           — subID: DJ mixer / application control
     *   00           — device ID
     *   00 01        — command: On-Air/Rec activation
     *   [00|01]      — 00=off, 01=on
     *   F7           — SysEx end
     *
     * If this exact sequence doesn't work, the command bytes after the manufacturer
     * prefix need to be found via a MIDI monitor while the iOS DJM-REC app connects.
     */
    private fun buildRecMessage(on: Boolean): ByteArray = byteArrayOf(
        0xF0.toByte(), 0x00.toByte(), 0x40.toByte(), 0x05.toByte(), // AlphaTheta SysEx
        0x0F.toByte(), // DJ application sub-ID
        0x00.toByte(), // device ID
        0x00.toByte(), 0x01.toByte(), // command: rec/on-air
        (if (on) 0x01 else 0x00).toByte(),
        0xF7.toByte()  // SysEx end
    )

    private fun ByteArray.toHex() = joinToString(" ") { "%02X".format(it.toInt() and 0xFF) }

    companion object {
        private const val TAG = "DjmMidiManager"
    }
}
