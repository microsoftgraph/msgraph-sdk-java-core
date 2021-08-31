package com.microsoft.graph.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

import javax.annotation.Nullable;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.microsoft.graph.serializer.IJsonBackedObject;
import com.microsoft.graph.serializer.ISerializer;
import com.microsoft.graph.serializer.AdditionalDataManager;

/**
 * The basic collection response implementation
 *
 * @param <T> the entity or complex type
 */
public abstract class BaseCollectionResponse<T> implements ICollectionResponse<T> {

	/**
     * The list of items within this collection page
     */
	@SerializedName("value")
    @Expose
    @Nullable
	public java.util.List<T> value;


	/**
     * The list of items within this collection page
     */
    @Override
    @Nullable
    public java.util.List<T> values() {
        return value;
	}

    /** The link to the next page returned by the initial request if any. */
	@SerializedName("@odata.nextLink")
    @Expose(serialize = false)
    @Nullable
    public String nextLink;

    @Override
    @Nullable
    public String nextLink() {
        return nextLink;
    }

    private transient AdditionalDataManager additionalDataManager = new AdditionalDataManager(this);

    @Override
    @Nonnull
    public final AdditionalDataManager additionalDataManager() {
        return additionalDataManager;
    }
    private static final String VALUE_JSON_KEY = "value";
	/**
     * Sets the raw JSON object
     *
     * @param serializer the serializer
     * @param json the JSON object to set this object to
     */
    public void setRawObject(@Nonnull final ISerializer serializer, @Nonnull final JsonObject json) {
        Objects.requireNonNull(serializer, "parameter serializer cannot be null");
        Objects.requireNonNull(json, "parameter json cannot be null");
        if (json.has(VALUE_JSON_KEY) && value != null && !value.isEmpty()) {
            final JsonArray array = json.getAsJsonArray(VALUE_JSON_KEY);
            for (int i = 0; i < array.size() && i < value.size(); i++) {
				final Object targetObject = value.get(i);
				if(targetObject instanceof IJsonBackedObject && array.get(i).isJsonObject()) {
					((IJsonBackedObject)targetObject).setRawObject(serializer, array.get(i).getAsJsonObject());
				}
            }
        }
    }
}
