package com.microsoft.graph.Requests;

import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.serialization.SerializationWriterFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressFBWarnings
public class BaseGraphRequestAdapter extends OkHttpRequestAdapter {

    public BaseGraphRequestAdapter(@Nonnull final AuthenticationProvider authenticationProvider, @Nullable final ParseNodeFactory parseNodeFactory, @Nullable final SerializationWriterFactory serializationWriterFactory, @Nullable final OkHttpClient client, @Nullable GraphClientOptions graphClientOptions) {
        super(authenticationProvider, parseNodeFactory, serializationWriterFactory, client != null ? client : GraphClientFactory.create(graphClientOptions).build());
    }
}
