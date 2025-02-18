package com.microsoft.graph.core.models;

/**
 * Contains Decryptable content
 * @param <T> The type of the decryptable content
 */
public interface EncryptedContentBearer<T extends DecryptableContent> {

    /**
     * Sets encrypted content
     * @param encryptedContent encrypted content
     */
    public void setEncryptedContent(T encryptedContent);

    /**
     * Return encrypted content
     * @return encrypted content
     */
    public T getEncryptedContent();

}
