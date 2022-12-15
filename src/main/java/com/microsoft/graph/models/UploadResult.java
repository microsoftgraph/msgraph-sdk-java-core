package com.microsoft.graph.models;

import java.net.URI;

public class UploadResult<T> {

    public IUploadSession UploadSession;

    public T ItemResponse;

    public URI Location;

    public boolean UploadSucceeded() {
        return (this.ItemResponse != null) || (this.Location != null);
    }


}
