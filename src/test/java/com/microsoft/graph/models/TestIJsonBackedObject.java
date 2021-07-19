package com.microsoft.graph.models;

import com.google.gson.JsonObject;
import com.microsoft.graph.serializer.AdditionalDataManager;
import com.microsoft.graph.serializer.IJsonBackedObject;
import com.microsoft.graph.serializer.ISerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mockito.Mockito.mock;

public abstract class TestIJsonBackedObject implements IJsonBackedObject {

    @Override
    public void setRawObject(@NotNull ISerializer serializer, @NotNull JsonObject json) {
        // Do nothing
    }

    @Nullable
    @Override
    public AdditionalDataManager additionalDataManager() {
        return mock(AdditionalDataManager.class);
    }
}
