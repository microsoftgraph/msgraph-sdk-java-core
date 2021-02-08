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

package com.microsoft.graph.core;

import com.google.gson.JsonElement;
import com.microsoft.graph.http.CoreHttpProvider;
import com.microsoft.graph.http.IHttpProvider;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.content.BatchRequestBuilder;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.serializer.DefaultSerializer;
import com.microsoft.graph.serializer.ISerializer;

import javax.annotation.Nullable;

import java.util.Objects;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

/**
 * A client that communications with an OData service
 */
public class BaseClient implements IBaseClient {
    /**
	 * Restricted constructor
	 */
	protected BaseClient() {
    }

    /**
     * The default endpoint for the Microsoft Graph Service
     */
    public static final String DEFAULT_GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0";

    /**
     * The current endpoint
     */
    private String endpoint;

    @Override
    @Nonnull
    public String getServiceRoot() {
        if (endpoint == null) {
            endpoint = DEFAULT_GRAPH_ENDPOINT;
        }
        return endpoint;
    }

    @Override
    public void setServiceRoot(@Nonnull final String value) {
        endpoint = value;
    }

	/**
	 * Send a custom request to Graph
	 *
	 * @param url
	 *			the full URL to make a request with
	 * @param responseType
	 *			the response class to deserialize the response into
	 * @return the instance of this builder
	 */
	@Nonnull
	public <T> CustomRequestBuilder<T> customRequest(@Nonnull final String url, @Nonnull final Class<T> responseType) {
		return new CustomRequestBuilder<T>(getServiceRoot() + url, this, null, responseType);
	}

	/**
	 * Send a custom request to Graph
	 *
	 * @param url
	 *			the full URL to make a request with
	 * @return the instance of this builder
	 */
	@Nonnull
	public CustomRequestBuilder<JsonElement> customRequest(@Nonnull final String url) {
		return new CustomRequestBuilder<JsonElement>(getServiceRoot() + url, this, null,
				JsonElement.class);
    }

    /**
     * Get the batch request builder.
     * @return a request builder to execute a batch.
     */
    @Nonnull
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(getServiceRoot() + "/$batch", this, null);
    }

	/**
	 * Gets the builder to start configuring the client
	 *
	 * @return builder to start configuring the client
	 */
	@Nonnull
	public static Builder<OkHttpClient> builder() {
		return new Builder<>();
	}

	/**
	 * Builder to help configure the Graph service client
     * @param <httpClientType> type of the native http library client
	 */
	public static class Builder<httpClientType> {
		private ISerializer serializer;
		private IHttpProvider httpProvider;
		private ILogger logger;
		private httpClientType httpClient;
		private IAuthenticationProvider auth;

		private IAuthenticationProvider getAuthenticationProvider() {
			if(auth == null) {
				throw new NullPointerException("auth");
			} else {
				return auth;
			}
		}
		private ILogger getLogger() {
			if(logger == null) {
				return new DefaultLogger();
			} else {
				return logger;
			}
		}
		private ISerializer getSerializer() {
			if(serializer == null) {
				return new DefaultSerializer(getLogger());
			} else {
				return serializer;
			}
        }
        @SuppressWarnings("unchecked")
		private httpClientType getHttpClient() {
			if(httpClient == null) {
				return (httpClientType)HttpClients.createDefault(getAuthenticationProvider());
			} else {
				return httpClient;
			}
		}
		private IHttpProvider getHttpProvider() {
			if(httpProvider == null) {
				return new CoreHttpProvider(getSerializer(), getLogger(), (OkHttpClient)getHttpClient());
			} else {
				return httpProvider;
			}
		}

		/**
		 * Sets the serializer.
		 *
		 * @param serializer
		 *			the serializer
		 * @return the instance of this builder
		 */
		@Nonnull
		public Builder<httpClientType> serializer(@Nonnull final ISerializer serializer) {
			checkNotNull(serializer, "serializer");
			this.serializer = serializer;
			return this;
		}

		/**
		 * Sets the httpProvider
		 *
		 * @param httpProvider
		 *			the httpProvider
		 * @return the instance of this builder
		 */
		@Nonnull
		public Builder<httpClientType> httpProvider(@Nonnull final IHttpProvider httpProvider) {
			checkNotNull(httpProvider, "httpProvider");
			this.httpProvider = httpProvider;
			return this;
		}

		/**
		 * Sets the logger
		 *
		 * @param logger
		 *			the logger
		 * @return the instance of this builder
		 */
		@Nonnull
		public Builder<httpClientType> logger(@Nonnull final ILogger logger) {
			checkNotNull(logger, "logger");
			this.logger = logger;
			return this;
		}

		/**
		 * Sets the http client
		 *
		 * @param client the http client
		 *
		 * @return the instance of this builder
		 */
		@Nonnull
		public Builder<httpClientType> httpClient(@Nonnull final httpClientType client) {
			checkNotNull(client, "client");
			this.httpClient = client;
			return this;
		}

		/**
		 * Sets the authentication provider
		 *
		 * @param auth the authentication provider
		 * @return the instance of this builder
		 */
		@Nonnull
		public Builder<httpClientType> authenticationProvider(@Nonnull final IAuthenticationProvider auth) {
			checkNotNull(auth, "auth");
			this.auth = auth;
			return this;
		}

		/**
		 * Builds and returns the Graph service client.
		 *
		 * @param instance the instance to set the information for
		 * @param <ClientType> the type of the client to return
		 * @return the Graph service client object
		 * @throws ClientException
		 *			 if there was an exception creating the client
		 */
		@Nonnull
		protected <ClientType extends BaseClient> ClientType buildClient(@Nonnull ClientType instance) throws ClientException {
            Objects.requireNonNull(instance, "The instance cannot be null");
			instance.setHttpProvider(this.getHttpProvider());
			instance.setLogger(this.getLogger());
			instance.setSerializer(this.getSerializer());
			return instance;
		}

		/**
		 * Builds and returns the Graph service client.
		 *
		 * @return the Graph service client object
		 * @throws ClientException
		 *			 if there was an exception creating the client
		 */
		@Nonnull
		public IBaseClient buildClient() throws ClientException {
			return buildClient(new BaseClient());
		}
	}

	/**
	 * Checks whether the provided object is null or not and throws an exception if it is
	 *
	 * @param o object to check
	 * @param name name to use in the exception message
	 */
	protected static void checkNotNull(@Nullable final Object o, @Nonnull final String name) {
		if (o==null) {
			throw new NullPointerException(name + " cannot be null");
		}
    }

    /**
     * The HTTP provider instance
     */
    private IHttpProvider httpProvider;

    /**
     * The logger
     */
    private ILogger logger;

    /**
     * The serializer instance
     */
    private ISerializer serializer;

    /**
     * Gets the HTTP provider
     *
     * @return The HTTP provider
     */
    @Override
    @Nullable
    public IHttpProvider getHttpProvider() {
        return httpProvider;
    }

    /**
     * Gets the logger
     *
     * @return The logger
     */
    @Nullable
    public ILogger getLogger() {
        return logger;
    }

    /**
     * Gets the serializer
     *
     * @return The serializer
     */
    @Override
    @Nullable
    public ISerializer getSerializer() {
        return serializer;
    }

    /**
     * Sets the logger
     *
     * @param logger The logger
     */
    protected void setLogger(@Nonnull final ILogger logger) {
        checkNotNull(logger, "logger");
        this.logger = logger;
    }

    /**
     * Sets the HTTP provider
     *
     * @param httpProvider The HTTP provider
     */
    protected void setHttpProvider(@Nonnull final IHttpProvider httpProvider) {
        checkNotNull(httpProvider, "httpProvider");
        this.httpProvider = httpProvider;
    }

    /**
     * Sets the serializer
     *
     * @param serializer The serializer
     */
    public void setSerializer(@Nonnull final ISerializer serializer) {
        checkNotNull(serializer, "serializer");
        this.serializer = serializer;
    }

    /**
     * Gets the service SDK version if the service SDK is in use, null otherwise
     * @return the service SDK version if the service SDK is in use, null otherwise
     */
    @Override
    @Nullable
    public String getServiceSDKVersion() {
        return null;
    }
}
