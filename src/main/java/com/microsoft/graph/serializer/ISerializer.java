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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Serializes and deserializes items from strings into their types
 */
public interface ISerializer {

    /**
     * Deserialize an object from the input string
     *
     * @param inputString the string that stores the representation of the item
     * @param clazz       the {@code Class} of the item to be deserialized
     * @param <T>         the type of the item to be deserialized
     * @return            the deserialized item from the input string
     */
    @Nullable
    default <T> T deserializeObject(@Nonnull final String inputString, @Nonnull final Class<T> clazz) {
        return deserializeObject(inputString, clazz, null);
    }

    /**
     * Deserialize an object from the input string
     *
     * @param inputString     the string that stores the representation of the item
     * @param clazz           the {@code Class} of the item to be deserialized
     * @param responseHeaders the HTTP response headers
     * @param <T>             the type of the item to be deserialized
     * @return                the deserialized item from the input string
     */
    @Nullable
    <T> T deserializeObject(@Nonnull final String inputString, @Nonnull final Class<T> clazz, @Nonnull final Map<String, List<String>> responseHeaders);

    /**
     * Deserialize an object from the input stream
     *
     * @param inputStream the stream that stores the representation of the item
     * @param clazz       the {@code Class} of the item to be deserialized
     * @param <T>         the type of the item to be deserialized
     * @return the deserialized item from the input string
     */
    @Nullable
    default <T> T deserializeObject(@Nonnull final InputStream inputStream, @Nonnull final Class<T> clazz) {
        return deserializeObject(inputStream, clazz, null);
    }

    /**
     * Deserialize an object from the input stream
     *
     * @param inputStream     the stream that stores the representation of the item
     * @param clazz           the {@code Class} of the item to be deserialized
     * @param responseHeaders the HTTP response headers
     * @param <T>             the type of the item to be deserialized
     * @return                the deserialized item from the input string
     */
    @Nullable
    <T> T deserializeObject(@Nonnull final InputStream inputStream, @Nonnull final Class<T> clazz, @Nonnull final Map<String, List<String>> responseHeaders);

    /**
     * Deserialize an object from the input JsonElement
     *
     * @param jsonElement     the {@code JsonElement} that stores the representation of the item
     * @param clazz           the {@code Class} of the item to be deserialized
     * @param <T>             the type of the item to be deserialized
     * @return the deserialized item from the input string
     */
    @Nullable
    default <T> T deserializeObject(@Nonnull JsonElement jsonElement, @Nonnull Class<T> clazz) {
        return deserializeObject(jsonElement, clazz, null);
    }

    /**
     * Deserialize an object from the input JsonElement
     *
     * @param jsonElement     the {@code JsonElement} that stores the representation of the item
     * @param clazz           the {@code Class} of the item to be deserialized
     * @param responseHeaders the HTTP response headers
     * @param <T>             the type of the item to be deserialized
     * @return                the deserialized item from the input string
     */
    @Nullable
    <T> T deserializeObject(@Nonnull JsonElement jsonElement, @Nonnull Class<T> clazz, @Nonnull Map<String, List<String>> responseHeaders);

    /**
     * Serializes an object into a string
     *
     * @param serializableObject the object to convert into a string
     * @param <T>                the type of the item to be serialized
     * @return                   the string representation of that item
     */
    @Nullable
    <T> String serializeObject(@Nonnull final T serializableObject);
}
