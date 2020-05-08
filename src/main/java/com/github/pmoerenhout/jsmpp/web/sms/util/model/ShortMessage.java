package com.github.pmoerenhout.jsmpp.web.sms.util.model;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortMessage {

  @JsonProperty
  private long id;

  @JsonProperty
  private String user;

  @JsonProperty
  private String smscAddress;

  @JsonProperty
  private byte smscAddressTon;

  @JsonProperty
  private byte smscAddressNpi;

  @JsonProperty(required = true)
  private String source;

  @JsonProperty
  private byte sourceTon;

  @JsonProperty
  private byte sourceNpi;

  @JsonProperty
  private String scheduled;

  @JsonProperty
  private String validityPeriod;

  @JsonProperty
  private byte esmClass;

  @JsonProperty(defaultValue = "0")
  private byte priorityFlag;

  @JsonProperty(defaultValue = "0")
  private byte replaceIfPresentFlag;

  @JsonProperty(defaultValue = "CMT")
  private String serviceType;

  @JsonProperty(required = true)
  private String destination;

  @JsonProperty
  private byte destinationTon;

  @JsonProperty
  private byte destinationNpi;

  @JsonProperty
  private byte dataCodingScheme;

  @JsonProperty
  private byte protocolIdentifier;

  @JsonProperty("registered_delivery")
  private byte registeredDelivery;

  // The name must be unique (see
  @JsonProperty("short_message")
  private byte[] shortMessage;

  public ShortMessage() {
  }

  public ShortMessage(final byte esmClass, final byte[] shortMessage) {
    this.esmClass = esmClass;
    this.shortMessage = shortMessage;
  }

  public long getId() {
    return id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getUser() {
    return user;
  }

  public void setUser(final String user) {
    this.user = user;
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

  public byte getEsmClass() {
    return esmClass;
  }

  public void setEsmClass(final byte esmClass) {
    this.esmClass = esmClass;
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

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(final String serviceType) {
    this.serviceType = serviceType;
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

  public byte getRegisteredDelivery() {
    return registeredDelivery;
  }

  public void setRegisteredDelivery(final byte registeredDelivery) {
    this.registeredDelivery = registeredDelivery;
  }

  public byte[] getShortMessage() {
    return shortMessage;
  }

  public void setShortMessage(final byte[] shortMessage) {
    this.shortMessage = shortMessage;
  }

  public ShortMessage withShortMessage(final byte[] shortMessage) {
    this.shortMessage = shortMessage;
    return this;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ShortMessage that = (ShortMessage) o;
    return id == that.id &&
        smscAddressTon == that.smscAddressTon &&
        smscAddressNpi == that.smscAddressNpi &&
        sourceTon == that.sourceTon &&
        sourceNpi == that.sourceNpi &&
        esmClass == that.esmClass &&
        priorityFlag == that.priorityFlag &&
        replaceIfPresentFlag == that.replaceIfPresentFlag &&
        destinationTon == that.destinationTon &&
        destinationNpi == that.destinationNpi &&
        dataCodingScheme == that.dataCodingScheme &&
        protocolIdentifier == that.protocolIdentifier &&
        registeredDelivery == that.registeredDelivery &&
        Objects.equals(user, that.user) &&
        Objects.equals(smscAddress, that.smscAddress) &&
        Objects.equals(source, that.source) &&
        Objects.equals(scheduled, that.scheduled) &&
        Objects.equals(validityPeriod, that.validityPeriod) &&
        Objects.equals(serviceType, that.serviceType) &&
        Objects.equals(destination, that.destination) &&
        Arrays.equals(shortMessage, that.shortMessage);
  }

  @Override
  public int hashCode() {

    int result = Objects
        .hash(id, user, smscAddress, smscAddressTon, smscAddressNpi, source, sourceTon, sourceNpi, scheduled, validityPeriod, esmClass, priorityFlag,
            replaceIfPresentFlag, serviceType, destination, destinationTon, destinationNpi, dataCodingScheme, protocolIdentifier, registeredDelivery);
    result = 31 * result + Arrays.hashCode(shortMessage);
    return result;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("ShortMessage{");
    sb.append("id=").append(id);
    sb.append(", user='").append(user).append('\'');
    sb.append(", smscAddress='").append(smscAddress).append('\'');
    sb.append(", smscAddressTon=").append(smscAddressTon);
    sb.append(", smscAddressNpi=").append(smscAddressNpi);
    sb.append(", source='").append(source).append('\'');
    sb.append(", sourceTon=").append(sourceTon);
    sb.append(", sourceNpi=").append(sourceNpi);
    sb.append(", scheduled='").append(scheduled).append('\'');
    sb.append(", validityPeriod='").append(validityPeriod).append('\'');
    sb.append(", esmClass=").append(esmClass);
    sb.append(", priorityFlag=").append(priorityFlag);
    sb.append(", replaceIfPresentFlag=").append(replaceIfPresentFlag);
    sb.append(", serviceType='").append(serviceType).append('\'');
    sb.append(", destination='").append(destination).append('\'');
    sb.append(", destinationTon=").append(destinationTon);
    sb.append(", destinationNpi=").append(destinationNpi);
    sb.append(", dataCodingScheme=").append(dataCodingScheme);
    sb.append(", protocolIdentifier=").append(protocolIdentifier);
    sb.append(", registeredDelivery=").append(registeredDelivery);
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
    sb.append('}');
    return sb.toString();
  }
}
