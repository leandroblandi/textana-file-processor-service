package com.lblandi.textana.fileprocessorservice.entity;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileAnalysisEntity {
    private String uuid;
    private FileAnalysisStatusEnum status;
    private AnalysisResultEntity result;
    private LocalDateTime lastStepAt;

    public static FileAnalysisEntity of(String fileIdentifier, String resume, EmotionDetectedEnum emotionDetected) {
        return FileAnalysisEntity.builder()
                .uuid(fileIdentifier)
                .status(FileAnalysisStatusEnum.COMPLETED)
                .result(mapToAnalysisResultEntity(resume, emotionDetected))
                .build();
    }

    private static AnalysisResultEntity mapToAnalysisResultEntity(String resume, EmotionDetectedEnum emotionDetected) {
        return AnalysisResultEntity.builder()
                .emotionDetected(emotionDetected)
                .resume(resume)
                .build();
    }
}

