package com.microsoft.graph.core.models;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;

import jakarta.annotation.Nonnull;

/**
 * DecryptableContent interface
 */
public interface DecryptableContent {

    /**
     * Sets the data
     * @param data resource data
     */
    public void setData(String data);
    /**
     * Gets the data
     * @return the data
     */
    public String getData();
    /**
     * Sets the data key
     * @param dataKey asymmetric key used to sign data
     */
    public void setDataKey(String dataKey);
    /**
     * Gets the data key
     * @return the data key
     */
    public String getDataKey();

    /**
     * Sets the data signature
     * @param signature signature of the data
     */
    public void setDataSignature(String signature);
    /**
     * Gets the data signature
     * @return data signature
     */
    public String getDataSignature();
    /**
     * Sets the encryption certificate id
     * @param encryptionCertificateId certificate Id used when subscribing
     */
    public void setEncryptionCertificateId(String encryptionCertificateId);
    /**
     * Gets the encryption certificate id
     * @return the encryption certificate id
     */
    public String getEncryptionCertificateId();
    /**
     * Sets the encryption certificate thumbprint
     * @param encryptionCertificateThumbprint certificate thumbprint
     */
    public void setEncryptionCertificateThumbprint(String encryptionCertificateThumbprint);
    /**
     * Gets the encryption certificate thumbprint
     * @return the encryption certificate thumbprint
     */
    public String getEncryptionCertificateThumbprint();

    /**
     * Validates the signature of the resource data, decrypts resource data and deserializes the data to a Parsable
     * https://learn.microsoft.com/en-us/graph/change-notifications-with-resource-data?tabs=csharp#decrypting-resource-data-from-change-notifications
     *
     * @param <T> Parsable type to return
     * @param decryptableContent instance of DecryptableContent
     * @param certificateKeyProvider provides an RSA Private Key for the certificate provided when subscribing
     * @param factory ParsableFactory for the return type
     * @return decrypted resource data
     * @throws Exception if an error occurs while decrypting the data
     */
    public static <T extends Parsable> T decrypt(@Nonnull final DecryptableContent decryptableContent, @Nonnull final CertificateKeyProvider certificateKeyProvider, @Nonnull final ParsableFactory<T> factory) throws Exception {
        Objects.requireNonNull(certificateKeyProvider);
        final String decryptedContent = decryptAsString(decryptableContent, certificateKeyProvider);
        final ParseNode rootParseNode = ParseNodeFactoryRegistry.defaultInstance.getParseNode(
            "application/json", new ByteArrayInputStream(decryptedContent.getBytes(StandardCharsets.UTF_8)));
        return rootParseNode.getObjectValue(factory);
    }

    /**
     * Validates the signature and decrypts resource data attached to the notification.
     * https://learn.microsoft.com/en-us/graph/change-notifications-with-resource-data?tabs=csharp#decrypting-resource-data-from-change-notifications
     *
     * @param content instance of DecryptableContent
     * @param certificateKeyProvider provides an RSA Private Key for the certificate provided when subscribing
     * @return decrypted resource data
     * @throws Exception if an error occurs while decrypting the data
     */
    public static String decryptAsString(@Nonnull final DecryptableContent content, @Nonnull final CertificateKeyProvider certificateKeyProvider) throws Exception {
        Objects.requireNonNull(certificateKeyProvider);
        final Key privateKey = certificateKeyProvider.getCertificateKey(content.getEncryptionCertificateId(), content.getEncryptionCertificateThumbprint());
        final Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        final byte[] decryptedSymmetricKey = cipher.doFinal(Base64.getDecoder().decode(content.getDataKey()));

        final Mac sha256Mac = Mac.getInstance("HmacSHA256");
        sha256Mac.init(new SecretKeySpec(decryptedSymmetricKey, "HmacSHA256"));
        final byte[] hashedData = sha256Mac.doFinal(Base64.getDecoder().decode(content.getData()));

        final String expectedSignature = Base64.getEncoder().encodeToString(hashedData);
        if (!expectedSignature.equals(content.getDataSignature())) {
            throw new Exception("Signature does not match");
        }
        return new String(aesDecrypt(Base64.getDecoder().decode(content.getData()), decryptedSymmetricKey), StandardCharsets.UTF_8);
    }

    /**
     * Decrypts the resource data using the decrypted symmetric key
     * @param data Base-64 decoded resource data
     * @param key Decrypted symmetric key from DecryptableContent.getDataKey()
     * @return decrypted resource data
     * @throws Exception if an error occurs while decrypting the data
     */
    public static byte[] aesDecrypt(byte[] data, byte[] key) throws Exception {
        try {
            @SuppressWarnings("java:S3329")
            // Sonar warns that a random IV should be used for encryption
            // but we are decrypting here.
            final IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(key, 16));
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error occurred while trying to decrypt the data", ex);
        }
    }

    /**
     * Provides a private key for the certificate with the ID provided when creating the
     * subscription and the thumbprint.
     */
    @FunctionalInterface
    public interface CertificateKeyProvider {
        /**
         * Returns the private key for an X.509 certificate with the given id and thumbprint
         * @param certificateId certificate Id provided when subscribing
         * @param certificateThumbprint certificate thumbprint
         * @return Private key used to sign the certificate
         */
        public Key getCertificateKey(String certificateId, String certificateThumbprint);
    }
}
