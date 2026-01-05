package com.github.pmoerenhout.jsmpp.web.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pmoerenhout.jsmpp.web.Util;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "smout")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmOut implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty
  private Long id;

  @Column(name = "timestamp")
  @JsonProperty
  private Instant timestamp;

  @Column(name = "operationid", columnDefinition = "VARBINARY(16)", updatable = false)
  @JsonProperty
  private UUID operationId;

  @Column(name = "user", updatable = false)
  @JsonProperty
  private String user;

  @Column(name = "connectionid")
  @JsonProperty
  private String connectionId;

  @Column(name = "servicetype", updatable = false)
  @JsonProperty
  private String serviceType;

  @Column(name = "source", updatable = false)
  @JsonProperty
  private String source;

  @Column(name = "sourceton", updatable = false)
  @JsonProperty
  private byte sourceTon;

  @Column(name = "sourcenpi", updatable = false)
  @JsonProperty
  private byte sourceNpi;

  @Column(name = "destination", updatable = false)
  @JsonProperty
  private String destination;

  @Column(name = "destinationton", updatable = false)
  @JsonProperty
  private byte destinationTon;

  @Column(name = "destinationnpi", updatable = false)
  @JsonProperty
  private byte destinationNpi;

  @Column(name = "esmclass", updatable = false)
  @JsonProperty
  private byte esmClass;

  @Column(name = "dcs", updatable = false)
  @JsonProperty
  private byte dataCodingScheme;

  @Column(name = "pid", updatable = false)
  @JsonProperty
  private byte protocolIdentifier;

  @Column(name = "priority", updatable = false)
  @JsonProperty
  private byte priorityFlag;

  @Column(name = "scheduled", updatable = false)
  @JsonProperty
  private String scheduled;

  @Column(name = "validityperiod", updatable = false)
  @JsonProperty
  private String validityPeriod;

  @Column(name = "replaceifpresent", updatable = false)
  @JsonProperty
  private byte replaceIfPresentFlag;

  @Column(name = "shortmessage", updatable = false, columnDefinition = "blob")
  @JsonProperty
  private byte[] shortMessage;

  @Column(name = "messageid")
  @JsonProperty
  private String messageId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "messageid", referencedColumnName = "messageid", insertable = false, updatable = false),
      @JoinColumn(name = "destination", referencedColumnName = "source", insertable = false, updatable = false)
  })
  @JsonProperty
  private Dr dr;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(final String connectionId) {
    this.connectionId = connectionId;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = timestamp;
  }

  public UUID getOperationId() {
    return operationId;
  }

  public void setOperationId(final UUID operationId) {
    this.operationId = operationId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(final String user) {
    this.user = user;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(final String serviceType) {
    this.serviceType = serviceType;
  }

  public String getSource() {
    return source;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  public byte getSourceTon() {
    return sourceTon;
  }

  public void setSourceTon(final byte sourceTon) {
    this.sourceTon = sourceTon;
  }

  public byte getSourceNpi() {
    return sourceNpi;
  }

  public void setSourceNpi(final byte sourceNpi) {
    this.sourceNpi = sourceNpi;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(final String destination) {
    this.destination = destination;
  }

  public byte getDestinationTon() {
    return destinationTon;
  }

  public void setDestinationTon(final byte destinationTon) {
    this.destinationTon = destinationTon;
  }

  public byte getDestinationNpi() {
    return destinationNpi;
  }

  public void setDestinationNpi(final byte destinationNpi) {
    this.destinationNpi = destinationNpi;
  }

  public byte getEsmClass() {
    return esmClass;
  }

  public void setEsmClass(final byte esmClass) {
    this.esmClass = esmClass;
  }

  public byte getDataCodingScheme() {
    return dataCodingScheme;
  }

  public void setDataCodingScheme(final byte dataCodingScheme) {
    this.dataCodingScheme = dataCodingScheme;
  }

  public byte getProtocolIdentifier() {
    return protocolIdentifier;
  }

  public void setProtocolIdentifier(final byte protocolIdentifier) {
    this.protocolIdentifier = protocolIdentifier;
  }

  public byte getPriorityFlag() {
    return priorityFlag;
  }

  public void setPriorityFlag(final byte priorityFlag) {
    this.priorityFlag = priorityFlag;
  }

  public String getScheduled() {
    return scheduled;
  }

  public void setScheduled(final String scheduled) {
    this.scheduled = scheduled;
  }

  public String getValidityPeriod() {
    return validityPeriod;
  }

  public void setValidityPeriod(final String validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  public byte getReplaceIfPresentFlag() {
    return replaceIfPresentFlag;
  }

  public void setReplaceIfPresentFlag(final byte replaceIfPresentFlag) {
    this.replaceIfPresentFlag = replaceIfPresentFlag;
  }

  public byte[] getShortMessage() {
    return shortMessage;
  }

  public void setShortMessage(final byte[] shortMessage) {
    this.shortMessage = shortMessage;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(final String messageId) {
    this.messageId = messageId;
  }

  public Dr getDr() {
    return dr;
  }

  public void setDr(final Dr dr) {
    this.dr = dr;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SmOut{");
    sb.append("id=").append(id);
    sb.append(", timestamp=").append(timestamp);
    sb.append(", operationId=").append(operationId);
    sb.append(", user='").append(user).append('\'');
    sb.append(", connectionId='").append(connectionId).append('\'');
    sb.append(", serviceType='").append(serviceType).append('\'');
    sb.append(", source='").append(source).append('\'');
    sb.append(", sourceTon=").append(sourceTon);
    sb.append(", sourceNpi=").append(sourceNpi);
    sb.append(", destination='").append(destination).append('\'');
    sb.append(", destinationTon=").append(destinationTon);
    sb.append(", destinationNpi=").append(destinationNpi);
    sb.append(", esmClass=").append(esmClass);
    sb.append(", dataCodingScheme=").append(dataCodingScheme);
    sb.append(", protocolIdentifier=").append(protocolIdentifier);
    sb.append(", priorityFlag=").append(priorityFlag);
    sb.append(", scheduled='").append(scheduled).append('\'');
    sb.append(", validityPeriod='").append(validityPeriod).append('\'');
    sb.append(", replaceIfPresentFlag=").append(replaceIfPresentFlag);
    sb.append(", shortMessage=").append(Util.bytesToHexString(shortMessage));
    sb.append(", messageId='").append(messageId).append('\'');
    sb.append(", dr=").append(dr);
    sb.append('}');
    return sb.toString();
  }
}
