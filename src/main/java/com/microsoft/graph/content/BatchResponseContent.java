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

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/** Respresents the result of a JSON batch request */
public class BatchResponseContent {
    /** Responses to the steps from the request */
    @Expose
    @SerializedName("responses")
    public List<BatchResponseStep<?>> responses;

    /**
     * Gets a response to a request in the batch by its id
     * @param <T> Type of the response body
     * @param stepId Id of the request step in the batch request
     * @return The step response corresponding to the ID or null
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> BatchResponseStep<T> getResponseById(@Nonnull final String stepId) {
        Objects.requireNonNull(stepId, "parameter stepId cannot be null");
        if(responses == null) return null;

        for(final BatchResponseStep<?> step : responses) {
            if(stepId.equals(step.id))
                return (BatchResponseStep<T>)step;
        }
        return null;
    }
}
