package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.ValuedEnum;
import java.util.Objects;

public enum TestChangeType implements ValuedEnum {
    Created("created"),
    Updated("updated"),
    Deleted("deleted");
    public final String value;
    TestChangeType(final String value) {
        this.value = value;
    }
    @jakarta.annotation.Nonnull
    public String getValue() { return this.value; }
    @jakarta.annotation.Nullable
    public static TestChangeType forValue(@jakarta.annotation.Nonnull final String searchValue) {
        Objects.requireNonNull(searchValue);
        switch(searchValue) {
            case "created": return Created;
            case "updated": return Updated;
            case "deleted": return Deleted;
            default: return null;
        }
    }
}
