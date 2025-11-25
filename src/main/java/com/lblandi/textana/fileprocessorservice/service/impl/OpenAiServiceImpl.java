package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAiServiceImpl implements AiService {
    private static final String PROMPT_RESUME = "Analyze the following text and produce a singleparagraph summary " +
            "using concise and direct language: Text: %s. Do not include emojis, disclaimers, or metadata. Return " +
            "only the raw summary text with no additional formatting, headers, or explanations.";

    private static final String PROMPT_SENTIMENT = "Analyze the sentiment of the following text and return " +
            "exactly one word: POSITIVE | NEUTRAL | NEGATIVE. Text: %s. The output must contain only the " +
            "selected word. Do not include explanations, emojis, formatting, or any additional text.";

    private final ChatClient chatClient;

    public OpenAiServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String resumeText(String text) {
        log.debug("Trying to resume text for text: {}", text.substring(0, 20) + "...");
        return sanitizeResponse(chatClient.prompt(PROMPT_RESUME.formatted(text)).call().content());
    }

    @Override
    public EmotionDetectedEnum detectSentiment(String text) {
        log.debug("Trying to detect sentiment for text: {}", text.substring(0, 20) + "...");
        String emotion = sanitizeResponse(chatClient.prompt(PROMPT_SENTIMENT.formatted(text)).call().content());
        return evaluateEmotion(emotion);
    }

    private String sanitizeResponse(String response) {
        return response == null ? "" : response.replaceAll("\\s+", " ").trim();
    }

    private EmotionDetectedEnum evaluateEmotion(String emotion) {
        try {
            return EmotionDetectedEnum.valueOf(emotion.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid emotion detected: {}", emotion);
            return EmotionDetectedEnum.NEUTRAL;
        }
    }
}
