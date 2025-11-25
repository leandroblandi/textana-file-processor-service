package com.lblandi.textana.fileprocessorservice.repository;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import com.lblandi.textana.fileprocessorservice.request.SaveAnalysisItemRequest;

/**
 * @author lblandi
 * @since 2025-11-25
 */
public interface FileAnalysisRepository {

    /**
     * Persists the analysis data into a storage system.
     *
     * @param request the request object containing the details of the analysis to be saved,
     *                including the file identifier, status, and the timestamp of the last step.
     */
    void saveAnalysis(SaveAnalysisItemRequest request);

    /**
     * Updates the analysis record for the specified file with the provided details.
     *
     * @param uuid the unique identifier of the file whose analysis record is being updated
     * @param resume the summarized content of the analysis
     * @param emotionDetected the emotion detected during the analysis process
     */
    void updateAnalysis(String uuid, String resume, EmotionDetectedEnum emotionDetected);

    /**
     * Updates the status of a file analysis record identified by its unique identifier.
     *
     * @param uuid the unique identifier of the file whose analysis status is to be updated
     * @param status the new status to be assigned to the file analysis record
     */
    void updateAnalysisStatus(String uuid, FileAnalysisStatusEnum status);
}
