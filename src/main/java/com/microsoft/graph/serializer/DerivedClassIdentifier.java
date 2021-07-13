package com.microsoft.graph.serializer;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonObject;
import com.microsoft.graph.logger.ILogger;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DerivedClassIdentifier {

    private final static String ODATA_TYPE_KEY = "@odata.type";
    private final ILogger logger;

    public DerivedClassIdentifier(ILogger logger) {
        this.logger = logger;
    }

    /**
     * Get the derived class for the given JSON object
     * This covers scenarios in which the service may return one of several derived types
     * of a base object, which it defines using the odata.type parameter
     *
     * @param jsonObject  the raw JSON object of the response
     * @param parentClass the parent class the derived class should inherit from
     * @return			the derived class if found, or null if not applicable
     */
    @Nullable
    public Class<?> getDerivedClass(@Nonnull final JsonObject jsonObject, @Nullable final Class<?> parentClass) {
        Objects.requireNonNull(jsonObject, "parameter jsonObject cannot be null");
        //Identify the odata.type information if provided
        if (jsonObject.get(ODATA_TYPE_KEY) != null) {
            /** #microsoft.graph.user or #microsoft.graph.callrecords.callrecord */
            final String odataType = jsonObject.get(ODATA_TYPE_KEY).getAsString();
            final int lastDotIndex = odataType.lastIndexOf(".");
            final String derivedType = (odataType.substring(0, lastDotIndex) +
                ".models." +
                CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,
                    odataType.substring(lastDotIndex + 1)))
                .replace("#", "com.");
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
}
