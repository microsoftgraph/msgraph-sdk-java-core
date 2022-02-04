package com.microsoft.graph.models;

import com.google.gson.JsonObject;
import com.microsoft.graph.serializer.AdditionalDataManager;
import com.microsoft.graph.serializer.IJsonBackedObject;
import com.microsoft.graph.serializer.ISerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mockito.Mockito.mock;

public abstract class TestIJsonBackedObject implements IJsonBackedObject {

    AdditionalDataManager additionalDataManager = mock(AdditionalDataManager.class);

    String rawObject;

    @Override
    public void setRawObject(@NotNull ISerializer serializer, @NotNull JsonObject json) {
        this.rawObject = json.toString();
    }

    @Nullable
    @Override
    @SuppressFBWarnings
    public AdditionalDataManager additionalDataManager() {
        return additionalDataManager;
    }

    public String getRawObject() {
        return rawObject;
    }
}
