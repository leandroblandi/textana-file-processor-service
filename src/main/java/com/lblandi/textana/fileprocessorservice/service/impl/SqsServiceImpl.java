package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import com.lblandi.textana.fileprocessorservice.repository.DynamoDbFileAnalysisRepository;
import com.lblandi.textana.fileprocessorservice.request.SaveAnalysisItemRequest;
import com.lblandi.textana.fileprocessorservice.service.QueueService;
import com.lblandi.textana.fileprocessorservice.worker.FileProcessorWorker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SqsServiceImpl implements QueueService {
    public static final int MAX_NUMBER_MESSAGES_SQS = 10;
    public static final int WAIT_TIME_SECONDS_SQS = 10;
    public static final int VISIBILITY_TIMEOUT_SQS = 30;

    private final SqsClient sqsClient;
    private final DynamoDbFileAnalysisRepository fileAnalysisRepository;
    private final FileProcessorWorker fileProcessorWorker;

    @Value("${textana.aws.sqs.url}")
    private String queueUrl;

    public SqsServiceImpl(SqsClient sqsClient, DynamoDbFileAnalysisRepository fileAnalysisRepository, FileProcessorWorker fileProcessorWorker) {
        this.sqsClient = sqsClient;
        this.fileAnalysisRepository = fileAnalysisRepository;
        this.fileProcessorWorker = fileProcessorWorker;
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void listenNewMessages() {
        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_NUMBER_MESSAGES_SQS)
                .waitTimeSeconds(WAIT_TIME_SECONDS_SQS)
                .visibilityTimeout(VISIBILITY_TIMEOUT_SQS)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        if (messages.isEmpty()) {
            log.debug("No new messages found in SQS");
            return;
        }

        processMessages(messages);
    }

    /**
     * Processes a list of messages retrieved from the SQS queue.
     * Each message's file identifier is logged and handled individually.
     * If any exception occurs during processing, the message's status is updated to FAILED
     * in the file analysis repository.
     *
     * @param messages The list of messages to process, each containing data such as
     *        a file identifier and metadata.
     */
    private void processMessages(List<Message> messages) {
        log.debug("Processing {} new messages from SQS", messages.size());

        for (var msg : messages) {
            try {
                log.info("Trying to process message with file identifier: {}", msg.body());
                handleMessage(msg);
            } catch (Exception e) {
                log.error("Error processing message {}", msg.body(), e);
                fileAnalysisRepository.updateAnalysisStatus(msg.body(), FileAnalysisStatusEnum.FAILED);
            }
        }
    }

    /**
     * Handles the processing of a given message. This method performs the following steps:
     * 1. Saves the analysis details (e.g., file identifier, status, timestamp) in the database.
     * 2. Passes the file identifier for processing by the file processor worker.
     * 3. Deletes the processed message from the SQS queue.
     *
     * @param msg The message to process, containing a file identifier and other message metadata.
     */
    private void handleMessage(Message msg) {

        // Save the analysis in the database
        fileAnalysisRepository.saveAnalysis(SaveAnalysisItemRequest.builder()
                .fileIdentifier(msg.body())
                .lastStepAt(LocalDateTime.now())
                .status(FileAnalysisStatusEnum.IN_PROGRESS)
                .build());

        // Process the file
        fileProcessorWorker.process(msg.body());

        // After all, delete the message from the queue
        sqsClient.deleteMessage(del -> del
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
        );
    }
}
