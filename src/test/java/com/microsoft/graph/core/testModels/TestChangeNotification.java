package com.microsoft.graph.core.testModels;

import com.microsoft.graph.core.models.EncryptedContentBearer;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import com.microsoft.kiota.store.BackedModel;
import com.microsoft.kiota.store.BackingStore;
import com.microsoft.kiota.store.BackingStoreFactorySingleton;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TestChangeNotification implements EncryptedContentBearer<TestChangeNotificationEncryptedContent>,  AdditionalDataHolder, BackedModel, Parsable {
    /**
     * Stores model information.
     */
    @jakarta.annotation.Nonnull
    protected BackingStore backingStore;
    /**
     * Instantiates a new {@link TestChangeNotification} and sets the default values.
     */
    public TestChangeNotification() {
        this.backingStore = BackingStoreFactorySingleton.instance.createBackingStore();
        this.setAdditionalData(new HashMap<>());
    }
    /**
     * Creates a new instance of the appropriate class based on discriminator value
     * @param parseNode The parse node to use to read the discriminator value and create the object
     * @return a {@link TestChangeNotification}
     */
    @jakarta.annotation.Nonnull
    public static TestChangeNotification createFromDiscriminatorValue(@jakarta.annotation.Nonnull final ParseNode parseNode) {
        Objects.requireNonNull(parseNode);
        return new TestChangeNotification();
    }
    /**
     * Gets the AdditionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
     * @return a {@link Map<String, Object>}
     */
    @jakarta.annotation.Nonnull
    public Map<String, Object> getAdditionalData() {
        Map<String, Object> value = this.backingStore.get("additionalData");
        if(value == null) {
            value = new HashMap<>();
            this.setAdditionalData(value);
        }
        return value;
    }
    /**
     * Gets the backingStore property value. Stores model information.
     * @return a {@link BackingStore}
     */
    @jakarta.annotation.Nonnull
    public BackingStore getBackingStore() {
        return this.backingStore;
    }
    /**
     * Gets the changeType property value. The changeType property
     * @return a {@link TestChangeType}
     */
    @jakarta.annotation.Nullable
    public TestChangeType getChangeType() {
        return this.backingStore.get("changeType");
    }
    /**
     * Gets the clientState property value. The clientState property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getClientState() {
        return this.backingStore.get("clientState");
    }
    /**
     * Gets the encryptedContent property value. The encryptedContent property
     * @return a {@link TestChangeNotificationEncryptedContent}
     */
    @jakarta.annotation.Nullable
    public TestChangeNotificationEncryptedContent getEncryptedContent() {
        return this.backingStore.get("encryptedContent");
    }
    /**
     * The deserialization information for the current model
     * @return a {@link Map<String, java.util.function.Consumer<ParseNode>>}
     */
    @jakarta.annotation.Nonnull
    public Map<String, java.util.function.Consumer<ParseNode>> getFieldDeserializers() {
        final HashMap<String, java.util.function.Consumer<ParseNode>> deserializerMap = new HashMap<String, java.util.function.Consumer<ParseNode>>(11);
        deserializerMap.put("changeType", (n) -> { this.setChangeType(n.getEnumValue(TestChangeType::forValue)); });
        deserializerMap.put("clientState", (n) -> { this.setClientState(n.getStringValue()); });
        deserializerMap.put("encryptedContent", (n) -> { this.setEncryptedContent(n.getObjectValue(TestChangeNotificationEncryptedContent::createFromDiscriminatorValue)); });
        deserializerMap.put("id", (n) -> { this.setId(n.getStringValue()); });
        deserializerMap.put("lifecycleEvent", (n) -> { this.setLifecycleEvent(n.getEnumValue(TestLifecycleEventType::forValue)); });
        deserializerMap.put("@odata.type", (n) -> { this.setOdataType(n.getStringValue()); });
        deserializerMap.put("resource", (n) -> { this.setResource(n.getStringValue()); });
        deserializerMap.put("resourceData", (n) -> { this.setResourceData(n.getObjectValue(TestResourceData::createFromDiscriminatorValue)); });
        deserializerMap.put("subscriptionExpirationDateTime", (n) -> { this.setSubscriptionExpirationDateTime(n.getOffsetDateTimeValue()); });
        deserializerMap.put("subscriptionId", (n) -> { this.setSubscriptionId(n.getStringValue()); });
        deserializerMap.put("tenantId", (n) -> { this.setTenantId(n.getStringValue()); });
        return deserializerMap;
    }
    /**
     * Gets the id property value. The id property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getId() {
        return this.backingStore.get("id");
    }
    /**
     * Gets the lifecycleEvent property value. The lifecycleEvent property
     * @return a {@link TestLifecycleEventType}
     */
    @jakarta.annotation.Nullable
    public TestLifecycleEventType getLifecycleEvent() {
        return this.backingStore.get("lifecycleEvent");
    }
    /**
     * Gets the @odata.type property value. The OdataType property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getOdataType() {
        return this.backingStore.get("odataType");
    }
    /**
     * Gets the resource property value. The resource property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getResource() {
        return this.backingStore.get("resource");
    }
    /**
     * Gets the resourceData property value. The resourceData property
     * @return a {@link TestResourceData}
     */
    @jakarta.annotation.Nullable
    public TestResourceData getResourceData() {
        return this.backingStore.get("resourceData");
    }
    /**
     * Gets the subscriptionExpirationDateTime property value. The subscriptionExpirationDateTime property
     * @return a {@link OffsetDateTime}
     */
    @jakarta.annotation.Nullable
    public OffsetDateTime getSubscriptionExpirationDateTime() {
        return this.backingStore.get("subscriptionExpirationDateTime");
    }
    /**
     * Gets the subscriptionId property value. The subscriptionId property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getSubscriptionId() {
        return this.backingStore.get("subscriptionId");
    }
    /**
     * Gets the tenantId property value. The tenantId property
     * @return a {@link String}
     */
    @jakarta.annotation.Nullable
    public String getTenantId() {
        return this.backingStore.get("tenantId");
    }
    /**
     * Serializes information the current object
     * @param writer Serialization writer to use to serialize this model
     */
    public void serialize(@jakarta.annotation.Nonnull final SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeEnumValue("changeType", this.getChangeType());
        writer.writeStringValue("clientState", this.getClientState());
        writer.writeObjectValue("encryptedContent", this.getEncryptedContent());
        writer.writeStringValue("id", this.getId());
        writer.writeEnumValue("lifecycleEvent", this.getLifecycleEvent());
        writer.writeStringValue("@odata.type", this.getOdataType());
        writer.writeStringValue("resource", this.getResource());
        writer.writeObjectValue("resourceData", this.getResourceData());
        writer.writeOffsetDateTimeValue("subscriptionExpirationDateTime", this.getSubscriptionExpirationDateTime());
        writer.writeStringValue("subscriptionId", this.getSubscriptionId());
        writer.writeStringValue("tenantId", this.getTenantId());
        writer.writeAdditionalData(this.getAdditionalData());
    }
    /**
     * Sets the AdditionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
     * @param value Value to set for the AdditionalData property.
     */
    public void setAdditionalData(@jakarta.annotation.Nullable final Map<String, Object> value) {
        this.backingStore.set("additionalData", value);
    }
    /**
     * Sets the backingStore property value. Stores model information.
     * @param value Value to set for the backingStore property.
     */
    public void setBackingStore(@jakarta.annotation.Nonnull final BackingStore value) {
        Objects.requireNonNull(value);
        this.backingStore = value;
    }
    /**
     * Sets the changeType property value. The changeType property
     * @param value Value to set for the changeType property.
     */
    public void setChangeType(@jakarta.annotation.Nullable final TestChangeType value) {
        this.backingStore.set("changeType", value);
    }
    /**
     * Sets the clientState property value. The clientState property
     * @param value Value to set for the clientState property.
     */
    public void setClientState(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("clientState", value);
    }
    /**
     * Sets the encryptedContent property value. The encryptedContent property
     * @param value Value to set for the encryptedContent property.
     */
    public void setEncryptedContent(@jakarta.annotation.Nullable final TestChangeNotificationEncryptedContent value) {
        this.backingStore.set("encryptedContent", value);
    }
    /**
     * Sets the id property value. The id property
     * @param value Value to set for the id property.
     */
    public void setId(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("id", value);
    }
    /**
     * Sets the lifecycleEvent property value. The lifecycleEvent property
     * @param value Value to set for the lifecycleEvent property.
     */
    public void setLifecycleEvent(@jakarta.annotation.Nullable final TestLifecycleEventType value) {
        this.backingStore.set("lifecycleEvent", value);
    }
    /**
     * Sets the @odata.type property value. The OdataType property
     * @param value Value to set for the @odata.type property.
     */
    public void setOdataType(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("odataType", value);
    }
    /**
     * Sets the resource property value. The resource property
     * @param value Value to set for the resource property.
     */
    public void setResource(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("resource", value);
    }
    /**
     * Sets the resourceData property value. The resourceData property
     * @param value Value to set for the resourceData property.
     */
    public void setResourceData(@jakarta.annotation.Nullable final TestResourceData value) {
        this.backingStore.set("resourceData", value);
    }
    /**
     * Sets the subscriptionExpirationDateTime property value. The subscriptionExpirationDateTime property
     * @param value Value to set for the subscriptionExpirationDateTime property.
     */
    public void setSubscriptionExpirationDateTime(@jakarta.annotation.Nullable final OffsetDateTime value) {
        this.backingStore.set("subscriptionExpirationDateTime", value);
    }
    /**
     * Sets the subscriptionId property value. The subscriptionId property
     * @param value Value to set for the subscriptionId property.
     */
    public void setSubscriptionId(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("subscriptionId", value);
    }
    /**
     * Sets the tenantId property value. The tenantId property
     * @param value Value to set for the tenantId property.
     */
    public void setTenantId(@jakarta.annotation.Nullable final String value) {
        this.backingStore.set("tenantId", value);
    }
}
