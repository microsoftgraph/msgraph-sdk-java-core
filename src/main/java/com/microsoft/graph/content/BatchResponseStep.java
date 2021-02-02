// ------------------------------------------------------------------------------
// Copyright (c) 2021 Microsoft Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------
package com.microsoft.graph.content;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.microsoft.graph.http.GraphErrorResponse;
import com.microsoft.graph.http.GraphFatalServiceException;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.serializer.ISerializer;

/** Response for the batch step */
public class BatchResponseStep<T> extends BatchStep<T> {
    /** Http status code of the response */
    @Expose
    @SerializedName("status")
    public int status;
    /** Serializer to use for response deserialization */
    protected ISerializer serializer;

    /**
     * Returned the deserialized response body of the current step
     * @param <T2> type of the response body
     * @param resultClass class of the resulting response body
     * @return the deserialized response body
     * @throws GraphServiceException when a bad request was sent
     * @throws GraphFatalServiceException when the service did not complete the operation as expected because of an internal error
     */
    @Nullable
    public <T2> T2 getDeserializedBody(@Nonnull final Class<T2> resultClass) throws GraphServiceException, GraphFatalServiceException {
        Objects.requireNonNull(resultClass, "parameter resultClass cannot be null");
        if(serializer == null || body == null || !(body instanceof JsonElement)) return null;

        final GraphErrorResponse error = serializer.deserializeObject((JsonElement)body, GraphErrorResponse.class);
        if(error == null || error.error == null) {
            return serializer.deserializeObject((JsonElement)body, resultClass);
        } else
            throw GraphServiceException.createFromResponse("", "", new ArrayList<>(), "", headers, "", status, error, false);
    }
}
