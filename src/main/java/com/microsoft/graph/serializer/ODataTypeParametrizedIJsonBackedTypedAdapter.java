package com.microsoft.graph.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.microsoft.graph.logger.ILogger;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * This adapter is responsible for deserialization of IJsonBackedObjects where service
 * returns one of several derived types of a base object, which is defined using the
 * odata.type parameter. If odata.type parameter is not found, the Gson default
 * (delegated) type adapter is used.
 */
class ODataTypeParametrizedIJsonBackedTypedAdapter extends TypeAdapter<IJsonBackedObject> {

    private final FallbackTypeAdapterFactory fallbackTypeAdapterFactory;
    private final Gson gson;
    private final TypeAdapter<IJsonBackedObject> delegatedAdapter;
    private final TypeToken<IJsonBackedObject> type;
    private final DerivedClassIdentifier derivedClassIdentifier;

    public ODataTypeParametrizedIJsonBackedTypedAdapter(FallbackTypeAdapterFactory fallbackTypeAdapterFactory, @Nonnull Gson gson,
        @Nonnull TypeAdapter<IJsonBackedObject> delegatedAdapter, @Nonnull final TypeToken<IJsonBackedObject> type, @Nonnull final ILogger logger)
    {
        super();
        this.fallbackTypeAdapterFactory = fallbackTypeAdapterFactory;
        this.gson = Objects.requireNonNull(gson, "parameter gson cannot be null");
        this.delegatedAdapter = Objects.requireNonNull(delegatedAdapter, "object delegated adapted cannot be null");
        this.type = Objects.requireNonNull(type, "object type cannot be null");
        this.derivedClassIdentifier = new DerivedClassIdentifier(logger);
    }

    @Override
    public void write(JsonWriter out, IJsonBackedObject value)
        throws IOException
    {
        this.delegatedAdapter.write(out, value);
    }

    @Override
    public IJsonBackedObject read(JsonReader in) {
        JsonElement jsonElement = Streams.parse(in);

        if (jsonElement.isJsonObject()) {
            final Class<?> derivedClass = derivedClassIdentifier.identify(jsonElement.getAsJsonObject(), type.getRawType());

            if (derivedClass != null) {
                final TypeAdapter<?> subTypeAdapter = gson.getDelegateAdapter(fallbackTypeAdapterFactory, TypeToken.get(derivedClass));
                return (IJsonBackedObject) subTypeAdapter.fromJsonTree(jsonElement);
            }
        }

        return delegatedAdapter.fromJsonTree(jsonElement);
    }
}
