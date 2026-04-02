package com.gogidix.rapidassist.ai.speech.recognition.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for AI Speech Recognition Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered speech recognition capabilities including:
 * - Speech-to-text conversion
 * - Multi-language speech recognition
 * - Real-time transcription
 * - Speaker identification and diarization
 * - Audio format handling
 * - Batch audio processing
 * - Transcription confidence scoring
 */
@SpringBootApplication
public class Application {

    /**
     * Main entry point for the application
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
