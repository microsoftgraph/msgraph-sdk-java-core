package com.microsoft.graph.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class shouldn't be moved as it's location in this
 * particular package is tightly coupled with the logic present at:
 * com/microsoft/graph/serializer/DerivedClassIdentifier.java:38
 */
public class MessageStub extends TestIJsonBackedObject {

    @SerializedName("name")
    @Expose()
    public String name;

    @SerializedName("reaction")
    @Expose()
    public ReactionStub reaction;
}
