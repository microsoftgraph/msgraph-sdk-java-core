package com.microsoft.graph.core.models;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
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
     * @param privateKeyProvider provides an RSA Private Key for the certificate provided when subscribing
     * @param factory ParsableFactory for the return type
     * @return
     * @throws Exception
     */
    public static <T extends Parsable> T decrypt(@Nonnull final DecryptableContent decryptableContent, @Nonnull final PrivateKeyProvider privateKeyProvider, @Nonnull final ParsableFactory<T> factory) throws Exception {
        Objects.requireNonNull(privateKeyProvider);
        final String decryptedContent = decryptAsString(decryptableContent, privateKeyProvider);
        final ParseNode rootParseNode = ParseNodeFactoryRegistry.defaultInstance.getParseNode(
            "application/json", new ByteArrayInputStream(decryptedContent.getBytes(StandardCharsets.UTF_8)));
        return rootParseNode.getObjectValue(factory);
    }

    /**
     * Validates the signature and decrypts resource data attached to the notification.
     * https://learn.microsoft.com/en-us/graph/change-notifications-with-resource-data?tabs=csharp#decrypting-resource-data-from-change-notifications
     *
     * @param content instance of DecryptableContent
     * @param privateKeyProvider provides an RSA Private Key for the certificate provided when subscribing
     * @return decrypted resource data
     * @throws Exception
     */
    public static String decryptAsString(@Nonnull final DecryptableContent content, @Nonnull final PrivateKeyProvider privateKeyProvider) throws Exception {
        Objects.requireNonNull(privateKeyProvider);
        final RSAPrivateKey privateKey = privateKeyProvider.getCertificatePrivateKey(content.getEncryptionCertificateId(), content.getEncryptionCertificateThumbprint());
        final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
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
     * @throws Exception
     */
    public static byte[] aesDecrypt(byte[] data, byte[] key) throws Exception {
        try {
            final IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOf(key, 16));
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error occurred while trying to decrypt the data", ex);
        }
    }

    /**
     * Provides an RSA Private Key for the certificate with the ID provided when creating the
     * subscription and the thumbprint.
     */
    @FunctionalInterface
    public interface PrivateKeyProvider {
        /**
         * Returns the RSAPrivateKey for an X.509 certificate with the given id and thumbprint
         * @param certificateId certificate Id provided when subscribing
         * @param certtificateThumbprint certificate thumbprint
         * @return RSA private key used to sign the certificate
         */
        public RSAPrivateKey getCertificatePrivateKey(String certificateId, String certtificateThumbprint);
    }
}
