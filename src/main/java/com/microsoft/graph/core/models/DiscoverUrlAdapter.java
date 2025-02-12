package com.microsoft.graph.core.models;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import io.jsonwebtoken.JweHeader;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * DiscoverUrlAdapter class
 */
public class DiscoverUrlAdapter extends LocatorAdapter<Key> {

    /**
     * Key store
     */
    private final JwkProvider keyStore;

    /**
     * Constructor
     * @param keyDiscoveryUrl the JWKS endpoint to use to retrieve signing keys
     * @throws URISyntaxException if uri is invalid
     * @throws MalformedURLException if url is invalid
     */
    public DiscoverUrlAdapter(@Nonnull final String keyDiscoveryUrl)
            throws URISyntaxException, MalformedURLException {
        this.keyStore =
                new UrlJwkProvider(new URI(Objects.requireNonNull(keyDiscoveryUrl)).toURL());
    }

    @Override
    protected @Nullable Key locate(@Nonnull JwsHeader header) {
        Objects.requireNonNull(header);
        try {
            String keyId = header.getKeyId();
            Jwk publicKey = keyStore.get(keyId);
            return publicKey.getPublicKey();
        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not locate key", e);
        }
    }

    @Override
    protected @Nullable Key locate(@Nonnull JweHeader header) {
        return null;
    }

}
