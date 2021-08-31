// ------------------------------------------------------------------------------
// Copyright (c) 2017 Microsoft Corporation
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.graph.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/** Holds additional properties that are not part of the default object's schema */
public class AdditionalDataManager extends HashMap<String, JsonElement> {

    private static final long serialVersionUID = 8641634955796941429L;

    private final transient IJsonBackedObject jsonBackedObject;

    /**
     * Instanciates a new additional data manager from the json backed object
     *
     * @param jsonBackedObject the object to read values from
     */
    public AdditionalDataManager(@Nullable final IJsonBackedObject jsonBackedObject) {
        this.jsonBackedObject = jsonBackedObject;
    }

    /**
     * Populates the additional data object with the raw JSON from the
     * HTTP response
     *
     * @param json the raw JSON to set as additionalData
     */
    final void setAdditionalData(final JsonObject json) {
        // Get the names of all the fields on this object's hierarchy
        final Set<String> objectFields = getFields();

        // Get the keys on this JSON
        final Set<String> jsonKeys = getJsonKeys(json);

        // Get all keys present in JSON and *NOT* present in fields
        final Set<String> additionalDataKeys = new HashSet<>(jsonKeys);
        additionalDataKeys.removeAll(objectFields);

        // set the additionalData
        for (String jsonKey : additionalDataKeys) {
            put(jsonKey, json.get(jsonKey));
        }
    }

    private Set<String> getJsonKeys(final JsonObject json) {
        final Set<String> keys = new HashSet<>();
        final Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    private Set<String> getFields() {
        final Set<String> serializingFields = new HashSet<>();
        if(jsonBackedObject != null ) {
            final Field[] fields = jsonBackedObject.getClass().getFields();
            for (Field field : fields) {
                final SerializedName serializedName = field.getAnnotation(SerializedName.class);
                if (null != serializedName
                        && null != field.getAnnotation(Expose.class)) {
                    serializingFields.add(serializedName.value());
                }
            }
        }
        return serializingFields;
    }
}
