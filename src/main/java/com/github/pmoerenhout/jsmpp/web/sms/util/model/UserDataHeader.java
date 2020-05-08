package com.github.pmoerenhout.jsmpp.web.sms.util.model;

import java.util.Arrays;

public class UserDataHeader {

  static final byte IEI_CONCATENATED_8BIT = (byte) 0x00;
  static final byte IEI_SPECIAL_SMS_MESSAGE_INDICATION = (byte) 0x01;
  static final byte IEI_APPLICATIONPORT_8BIT = (byte) 0x04;
  static final byte IEI_APPLICATIONPORT_16BIT = (byte) 0x05;
  static final byte IEI_SERVICE_CENTER_CONTROL = (byte) 0x06;
  static final byte IEI_UDH_SOURCE_INDICATOR = (byte) 0x07;
  static final byte IEI_CONCATENATED_16BIT = (byte) 0x08;
  static final byte IEI_WIRELESS_CONTROL_MESSAGE_PROTOCOL = (byte) 0x09;
  static final byte IEI_TEXT_FORMATTING = (byte) 0x0a;
  static final byte IEI_PREDEFINED_SOUND = (byte) 0x0b;
  static final byte IEI_USERDEFINED_SOUND = (byte) 0x0c;
  static final byte IEI_PREDEFINED_ANIMATION = (byte) 0x0d;
  static final byte IEI_LARGE_ANIMATION = (byte) 0x0e;
  static final byte IEI_SMALL_ANIMATION = (byte) 0x0f;
  static final byte IEI_LARGE_PICTURE = (byte) 0x10;
  static final byte IEI_SMALL_PICTURE = (byte) 0x11;
  static final byte IEI_VARIABLESIZE_PICTURE = (byte) 0x12;
  static final byte IEI_USER_PROMPT_INDICATOR = (byte) 0x13;
  static final byte IEI_EXTENDED_OBJECT = (byte) 0x14;
  static final byte IEI_REUSED_EXTENDED_OBJECT = (byte) 0x15;
  static final byte IEI_COMPRESSION_CONTROL = (byte) 0x16;
  // ...
  // http://books.google.nl/books?id=rYeHSlp0CMsC&pg=PA97&lpg=PA97&dq=ie+iedl+information+element+sms&source=bl&ots=c7K1C7cICA&sig=ffUVz2PMiYslTl1F3ypxtG8qVHU&hl=nl&sa=X&ei=ryjdUfzUBIWZPYKJgJAC&ved=0CDsQ6AEwAQ#v=onepage&q=ie%20iedl%20information%20element%20sms&f=false
  static final byte IEI_EMAIL_HEADER = (byte) 0x20;
  static final byte IEI_HYPERLINK_FORMAT = (byte) 0x21;
  static final byte IEI_ALTERNATE_REPLY = (byte) 0x22;
  static final byte IEI_ENHANCED_VOICEMAIL_NOTIFICATION = (byte) 0x23;
  //
  static final byte IEI_USIM_SECURITY_HEADER = (byte) 0x70;
  byte[] header = new byte[140];

  public UserDataHeader() {
    // header[0] = 0;
    Arrays.fill(header, (byte) 0x00);
  }

  public UserDataHeader(byte[] data) throws IllegalArgumentException {
    /* data is including the length in the first byte */
    Arrays.fill(header, (byte) 0x00);

    if (data[0] + 1 != data.length) {
      throw new IllegalArgumentException("Invalid length for user data header (mismatch)");
    }
    if (data[0] > 140) {
      throw new IllegalArgumentException("Invalid length for user data header (> 140)");
    }
    System.arraycopy(data, 0, header, 0, data.length);
  }

  public void addHeader(byte[] ie) {
    System.arraycopy(ie, 0, header, 1 + header[0], ie.length);
    header[0] += ie.length;
  }

  public void addInformationElement(byte iei, int iedl, byte[] ied) throws IllegalArgumentException {
    if (iedl != ied.length) {
      throw new IllegalArgumentException("Invalid length for IE");
    }
    byte[] ie = new byte[2 + iedl];
    ie[0] = iei;
    ie[1] = (byte) (iedl & (byte) 0xff);
    System.arraycopy(ied, 0, ie, 2, iedl);
    addHeader(ie);
  }

  public void addInformationElementConcatenated(byte reference,
                                                int totalParts, int numberOfPart) {
    addInformationElement(UserDataHeader.IEI_CONCATENATED_8BIT, 3,
        new byte[]{ reference, (byte) (totalParts & 0xff),
            (byte) (numberOfPart & (byte) 0xff) });
  }

  public void addInformationElementConcatenated(short reference,
                                                int totalParts, int numberOfPart) {
    addInformationElement(UserDataHeader.IEI_CONCATENATED_16BIT, 4,
        new byte[]{ (byte) ((reference >> 8) & (byte) 0xff),
            (byte) ((reference) & (byte) 0xff),
            (byte) (totalParts & (byte) 0xff),
            (byte) (numberOfPart & (byte) 0xff) });
  }

  public void addInformationElementApplicationPort(byte destination,
                                                   byte originator) throws Exception {
    addInformationElement(UserDataHeader.IEI_APPLICATIONPORT_8BIT, 2,
        new byte[]{ destination, originator });
  }

  public void addInformationElementApplicationPort(short destination, short originator) {
    addInformationElement(UserDataHeader.IEI_APPLICATIONPORT_16BIT, 4,
        new byte[]{ (byte) ((destination >> 8) & (byte) 0xff),
            (byte) ((destination) & (byte) 0xff),
            (byte) ((originator >> 8) & (byte) 0xff),
            (byte) ((originator) & (byte) 0xff) });
  }

  public void addInformationElementSpecialSmsMessageIndication(byte type, byte number) {
    addInformationElement(UserDataHeader.IEI_SPECIAL_SMS_MESSAGE_INDICATION, 2, new byte[]{ type, number });
  }

  public void addInformationElementSimToolkitSecurityHeader(final byte[] sec) {
    addInformationElement(UserDataHeader.IEI_USIM_SECURITY_HEADER, sec.length, sec);
  }

  public byte[] getBytes() {
    return Arrays.copyOfRange(header, 0, 1 + header[0]);
  }

  public byte[] getBytesWithoutLength() {
    return Arrays.copyOfRange(header, 1, 1 + header[0]);
  }

  public int length() {
    return 1 + header[0];
  }

}