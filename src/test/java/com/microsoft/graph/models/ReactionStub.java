package com.microsoft.graph.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * This class shouldn't be moved as it's location in this
 * particular package is tightly coupled with the logic present at:
 * com/microsoft/graph/serializer/DerivedClassIdentifier.java:38
 */
public class ReactionStub extends TestIJsonBackedObject {

    /**
     * the OData type of the object as returned by the service
     */
    @SerializedName("@odata.type")
    @Expose
    @Nullable
    public String oDataType;
}
