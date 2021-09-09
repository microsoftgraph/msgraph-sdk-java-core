package com.microsoft.graph.serializer;

import java.math.BigDecimal;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.microsoft.graph.logger.ILogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Deserializer for native EDM types from the service. Used for actions and functions that return native types for example. */
public class EdmNativeTypeSerializer {
    /**
     * Deserializes native EDM types from the service. Used for actions and functions that return native types for example.
     * @param <T> Expected return type.
     * @param json json to deserialize.
     * @param type class of the expected return type.
     * @param logger logger to use.
     * @return the deserialized type or null.
     */
    @Nullable
    public static <T> T deserialize(@Nonnull final JsonElement json, @Nonnull final Class<T> type, @Nonnull final ILogger logger) {
        if (json == null || type == null) {
            return null;
        } else if(json.isJsonPrimitive()) {
            return getPrimitiveValue(json, type);
        } else if(json.isJsonObject()) {
            final JsonElement element = json.getAsJsonObject().get("@odata.null");
            if(element != null && element.isJsonPrimitive()) {
                return getPrimitiveValue(element, type);
            } else {
                return null;
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private static <T> T getPrimitiveValue(final JsonElement json, final Class<T> type) {
        if(type == Boolean.class) {
            return (T) Boolean.valueOf(json.getAsBoolean());
        } else if(type == String.class) {
            return (T)json.getAsString();
        } else if(type == Integer.class) {
            return (T) Integer.valueOf(json.getAsInt());
        } else if(type == UUID.class) {
            return (T) UUID.fromString(json.getAsString());
        } else if(type == Long.class) {
            return (T) Long.valueOf(json.getAsLong());
        } else if (type == Float.class) {
            return (T) Float.valueOf(json.getAsFloat());
        } else if (type == BigDecimal.class) {
            return (T) json.getAsBigDecimal();
        } else {
            return null;
        }
    }
}
