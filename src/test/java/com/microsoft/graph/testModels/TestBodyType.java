package com.microsoft.graph.testModels;

import com.microsoft.kiota.serialization.ValuedEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

public enum TestBodyType implements ValuedEnum {
    Text("Text"),
    Html("Html");
    public final String value;

    TestBodyType(@Nonnull final String value) {
        this.value = value;
    }

    @Nonnull
    @Override
    public String getValue() {
        return this.value;
    }

    @Nullable public static TestBodyType forValue(@Nullable final String searchValue) {
        Objects.requireNonNull(searchValue);
        switch(searchValue) {
            case "Text": return Text;
            case "Html": return Html;
            default: return null;
        }
    }

}
