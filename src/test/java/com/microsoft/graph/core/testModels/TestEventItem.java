package com.microsoft.graph.core.testModels;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;

import jakarta.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

public class TestEventItem implements Parsable, AdditionalDataHolder {
    private Boolean allowNewTimeProposals;
    private String bodyPreview;
    private Boolean hasAttachments;
    private Boolean hideAttendees;
    private String iCalUId;
    public List<TestEventItem> instances;
    private Boolean isAllDay;
    private Boolean isCancelled;
    private Boolean isDraft;
    private Boolean isOnlineMeeting;
    private Boolean isOrganizer;
    private Boolean isReminderOn;
    private String onlineMeetingUrl;
    private String originalEndTimeZone;
    private OffsetDateTime originalStart;
    private String originalStartTimeZone;
    private Integer reminderMinutesBeforeStart;
    private Boolean responseRequested;
    private String seriesMasterId;
    private String subject;
    private String transactionId;
    private String webLink;
    public Map<String, Object> additionalData;

    public Boolean getAllowNewTimeProposals() {
        return allowNewTimeProposals;
    }

    public void setAllowNewTimeProposals(Boolean allowNewTimeProposals) {
        this.allowNewTimeProposals = allowNewTimeProposals;
    }

    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHideAttendees() {
        return hideAttendees;
    }

    public void setHideAttendees(Boolean hideAttendees) {
        this.hideAttendees = hideAttendees;
    }

    public String getiCalUId() {
        return iCalUId;
    }

    public void setiCalUId(String iCalUId) {
        this.iCalUId = iCalUId;
    }

    public List<TestEventItem> getInstances() {
        return this.instances;
    }

    public void setInstances(List<TestEventItem> instances) {
        this.instances = new ArrayList<>(instances);
    }

    public Boolean getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(Boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public Boolean getIsOnlineMeeting() {
        return isOnlineMeeting;
    }

    public void setIsOnlineMeeting(Boolean isOnlineMeeting) {
        this.isOnlineMeeting = isOnlineMeeting;
    }

    public Boolean getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsOrganizer(Boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public Boolean getIsReminderOn() {
        return isReminderOn;
    }

    public void setIsReminderOn(Boolean isReminderOn) {
        this.isReminderOn = isReminderOn;
    }

    public String getOnlineMeetingUrl() {
        return onlineMeetingUrl;
    }

    public void setOnlineMeetingUrl(String onlineMeetingUrl) {
        this.onlineMeetingUrl = onlineMeetingUrl;
    }

    public String getOriginalEndTimeZone() {
        return originalEndTimeZone;
    }

    public void setOriginalEndTimeZone(String originalEndTimeZone) {
        this.originalEndTimeZone = originalEndTimeZone;
    }

    public OffsetDateTime getOriginalStart() {
        return originalStart;
    }

    public void setOriginalStart(OffsetDateTime originalStart) {
        this.originalStart = originalStart;
    }

    public String getOriginalStartTimeZone() {
        return originalStartTimeZone;
    }

    public void setOriginalStartTimeZone(String originalStartTimeZone) {
        this.originalStartTimeZone = originalStartTimeZone;
    }

    public Integer getReminderMinutesBeforeStart() {
        return reminderMinutesBeforeStart;
    }

    public void setReminderMinutesBeforeStart(Integer reminderMinutesBeforeStart) {
        this.reminderMinutesBeforeStart = reminderMinutesBeforeStart;
    }

    public Boolean getResponseRequested() {
        return responseRequested;
    }

    public void setResponseRequested(Boolean responseRequested) {
        this.responseRequested = responseRequested;
    }

    public String getSeriesMasterId() {
        return seriesMasterId;
    }

    public void setSeriesMasterId(String seriesMasterId) {
        this.seriesMasterId = seriesMasterId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalData() {
        return this.additionalData;
    }
    public void setAdditionalData (@Nonnull Map<String, Object> additionalData) {
        this.additionalData = new HashMap<>(additionalData);
    }

    @Nonnull
    public Map<String, Consumer<ParseNode>> getFieldDeserializers() {
        HashMap<String, Consumer< ParseNode >> fieldDeserializers = new HashMap<>();
        fieldDeserializers.put("allowNewTimeProposals", (n) -> setAllowNewTimeProposals(n.getBooleanValue()));
        fieldDeserializers.put("bodyPreview", (n) -> setBodyPreview(n.getStringValue()));
        fieldDeserializers.put("hasAttachments", (n) -> setHasAttachments(n.getBooleanValue()));
        fieldDeserializers.put("hideAttendees", (n) -> setHideAttendees(n.getBooleanValue()));
        fieldDeserializers.put("iCalUId", (n) -> setiCalUId(n.getStringValue()));
        fieldDeserializers.put("instances", (n) -> setInstances(n.getCollectionOfObjectValues(TestEventItem::createFromDiscriminatorValue)));
        fieldDeserializers.put("isAllDay", (n) -> setIsAllDay(n.getBooleanValue()));
        fieldDeserializers.put("isCancelled", (n) -> setIsCancelled(n.getBooleanValue()));
        fieldDeserializers.put("isDraft", (n) -> setIsDraft(n.getBooleanValue()));
        fieldDeserializers.put("isOnlineMeeting", (n) -> setIsOnlineMeeting(n.getBooleanValue()));
        fieldDeserializers.put("isOrganizer", (n) -> setIsOrganizer(n.getBooleanValue()));
        fieldDeserializers.put("isReminderOn", (n) -> setIsReminderOn(n.getBooleanValue()));
        fieldDeserializers.put("onlineMeetingUrl", (n) -> setOnlineMeetingUrl(n.getStringValue()));
        fieldDeserializers.put("originalEndTimeZone", (n) -> setOriginalEndTimeZone(n.getStringValue()));
        fieldDeserializers.put("originalStart", (n) -> setOriginalStart(n.getOffsetDateTimeValue()));
        fieldDeserializers.put("originalStartTimeZone", (n) -> setOriginalStartTimeZone(n.getStringValue()));
        fieldDeserializers.put("reminderMinutesBeforeStart", (n) -> setReminderMinutesBeforeStart(n.getIntegerValue()));
        fieldDeserializers.put("responseRequested", (n) -> setResponseRequested(n.getBooleanValue()));
        fieldDeserializers.put("seriesMasterId", (n) -> setSeriesMasterId(n.getStringValue()));
        fieldDeserializers.put("subject", (n) -> setSubject(n.getStringValue()));
        fieldDeserializers.put("transactionId", (n) -> setTransactionId(n.getStringValue()));
        fieldDeserializers.put("webLink", (n) -> setWebLink(n.getStringValue()));
        return fieldDeserializers;
    }

    public void serialize(@Nonnull SerializationWriter writer) {
        Objects.requireNonNull(writer);
        writer.writeBooleanValue("allowNewTimeProposals", getAllowNewTimeProposals());
        writer.writeStringValue("bodyPreview", getBodyPreview());
        writer.writeBooleanValue("hasAttachments", getHasAttachments());
        writer.writeBooleanValue("hideAttendees", getHideAttendees());
        writer.writeStringValue("iCalUId", getiCalUId());
        writer.writeCollectionOfObjectValues("instances", getInstances());
        writer.writeBooleanValue("isAllDay", getIsAllDay());
        writer.writeBooleanValue("isCancelled", getIsCancelled());
        writer.writeBooleanValue("isDraft", getIsDraft());
        writer.writeBooleanValue("isOnlineMeeting", getIsOnlineMeeting());
        writer.writeBooleanValue("isOrganizer", getIsOrganizer());
        writer.writeBooleanValue("isReminderOn", getIsReminderOn());
        writer.writeStringValue("onlineMeetingUrl", getOnlineMeetingUrl());
        writer.writeStringValue("originalEndTimeZone", getOriginalEndTimeZone());
        writer.writeOffsetDateTimeValue("originalStart", getOriginalStart());
        writer.writeStringValue("originalStartTimeZone",getOriginalStartTimeZone());
        writer.writeIntegerValue("reminderMinutesBeforeStart", getReminderMinutesBeforeStart());
        writer.writeBooleanValue("responseRequested", getResponseRequested());
        writer.writeStringValue("seriesMasterId", getSeriesMasterId());
        writer.writeStringValue("subject", getSubject());
        writer.writeStringValue("transactionId", getTransactionId());
        writer.writeStringValue("webLink", getWebLink());
    }

    public static TestEventItem createFromDiscriminatorValue(ParseNode parseNode) {
        if (parseNode == null) {
            throw new IllegalArgumentException("parseNode");
        }
        return new TestEventItem();
    }
}
