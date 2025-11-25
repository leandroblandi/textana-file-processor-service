package com.lblandi.textana.fileprocessorservice.repository;

import com.lblandi.textana.fileprocessorservice.entity.AnalysisResultEntity;
import com.lblandi.textana.fileprocessorservice.entity.FileAnalysisEntity;
import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import com.lblandi.textana.fileprocessorservice.request.SaveAnalysisItemRequest;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DynamoDbFileAnalysisRepository implements FileAnalysisRepository {
    private static final String TABLE_NAME = "textana-file-analysis";

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbFileAnalysisRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveAnalysis(SaveAnalysisItemRequest request) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("uuid", AttributeValue.fromS(request.getFileIdentifier()));
        item.put("status", AttributeValue.fromS(request.getStatus().name()));
        item.put("lastStepAt", AttributeValue.fromS(request.getLastStepAt().toString()));

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void updateAnalysis(String uuid, String resume, EmotionDetectedEnum emotionDetected) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();

        updates.put("resume", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(resume))
                .action(AttributeAction.PUT)
                .build());

        updates.put("emotionDetected", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(emotionDetected.name()))
                .action(AttributeAction.PUT)
                .build());

        updates.put("status", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(FileAnalysisStatusEnum.COMPLETED.name()))
                .action(AttributeAction.PUT)
                .build());

        updates.put("lastStepAt", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(LocalDateTime.now().toString()))
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("uuid", AttributeValue.fromS(uuid)))
                .attributeUpdates(updates)
                .build();

        dynamoDbClient.updateItem(request);
    }

    @Override
    public void updateAnalysisStatus(String uuid, FileAnalysisStatusEnum status) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();

        updates.put("status", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(status.name()))
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("uuid", AttributeValue.fromS(uuid)))
                .attributeUpdates(updates)
                .build();

        dynamoDbClient.updateItem(request);
    }
}
