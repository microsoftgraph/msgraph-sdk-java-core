package com.microsoft.graph.serializer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonObject;
import com.microsoft.graph.logger.ILogger;

import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class provides methods to get the derived class corresponding to the OData type when deserializing payloads.
 */
public class DerivedClassIdentifier {

    private final static String ODATA_TYPE_KEY = "@odata.type";

    private final ILogger logger;

    /**
     * Creates a new instance of the dereived class identifier.
     *
     * @param logger The logger to use.
     */
    public DerivedClassIdentifier(@Nonnull ILogger logger) {
        this.logger = Objects.requireNonNull(logger, "logger parameter cannot be null");
        ;
    }

    /**
     * Get the derived class for the given JSON object
     * This covers scenarios in which the service may return one of several derived types
     * of a base object, which it defines using the odata.type parameter
     *
     * @param jsonObject  the raw JSON object of the response
     * @param parentClass the parent class the derived class should inherit from
     * @return the derived class if found, or null if not applicable
     */
    @Nullable
    public Class<?> identify(@Nonnull final JsonObject jsonObject, @Nullable final Class<?> parentClass) {
        Objects.requireNonNull(jsonObject, "parameter jsonObject cannot be null");
        //Identify the odata.type information if provided
        if (jsonObject.get(ODATA_TYPE_KEY) != null) {
            /** #microsoft.graph.user or #microsoft.graph.callrecords.callrecord */
            final String odataType = jsonObject.get(ODATA_TYPE_KEY).getAsString();
            final int lastDotIndex = odataType.lastIndexOf(".");
            final String derivedType = oDataTypeToClassName(odataType);
            try {
                Class<?> derivedClass = Class.forName(derivedType);
                //Check that the derived class inherits from the given parent class
                if (parentClass == null || parentClass.isAssignableFrom(derivedClass)) {
                    return derivedClass;
                }
                return null;
            } catch (ClassNotFoundException e) {
                logger.logDebug("Unable to find a corresponding class for derived type " + derivedType + ". Falling back to parent class.");
                //If we cannot determine the derived type to cast to, return null
                //This may happen if the API and the SDK are out of sync
                return null;
            }
        }
        //If there is no defined OData type, return null
        return null;
    }

    /**
     * Convert {@code @odata.type} to proper java class name
     *
     * @param odataType to convert
     * @return converted class name
     */
    @VisibleForTesting
    static String oDataTypeToClassName(@Nonnull String odataType) {
        Objects.requireNonNull(odataType);
        final int lastDotIndex = odataType.lastIndexOf(".");
        return (odataType.substring(0, lastDotIndex).toLowerCase(Locale.ROOT) +
            ".models." +
            CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, odataType.substring(lastDotIndex + 1)))
            .replace("#", "com.");
    }
}
