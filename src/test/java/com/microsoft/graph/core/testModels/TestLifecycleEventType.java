package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.ValuedEnum;
import java.util.Objects;

public enum TestLifecycleEventType implements ValuedEnum {
    Missed("missed"),
    SubscriptionRemoved("subscriptionRemoved"),
    ReauthorizationRequired("reauthorizationRequired");
    public final String value;
    TestLifecycleEventType(final String value) {
        this.value = value;
    }
    @jakarta.annotation.Nonnull
    public String getValue() { return this.value; }
    @jakarta.annotation.Nullable
    public static TestLifecycleEventType forValue(@jakarta.annotation.Nonnull final String searchValue) {
        Objects.requireNonNull(searchValue);
        switch(searchValue) {
            case "missed": return Missed;
            case "subscriptionRemoved": return SubscriptionRemoved;
            case "reauthorizationRequired": return ReauthorizationRequired;
            default: return null;
        }
    }
}
