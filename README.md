# PA Audio Field Toolbox

A small Android audio toolkit for practical audio engineering, measurement and experimentation.

The app brings together everyday audio calculations, real-time signal analysis, signal generation and audio recording in one lightweight Android application.

> Built with Kotlin / Android with a focus on audio, creative technology and practical tools.

## Features

### 🧮 Tools

#### dB Calculator
Convert between common voltage and level representations:

- Voltage (RMS)
- dBu
- dBV
- dB voltage ratios
- dB power ratios

#### Delay Calculator
Calculate the relationship between:

- Distance
- Acoustic delay

Supports meters and feet.

#### Power / Impedance Calculator
A practical calculator based on Ohm's law.

Enter any two values and calculate:

- Voltage
- Current
- Resistance / Impedance
- Power

Useful for checking amplifier, speaker and other audio-system configurations.

---

### 📊 Analyzer

#### Real-time Audio Analyzer

Analyze microphone input with:

- Live waveform
- FFT spectrum
- Frequency display across the audible range
- BPM Counter

A simple way to inspect incoming audio signals in real time.

---

### 🌊 Generator

#### Signal Generator

Generate common test signals:

- Sine
- Square
- Triangle
- Sawtooth
- Sweep
- White noise
- Pink noise

Adjust frequency and amplitude.

Useful for basic audio testing, system checks and experimentation.

---

### 🎙️ Recorder

#### DJM / USB Audio Recorder

Record audio through the Android audio input.

The recorder provides:

- Recording timer
- Input level meter
- Gain control
- Microphone fallback when no USB mixer is connected
- USB audio support

The app has been tested with a Pioneer DJM-750MK2 workflow using USB audio.

> USB audio behaviour depends on the Android device and connected hardware.

## Why this project?

This project started as a collection of small audio tools that are useful in real-world situations.

Instead of building another generic demo application, the goal is to explore the intersection of:

- Android development
- Kotlin
- Digital signal processing
- Audio I/O
- USB audio
- Audio engineering
- Real-time visualization
- Creative technology

It is also an experiment in building a useful application with a deliberately simple interface.

## Technology

- Kotlin
- Android
- Real-time audio processing
- FFT-based frequency analysis
- Android audio input
- USB audio

The project is intentionally kept small and focused so that the audio-related implementation remains easy to explore.

## Screenshots

Add screenshots to the `screenshots/` directory and link them here, for example:

```text
screenshots/
├── tools.png
├── db-calculator.png
├── delay-calculator.png
├── power-impedance.png
├── signal-generator.png
├── analyzer.png
└── recorder.png
```

## Project structure

```text
Audio Tools
├── Tools
│   ├── dB Calculator
│   ├── Delay Calculator
│   └── Power / Impedance Calculator
│
├── Analyzer
│   └── FFT / Waveform
│
├── Generator
│   └── Test Signals / Noise
│
└── Recorder
    └── Microphone / USB Audio
```

## Status

This is a personal portfolio and experimentation project.

The application is functional, but it is not intended to replace dedicated measurement, recording or audio-analysis hardware/software.

## Future ideas

Possible improvements include:

- More detailed FFT controls
- Peak and RMS measurements
- Additional frequency / level visualization
- More USB audio device information
- Recording format and sample-rate options
- Improved audio routing
- Additional audio engineering calculators
- More test signal options
- Expanded DSP documentation

## About

Built by **Timo Arndt** as part of an ongoing exploration of software, audio engineering and creative technology.

Android / Kotlin is the main focus, with audio and physical technology providing the playground.

---

**Audio · Software · Hardware · Creative Technology**
