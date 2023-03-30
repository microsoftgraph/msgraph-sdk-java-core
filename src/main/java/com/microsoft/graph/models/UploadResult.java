package com.microsoft.graph.models;

import java.net.URI;

public class UploadResult<T> {

    public IUploadSession uploadSession;

    public T itemResponse;

    public URI location;

    public boolean uploadSucceeded() {
        return (this.itemResponse != null) || (this.location != null);
    }


}
