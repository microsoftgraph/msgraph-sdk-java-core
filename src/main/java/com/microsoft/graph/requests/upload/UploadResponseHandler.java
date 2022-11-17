package com.microsoft.graph.requests.upload;

import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class UploadResponseHandler {

    private final ParseNodeFactory _parseNodeFactory;

    public UploadResponseHandler(@Nullable ParseNodeFactory parseNodeFactory) {
        this._parseNodeFactory = (parseNodeFactory == null) ? ParseNodeFactoryRegistry.defaultInstance : parseNodeFactory;
    }

    //public CompletableFuture<UploadResult<T>> HandleResponse<T>

}
