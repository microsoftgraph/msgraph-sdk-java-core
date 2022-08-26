package com.microsoft.graph.tasks;

import javax.annotation.Nullable;

/**
 * Respresents the result of a large file upload task.
 *
 * @param <ResultType> type of the deserialized response.
 */
public class LargeFileUploadResult<ResultType> {
    /**
     * Location response header value if provided.
     */
    @Nullable
    public String location;

    /**
     * Deserialized response body if the response has content.
     */
    @Nullable
    public ResultType responseBody;
}
