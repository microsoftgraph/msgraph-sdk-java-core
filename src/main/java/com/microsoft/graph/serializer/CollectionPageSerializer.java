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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.http.BaseCollectionResponse;
import com.microsoft.graph.http.BaseRequestBuilder;
import com.microsoft.graph.logger.ILogger;

/** Specialized serializer to handle collection pages */
public class CollectionPageSerializer {

    private static DefaultSerializer serializer;
    /** length of the work "response" */
    private final static int responseLength = 8;
    /** length of the work "page" */
    private final static int pageLength = 4;

    /**
     * Not available for instantiation
     */
    private CollectionPageSerializer() {
    }

    /**
     * Serializes an CollectionPage
     *
     * @param src the CollectionPage variable for serialization
     * @param logger the logger
     * @param <T1> the entity type for the collection
     * @param <T2> the collection request builder interface type
     * @return       JsonElement of CollectionPage
     */
    @Nullable
    public static <T1, T2 extends BaseRequestBuilder<T1>> JsonElement serialize(@Nonnull final BaseCollectionPage<T1, T2> src, @Nonnull final ILogger logger) {
        if(src == null) {
            return null;
        }
        JsonArray jsonArray = new JsonArray();
        List<T1> items = src.getCurrentPage();
        serializer = new DefaultSerializer(logger);
        for(T1 item : items) {
            final String json = serializer.serializeObject(item);
            final JsonElement element = JsonParser.parseString(json);
            if(element != null && element.isJsonObject()) {
                final JsonObject jsonObject = element.getAsJsonObject();
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * Deserializes the JsonElement
     *
     * @param json the source CollectionPage's Json
     * @param typeOfT The type of the CollectionPage to deserialize to
     * @param logger the logger
     * @param <T1> the entity type for the collection
     * @param <T2> the collection request builder interface type
     * @throws JsonParseException the parse exception
     * @return    the deserialized CollectionPage
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T1, T2 extends BaseRequestBuilder<T1>> BaseCollectionPage<T1, T2> deserialize(@Nonnull final JsonElement json, @Nonnull final Type typeOfT, @Nonnull final ILogger logger) throws JsonParseException {
        if (json == null || !json.isJsonArray() || !typeOfT.getClass().equals(Class.class)) {
            return null;
        }
        serializer = new DefaultSerializer(logger);
        /** eg: com.microsoft.graph.requests.AttachmentCollectionPage */
        final String collectionPageClassCanonicalName = ((Class<?>)typeOfT).getName();
        try {
            /** eg: com.microsoft.graph.requests.AttachmentCollectionResponse */
            final String responseClassCanonicalName = collectionPageClassCanonicalName
                        .substring(0, collectionPageClassCanonicalName.length() - pageLength) + "Response";
            final Class<?> responseClass = Class.forName(responseClassCanonicalName);
            final JsonObject responseJson = new JsonObject();
            responseJson.add("value", json);
            final BaseCollectionResponse<T1> response = CollectionResponseSerializer.deserialize(responseJson, responseClass, logger);
            /** eg: com.microsoft.graph.requests.AttachmentCollectionRequestBuilder */
            final String responseBuilderCanonicalName = responseClassCanonicalName
                        .substring(0, responseClassCanonicalName.length() - responseLength) + "RequestBuilder";
            final Class<?> responseBuilderClass = Class.forName(responseBuilderCanonicalName);
            final Class<?> collectionPageClass = Class.forName(collectionPageClassCanonicalName);
            return (BaseCollectionPage<T1, T2>)collectionPageClass.getConstructor(responseClass, responseBuilderClass).newInstance(response, null);
        } catch(ClassNotFoundException ex) {
            logger.logError("Could not find class during deserialization", ex);
        } catch(NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
            logger.logError("Could not instanciate type during deserialization", ex);
        } catch(IllegalAccessException ex) {
            logger.logError("Unable to set field value during deserialization", ex);
        }
        return null;
    }
}
