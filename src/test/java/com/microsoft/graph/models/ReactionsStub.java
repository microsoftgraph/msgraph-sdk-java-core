package com.microsoft.graph.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class shouldn't be moved as it's location in this
 * particular package is tightly coupled with the logic present at:
 * com/microsoft/graph/serializer/DerivedClassIdentifier.java:38
 */
public class ReactionsStub extends TestIJsonBackedObject {

    @SerializedName("reactions")
    @Expose
    @Nullable
    public List<ReactionStub> reactions;

}
