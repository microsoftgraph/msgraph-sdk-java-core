package com.microsoft.graph.core.models;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * EncryptableSubscription interface
 */
public interface EncryptableSubscription {

    /**
     * Sets the encryption certificate
     * @param certificate Base-64 encoded certificate to be used by Microsoft Graph to encrypt resource data
     */
    public void setEncryptionCertificate(@Nullable final String certificate);

    /**
     * Returns the encryption certificate
     * @return encryption certificate
     */
    public @Nullable String getEncryptionCertificate();

    /**
     * Converts an X.509 Certificate object to Base-64 string and adds to the encryptableSubscription provided
     * @param subscription encryptable subscription
     * @param certificate X.509 Certificate
     * @throws CertificateEncodingException if the certificate cannot be encoded
     */
    public static void addPublicEncryptionCertificate(@Nonnull final EncryptableSubscription subscription, @Nonnull final X509Certificate certificate) throws CertificateEncodingException {
        Objects.requireNonNull(subscription);
        Objects.requireNonNull(certificate);
        subscription.setEncryptionCertificate(
            Base64.getEncoder().encodeToString(certificate.getEncoded())
        );
    }

}
