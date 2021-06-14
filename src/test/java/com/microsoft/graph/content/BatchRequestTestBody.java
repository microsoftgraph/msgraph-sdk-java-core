package com.microsoft.graph.content;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.microsoft.graph.serializer.AdditionalDataManager;
import com.microsoft.graph.serializer.IJsonBackedObject;
import com.microsoft.graph.serializer.ISerializer;

public class BatchRequestTestBody implements IJsonBackedObject {
    @Expose
    @Nullable
    @SerializedName("id")
    public String id;

    @Override
    public void setRawObject(ISerializer serializer, JsonObject json) {
        // TODO Auto-generated method stub

    }
    final AdditionalDataManager manager = new AdditionalDataManager(this);

    @Override
    public AdditionalDataManager additionalDataManager() {
        return manager;
    }

}
