package com.microsoft.graph.core.models;

import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nonnull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;

/**
 * TokenValidable interface
 */
public interface TokenValidable<U extends DecryptableContent, T extends EncryptedContentBearer<U>> {

    /**
     * Graph notification publisher. Ensures that a different app that isn't Microsoft Graph did not send the change notifications
     */
    public static final String graphNotificationPublisher = "0bf30f3b-4a52-48df-9a82-234910c4a086";

    /**
     * Sets collection of validation tokens
     * @param validationTokens tokens
     */
    public void setValidationTokens(List<String> validationTokens);

    /**
     * Returns validation tokens
     * @return list of tokens
     */
    public List<String> getValidationTokens();

    /**
     * Sets collection of encrypted token bearers
     * @param value collection of encrypted token bearers
     */
    public void setValue(List<T> value);

    /**
     * Get collection of encrypted token bearers
     * @return encrypted token bearers
     */
    public List<T> getValue();

    /**
     * Validates the tokens
     * @param <U> DecryptableContent
     * @param <T> EncryptedContentBearer
     * @param collection collection of encrypted token bearers
     * @param tenantIds tenant ids
     * @param appIds app ids
     * @param keyDiscoveryUrl the JWKS endpoint to use to retrieve signing keys
     * @return true if the tokens are valid
     * @throws IllegalArgumentException if one of the tokens are invalid
     */
    public static <U extends DecryptableContent, T extends EncryptedContentBearer<U>>
        boolean areTokensValid(
            @Nonnull final TokenValidable<U,T> collection,
            @Nonnull final List<UUID> tenantIds,
            @Nonnull final List<UUID> appIds,
            @Nonnull final String keyDiscoveryUrl) {

        Objects.requireNonNull(collection);
        Objects.requireNonNull(tenantIds);
        Objects.requireNonNull(appIds);
        Objects.requireNonNull(keyDiscoveryUrl);

        if (collection.getValidationTokens().isEmpty()
                || collection.getValue().stream().allMatch(x -> x.getEncryptedContent() == null)) {
            return true;
        }

        if (tenantIds.isEmpty() || appIds.isEmpty()) {
            throw new IllegalArgumentException("tenantIds, appIds and issuer formats must be provided");
        }

        for (final String token : collection.getValidationTokens()) {
            if (!isTokenValid(token, tenantIds, appIds, keyDiscoveryUrl)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the tokens
     * @param <U> DecryptableContent
     * @param <T> EncryptedContentBearer
     * @param collection collection of encrypted token bearers
     * @param tenantIds tenant ids
     * @param appIds app ids
     * @return true if the tokens are valid
     */
    public static <U extends DecryptableContent, T extends EncryptedContentBearer<U>>
        boolean areTokensValid(
            @Nonnull final TokenValidable<U,T> collection,
            @Nonnull final List<UUID> tenantIds,
            @Nonnull final List<UUID> appIds) {

        final String keyDiscoveryUrl = "https://login.microsoftonline.com/common/discovery/keys";
        return areTokensValid(collection, tenantIds, appIds, keyDiscoveryUrl);
    }

    /**
     * Validates the token
     * @param <U> DecryptableContent
     * @param <T> EncryptedContentBearer
     * @param token token
     * @param tenantIds tenant ids
     * @param appIds app ids
     * @param keyDiscoveryUrl the JWKS endpoint to use to retrieve signing keys
     * @return true if the token is valid
     * @throws IllegalArgumentException if the token is invalid
     */
    public static <U extends DecryptableContent, T extends EncryptedContentBearer<U>>
        boolean isTokenValid(
            @Nonnull final String token,
            @Nonnull final List<UUID> tenantIds,
            @Nonnull final List<UUID> appIds,
            @Nonnull final String keyDiscoveryUrl) {

        Objects.requireNonNull(token);
        Objects.requireNonNull(tenantIds);
        Objects.requireNonNull(appIds);
        Objects.requireNonNull(keyDiscoveryUrl);

        if (tenantIds.isEmpty() || appIds.isEmpty()) {
            throw new IllegalArgumentException("tenantIds, appIds and issuer formats must be provided");
        }

        try {
            Locator<Key> discoverUrlAdapter = new DiscoverUrlAdapter(keyDiscoveryUrl);
            // As part of this process, the signature is validated
            // This throws if the signature is invalid
            Jws<Claims> parsedToken = Jwts.parser().keyLocator(discoverUrlAdapter).build().parseSignedClaims(token);

            Claims body = parsedToken.getPayload();

            if (body.getExpiration().before(new java.util.Date())) {
                throw new IllegalArgumentException("Token is expired");
            }

            String issuer = body.getIssuer();
            Set<String> audience = body.getAudience();

            boolean isAudienceValid = false;
            for (final UUID appId : appIds) {
                if (audience.contains(appId.toString())) {
                    isAudienceValid = true;
                    break;
                }
            }

            boolean isIssuerValid = false;
            for (final UUID tenantId : tenantIds) {
                if (issuer.contains(tenantId.toString())) {
                    isIssuerValid = true;
                    break;
                }
            }

            if (!body.get("azp", String.class).equals(graphNotificationPublisher)) {
                throw new IllegalArgumentException("Invalid token publisher. Expected Graph notification publisher (azp): " + graphNotificationPublisher);
            }

            return isAudienceValid && isIssuerValid;

        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

    }

}
