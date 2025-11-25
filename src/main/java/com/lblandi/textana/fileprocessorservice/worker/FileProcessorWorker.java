package com.lblandi.textana.fileprocessorservice.worker;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.repository.DynamoDbFileAnalysisRepository;
import com.lblandi.textana.fileprocessorservice.service.AiService;
import com.lblandi.textana.fileprocessorservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileProcessorWorker {
    private final AiService aiService;
    private final FileService fileService;
    private final DynamoDbFileAnalysisRepository fileAnalysisRepository;

    public FileProcessorWorker(AiService aiService, FileService fileService,
                               DynamoDbFileAnalysisRepository fileAnalysisRepository) {
        this.aiService = aiService;
        this.fileService = fileService;
        this.fileAnalysisRepository = fileAnalysisRepository;
    }

    public void process(String fileIdentifier) {

        // Process file by analyzing its content
        String fileContent = fileService.getFileContent(fileIdentifier);
        String resumeText = aiService.resumeText(fileContent);
        EmotionDetectedEnum sentiment = aiService.detectSentiment(resumeText);

        // Update DynamoDB table with analysis results
        fileAnalysisRepository.updateAnalysis(fileIdentifier, resumeText, sentiment);
        log.info("Analysis completed for file: {}", fileIdentifier);
    }
}