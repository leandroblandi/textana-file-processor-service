package com.lblandi.textana.fileprocessorservice.service;

/**
 * @author lblandi
 * @since 2025-11-25
 */
public interface FileService {

    /**
     * Retrieves the content of a file based on its unique identifier from S3
     *
     * @param fileIdentifier the unique identifier of the file whose content is to be retrieved
     * @return the content of the file as a String
     */
    String getFileContent(String fileIdentifier);
}
