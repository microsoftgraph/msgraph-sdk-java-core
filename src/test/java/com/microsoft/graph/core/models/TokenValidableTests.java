package com.microsoft.graph.core.models;

import com.microsoft.graph.core.testModels.TestChangeNotification;
import com.microsoft.graph.core.testModels.TestChangeNotificationCollection;
import com.microsoft.graph.core.testModels.TestChangeNotificationEncryptedContent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TokenValidableTests {
    @Test
    void TestTokenValidWithNoValidationTokens() {
        var testChangeNotification = new TestChangeNotificationCollection ();
        var tenantIds = new ArrayList<UUID>();
        var appIds = new ArrayList<UUID>();
        var result = TokenValidable.areTokensValid(testChangeNotification,tenantIds,appIds);
        assertTrue(result);
    }

    @Test
    void TestTokenValidWithNoEncryptedData() {
        var testChangeNotificationCollection = new TestChangeNotificationCollection ();
        var testTokens = new ArrayList<String>();
        testTokens.add("testToken");
        testChangeNotificationCollection.setValidationTokens(testTokens);
        var testNotifications = new ArrayList<TestChangeNotification>();
        var testChangeNotification = new TestChangeNotification();
        testNotifications.add(testChangeNotification);
        testChangeNotificationCollection.setValue(testNotifications);
        var tenantIds = new ArrayList<UUID>();
        var appIds = new ArrayList<UUID>();
        var result = TokenValidable.areTokensValid(testChangeNotificationCollection,tenantIds,appIds);
        assertTrue(result); // no encrypted content
    }
    @Test
    void TestTokenValidWithEncryptedDataAndNoParameters() {
        var testChangeNotificationCollection = new TestChangeNotificationCollection ();
        var testTokens = new ArrayList<String>();
        testTokens.add("testToken");
        testChangeNotificationCollection.setValidationTokens(testTokens);
        var testNotifications = new ArrayList<TestChangeNotification>();
        var testChangeNotification = new TestChangeNotification();
        var testEncryptedContent = new TestChangeNotificationEncryptedContent();
        testChangeNotification.setEncryptedContent(testEncryptedContent);
        testNotifications.add(testChangeNotification);
        testChangeNotificationCollection.setValue(testNotifications);
        var tenantIds = new ArrayList<UUID>();
        var appIds = new ArrayList<UUID>();
        assertThrows(IllegalArgumentException.class,() -> TokenValidable.areTokensValid(testChangeNotificationCollection,tenantIds,appIds));
    }

    @Test
    void TestTokenValidWithEncryptedData() {
        var testChangeNotificationCollection = new TestChangeNotificationCollection ();
        var testTokens = new ArrayList<String>();
        testTokens.add("testToken");
        testChangeNotificationCollection.setValidationTokens(testTokens);
        var testNotifications = new ArrayList<TestChangeNotification>();
        var testChangeNotification = new TestChangeNotification();
        var testEncryptedContent = new TestChangeNotificationEncryptedContent();
        testChangeNotification.setEncryptedContent(testEncryptedContent);
        testNotifications.add(testChangeNotification);
        testChangeNotificationCollection.setValue(testNotifications);
        var tenantIds = new ArrayList<UUID>();
        tenantIds.add(UUID.randomUUID());
        var appIds = new ArrayList<UUID>();
        appIds.add(UUID.randomUUID());
        var exception = assertThrows(IllegalArgumentException.class,() -> TokenValidable.areTokensValid(testChangeNotificationCollection,tenantIds,appIds));
        assertEquals("Invalid token",exception.getMessage()); // issuer for the token is invalid
    }
}
