package com.microsoft.graph.tasks;

import com.microsoft.graph.models.IUploadSession;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.serialization.Parsable;
import jdk.internal.util.xml.impl.Input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Objects;

public class LargeFileUploadTask {

    private final int defaultMaxSliceSize = 5*1024*1024;
    private IUploadSession uploadSession;
    private RequestAdapter requestAdapter;
    private InputStream uploadStream;
    private int maxSliceSize;
    private ArrayList<AbstractMap.SimpleEntry<Long, Long>> rangesRemaining;
    private long TotalUploadLength;


    public LargeFileUploadTask(@Nonnull IUploadSession uploadSession, @Nonnull InputStream uploadStream, @Nullable int maxSliceSize, @Nullable RequestAdapter requestAdapter) {
        Objects.requireNonNull(uploadSession);
        Objects.requireNonNull(uploadStream);
        this.maxSliceSize = Objects.isNull(maxSliceSize) ? defaultMaxSliceSize : maxSliceSize;


    }

    public UploadSession ExtractSessionFromParsable(IUploadSession uploadSession) throws IllegalArgumentException{
        if (!uploadSession.getFieldDeserializers().containsKey("expirationDateTime"))
            throw new IllegalArgumentException("The Parsable does not contain the 'expirationDateTime' property");
        if (!uploadSession.getFieldDeserializers().containsKey("nextExpectedRanges"))
            throw new IllegalArgumentException("The Parsable does not contain the 'nextExpectedRanges' property");
        if (!uploadSession.getFieldDeserializers().containsKey("uploadUrl"))
            throw new IllegalArgumentException("The Parsable does not contain the 'uploadUrl' property");
        return new UploadSession() {{

        }};
    }
}
