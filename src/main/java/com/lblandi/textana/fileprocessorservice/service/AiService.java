package com.lblandi.textana.fileprocessorservice.service;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;

/**
 * @author lblandi
 * @since 2025-11-25
 */
public interface AiService {

    /**
     * Summarizes the given text into a more concise version.
     *
     * @param text the input text to be summarized
     * @return a summarized version of the input text
     */
    String resumeText(String text);

    /**
     * Analyzes the provided text to detect the predominant sentiment.
     *
     * @param text the input text to be analyzed for sentiment
     * @return the detected sentiment, represented as an EmotionDetectedEnum (POSITIVE, NEUTRAL, or NEGATIVE)
     */
    EmotionDetectedEnum detectSentiment(String text);
}
