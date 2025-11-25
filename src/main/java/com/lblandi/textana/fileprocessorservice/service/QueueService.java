package com.lblandi.textana.fileprocessorservice.service;

/**
 * @author lblandi
 * @since 2025-11-25
 */
public interface QueueService {
    /**
     * Starts listening for new messages in the queue.
     * Implementations of this method should handle the retrieval
     * and processing of incoming messages from the message queue.
     */
    void listenNewMessages();
}
