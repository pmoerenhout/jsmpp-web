package com.github.pmoerenhout.jsmpp.web.jpa.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "smin")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmIn implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty
  private Long id;

  @Column(name = "timestamp")
  @JsonProperty
  private Instant timestamp;

  @Column(name = "connectionid")
  @JsonProperty
  private String connectionId;

  @Column(name = "messageid")
  @JsonProperty
  private String messageId;

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

  @Column(name = "replaceifpresent", updatable = false)
  @JsonProperty
  private byte replaceIfPresentFlag;

  @Column(name = "shortmessage", updatable = false, columnDefinition = "blob")
  @JsonProperty
  private byte[] shortMessage;

  @Column(name = "smscaddress", updatable = false)
  @JsonProperty
  private String smscAddress;

  @Column(name = "smscaddresston", updatable = false)
  @JsonProperty
  private byte smscAddressTon;

  @Column(name = "smscaddressnpi", updatable = false)
  @JsonProperty
  private byte smscAddressNpi;

  @Column(name = "smsctimestamp", updatable = false)
  @JsonProperty
  private ZonedDateTime smscTimestamp;

  @Column(name = "mms", updatable = false)
  @JsonProperty
  private boolean moreMessagesToSend;

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

  public String getSmscAddress() {
    return smscAddress;
  }

  public void setSmscAddress(final String smscAddress) {
    this.smscAddress = smscAddress;
  }

  public byte getSmscAddressTon() {
    return smscAddressTon;
  }

  public void setSmscAddressTon(final byte smscAddressTon) {
    this.smscAddressTon = smscAddressTon;
  }

  public byte getSmscAddressNpi() {
    return smscAddressNpi;
  }

  public void setSmscAddressNpi(final byte smscAddressNpi) {
    this.smscAddressNpi = smscAddressNpi;
  }

  public ZonedDateTime getSmscTimestamp() {
    return smscTimestamp;
  }

  public void setSmscTimestamp(final ZonedDateTime smscTimestamp) {
    this.smscTimestamp = smscTimestamp;
  }

  public boolean isMoreMessagesToSend() {
    return moreMessagesToSend;
  }

  public void setMoreMessagesToSend(final boolean moreMessagesToSend) {
    this.moreMessagesToSend = moreMessagesToSend;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SmIn{");
    sb.append("id=").append(id);
    sb.append(", timestamp=").append(timestamp);
    sb.append(", connectionId='").append(connectionId).append('\'');
    sb.append(", messageId='").append(messageId).append('\'');
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
    sb.append(", replaceIfPresentFlag=").append(replaceIfPresentFlag);
    sb.append(", shortMessage=");
    if (shortMessage == null) {
      sb.append("null");
    } else {
      sb.append('[');
      for (int i = 0; i < shortMessage.length; ++i) {
        sb.append(i == 0 ? "" : ", ").append(shortMessage[i]);
      }
      sb.append(']');
    }
    sb.append(", smscAddress='").append(smscAddress).append('\'');
    sb.append(", smscAddressTon=").append(smscAddressTon);
    sb.append(", smscAddressNpi=").append(smscAddressNpi);
    sb.append(", smscTimestamp=").append(smscTimestamp);
    sb.append(", moreMessagesToSend=").append(moreMessagesToSend);
    sb.append('}');
    return sb.toString();
  }
}
