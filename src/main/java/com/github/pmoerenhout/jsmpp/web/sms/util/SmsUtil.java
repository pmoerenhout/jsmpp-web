package com.github.pmoerenhout.jsmpp.web.sms.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.IndicationSense;
import org.jsmpp.bean.IndicationType;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.MessageWaitingDataCoding;
import org.jsmpp.bean.SimpleDataCoding;
import org.springframework.stereotype.Service;

import com.github.pmoerenhout.jsmpp.web.Util;
import com.github.pmoerenhout.jsmpp.web.exception.LongSmsException;
import com.github.pmoerenhout.jsmpp.web.sms.util.model.ShortMessage;
import com.github.pmoerenhout.jsmpp.web.sms.util.model.UserDataHeader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsUtil {

  private static final Random RANDOM = new Random();
  private static final int SMS_OCTECTS = 140;

  public static int fillBits(final int udhSize) {
    final int reminder = (udhSize % 7);
    if (reminder != 0) {
      return 7 - reminder;
    }
    return 0;
  }

  public static byte[] removeFillBits(final byte[] data, final int fillBits) {
    final BitSet dataBitSet = BitSet.valueOf(data);
    final BitSet bs = new BitSet(data.length * 8 - fillBits);
    for (int i = 0; i < (data.length * 8 - fillBits); i++) {
      bs.set(i, dataBitSet.get(i + fillBits));
    }
    return bs.toByteArray();
  }

  public byte[] getUserData(final boolean udhi, final byte[] data) {
    if (udhi) {
      final int udhLength = data[0] + 1;
      final byte[] ud = new byte[data.length - udhLength];
      System.arraycopy(data, udhLength, ud, 0, data.length - udhLength);
      return ud;
    }
    return data;
  }

  public ShortMessage[] createShortMessagesSplitOrLong(final boolean udhi, final byte[] data, final boolean longSmsEnabled, final int longSmsMaxSize,
                                                       final Integer singleSmsMaxSize)
      throws LongSmsException {
    if (longSmsEnabled) {
      log.debug("Long sms is enabled with a maximum number of {} bytes", longSmsMaxSize);
    } else {
      if (singleSmsMaxSize != null) {
        log.debug("Long sms is disabled, maximum size per single sms is {}", singleSmsMaxSize);
      } else {
        log.debug("Long sms is disabled");
      }
    }
    log.debug("Will the data be send as long SMS? {}", longSmsEnabled);

    return longSmsEnabled ?
        longSms(udhi, data, longSmsMaxSize) :
        split8bit(udhi, data, singleSmsMaxSize);
  }

  public ShortMessage[] longSms(final boolean udhi, final byte[] data, final int maxSize)
      throws LongSmsException {
    log.debug("Create LongSMS with UDH indicator:{} length:{} maxSize:{}", udhi, data.length, maxSize);
    final ESMClass esmClass = new ESMClass();
    if (udhi) {
      esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
    }
    if (data.length > maxSize) {
      throw new LongSmsException("The message is exceeding the maximum length of " + maxSize + " octets");
    }
    final ShortMessage sm = new ShortMessage(esmClass.value(), data);
    return new ShortMessage[]{ sm };
  }

  public ShortMessage[] split8bit(final boolean udhi, final byte[] data) {
    return split8bit(RANDOM.nextInt(256), udhi, data, SMS_OCTECTS);
  }

  public ShortMessage[] split8bit(final boolean udhi, final byte[] data, final Integer singleSmsMaxSize) {
    return split8bit(RANDOM.nextInt(256), udhi, data, (singleSmsMaxSize != null ? singleSmsMaxSize.intValue() : SMS_OCTECTS));
  }

//  public ShortMessage createShortMessageText(final String user, final String source, final String destination, final String message) {
//    final ShortMessage shortMessage = new ShortMessage();
//    shortMessage.setUser(user);
//    shortMessage.setDestination(destination);
//    shortMessage.setSource(source);
//    if (source.startsWith("+")) {
//      shortMessage.setSource(source.substring(1));
//      shortMessage.setSourceTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setSourceNpi(NumberingPlanIndicator.ISDN.value());
//    } else if (StringUtils.isAlphanumericSpace(source)) {
//      shortMessage.setSource(source);
//      shortMessage.setSourceTon(TypeOfNumber.ALPHANUMERIC.value());
//      shortMessage.setSourceNpi(NumberingPlanIndicator.UNKNOWN.value());
//    } else if (StringUtils.isNumeric(source) && source.length() <= 5) {
//      shortMessage.setSource(source);
//      shortMessage.setSourceTon(TypeOfNumber.ABBREVIATED.value());
//      shortMessage.setSourceNpi(NumberingPlanIndicator.UNKNOWN.value());
//    } else if (destination.startsWith("00")) {
//      shortMessage.setDestination(destination.substring(2));
//      shortMessage.setDestinationTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    } else if (destination.startsWith("0")) {
//      shortMessage.setDestination(destination.substring(1));
//      shortMessage.setDestinationTon(TypeOfNumber.NATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    } else {
//      shortMessage.setSource(source);
//      shortMessage.setSourceTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setSourceNpi(NumberingPlanIndicator.ISDN.value());
//    }
//    if (destination.startsWith("+")) {
//      shortMessage.setDestination(destination.substring(1));
//      shortMessage.setDestinationTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    } else if (destination.startsWith("00")) {
//      shortMessage.setDestination(destination.substring(2));
//      shortMessage.setDestinationTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    } else if (destination.startsWith("0")) {
//      shortMessage.setDestination(destination.substring(1));
//      shortMessage.setDestinationTon(TypeOfNumber.NATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    } else {
//      shortMessage.setDestination(destination);
//      shortMessage.setDestinationTon(TypeOfNumber.INTERNATIONAL.value());
//      shortMessage.setDestinationNpi(NumberingPlanIndicator.ISDN.value());
//    }
//    shortMessage.setDataCodingScheme(DataCodings.ZERO.toByte());
//    shortMessage.setShortMessage(encodeGsm(message));
//    LOG.info("MSG: '{}' {}", message, Util.bytesToHexString(shortMessage.getShortMessage()));
//    return shortMessage;
//  }

  // TODO: for text message, use char boundary
  public ShortMessage[] split8bit(final int reference, final boolean udhi, final byte[] data, final int octets) {
    log.debug("SMS user data header indicator: {}", udhi);
    log.debug("SMS 8-bit reference: {}", Util.bytesToHexString((byte) (reference & 0xff)));
    log.debug("SMS data: {} ({} bytes)", Util.bytesToHexString(data), data.length);
    final ESMClass esmClass = new ESMClass();
    int udhl = 0;
    final byte[] udh;
    final byte[] ud;
    if (udhi) {
      /* first byte is the header length remaining */
      udhl = data[0] + 1;
      udh = ArrayUtils.subarray(data, 0, udhl);
      ud = ArrayUtils.subarray(data, udhl, data.length);
    } else {
      udh = new byte[]{};
      ud = data.clone();
    }
    log.debug("DATA [" + String.format("%03d", data.length) + "]: "
        + Util.bytesToHexString(data));
    log.debug("UDH  ["
        + String.format("%03d", udh.length) + "]: "
        + Util.bytesToHexString(udh));
    log.debug("UD   [" + String.format("%03d", ud.length) + "]: "
        + Util.bytesToHexString(ud));

    final ArrayList<ShortMessage> asm = new ArrayList<>();

    int bytesDone = 0;
    int part = 1;
    int total = getTotalParts(udhl, data.length);

    while (bytesDone < ud.length) {
      final ShortMessage sm = new ShortMessage();
      byte[] userDataHeaderBytes;
      int availableBytes = (part == 1 && udhi ? octets - 5 - udh
          .length : octets - 6);
      int bytesToCopy = (ud.length - bytesDone > availableBytes ? availableBytes
          : ud.length - bytesDone);
      // LOG.debug("bytes to copy:" + bytesToCopy);
      if (part == 1) {
        userDataHeaderBytes = udh;
        if (udhl > 0 || total > 1) {
//            LOG.debug("FIRST PART MUST HAVE UDH, available:"
//                + availableBytes + " bytes to copy:" + bytesToCopy);
          esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
        }
      } else {
        // next parts always have UDH for concatenation
        userDataHeaderBytes = new byte[]{};
        esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
      }
      if (total > 1) {
        final UserDataHeader newUserDataHeader = new UserDataHeader();
        newUserDataHeader.addInformationElementConcatenated((byte) (reference & (byte) 0xff), total, part);
        if (part == 1 && udhi) {
          newUserDataHeader.addHeader(ArrayUtils.subarray(udh, 1, udh[0] + 1));
        }
        userDataHeaderBytes = newUserDataHeader.getBytes();
      }

      final byte[] userDataBytes = new byte[bytesToCopy];
      System.arraycopy(ud, bytesDone, userDataBytes, 0, bytesToCopy);

      log.debug("[{}/{}] UDH:{} ({}) UD:{} ({}) UDHI:{}",
          part, total,
          Util.bytesToHexString(userDataHeaderBytes), userDataHeaderBytes.length,
          Util.bytesToHexString(userDataBytes), userDataBytes.length,
          GSMSpecificFeature.UDHI.containedIn(esmClass));
      sm.setShortMessage(ArrayUtils.addAll(userDataHeaderBytes, userDataBytes));
      sm.setEsmClass(esmClass.value());
      asm.add(sm);
      bytesDone += bytesToCopy;
      part++;
    }
    return asm.toArray(new ShortMessage[asm.size()]);
  }

  public ShortMessage[] split8bitOK(final int reference, final boolean udhi, final byte[] data) {
    log.debug("split8bit({},{},{})", reference, udhi, Util.bytesToHexString(data));
    final ESMClass esmClass = new ESMClass();
    if (udhi) {
      esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
    }
    try {
      int udhl = 0;
      UserDataHeader udh;
      byte[] ud;
      if (udhi) {
        /* first byte is the header length */
        udhl = data[0];
        udh = new UserDataHeader(ArrayUtils.subarray(data, 0, 1 + udhl));
        ud = ArrayUtils.subarray(data, 1 + udhl, data.length);
      } else {
        /* create an empty user data header */
        udh = new UserDataHeader();
        ud = data.clone();
      }
      log.debug("DATA [{}]: {}", String.format("%03d", data.length), Util.bytesToHexString(data));
      log.debug("UDH  [{}]: {}", String.format("%03d", udh.getBytes().length), Util.bytesToHexString(udh.getBytes()));
      log.debug("UD   [{}]: {}", String.format("%03d", ud.length), Util.bytesToHexString(ud));

      final ArrayList<ShortMessage> asm = new ArrayList<>();

      int bytesDone = 0;
      int part = 0;
      while (bytesDone < ud.length) {
        final ShortMessage sm = new ShortMessage();
        byte[] userDataHeaderBytes;
        int availableBytes = (part == 0 && udhi ? 140 - 5 - udh
            .length() : 140 - 6);
        int bytesToCopy = (ud.length - bytesDone > availableBytes ? availableBytes
            : ud.length - bytesDone);
        byte[] userDataBytes;

        if (part == 0 && udhl > 0) {
          log.debug("FIRST PART HAS OWN UDH, available: {} bytes to copy: {}", availableBytes, bytesToCopy);
          userDataBytes = new byte[bytesToCopy];
          userDataHeaderBytes = udh.getBytes();
          esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
        } else {
          log.debug("bytes to copy: {}", bytesToCopy);
          userDataBytes = new byte[bytesToCopy];
          userDataHeaderBytes = new byte[]{}; //UserDataHeader().getBytes();
          //sm.setUserDataHeaderIndicator(false);
        }
        System.arraycopy(ud, bytesDone, userDataBytes, 0, bytesToCopy);
        //sm.setReference(reference);

        log.debug("Adding UD: {} ({}) UDHI:{}"
            , Util.bytesToHexString(userDataBytes),
            userDataBytes.length,
            GSMSpecificFeature.UDHI.containedIn(esmClass));
        sm.setShortMessage(ArrayUtils.addAll(userDataHeaderBytes, userDataBytes));
        sm.setEsmClass(esmClass.value());
        asm.add(sm);
        bytesDone += bytesToCopy;
        part++;
      }
      log.debug("8-bit concatenated reference: 0x{}", Util.bytesToHexString((byte) (reference & (byte) 0xff)));
      for (int j = 0; j < asm.size(); j++) {
        ShortMessage sm = asm.get(j);
        byte[] shortMessage = sm.getShortMessage();
        // Split message into UDH and UD part
        boolean udhiPart = GSMSpecificFeature.UDHI.containedIn(sm.getEsmClass());
        byte[] smUdh = (j == 0 && udhiPart ? ArrayUtils
            .subarray(shortMessage, 0, shortMessage[0] + 1) : new byte[]{});
        byte[] smUd = (j == 0 && udhiPart ? ArrayUtils
            .subarray(shortMessage, shortMessage[0] + 1, shortMessage.length - shortMessage[0] + 2) : shortMessage);

        log.debug("PART[{}/{}]: SM:{} ({} bytes)",
            j + 1, asm.size(),
            Util.bytesToHexString(sm.getShortMessage()), sm.getShortMessage().length);
        log.debug("PART[{}/{}]: UDHI:{} UDH:{} UD:{}",
            j + 1, asm.size(), udhiPart,
            Util.bytesToHexString(smUdh),
            Util.bytesToHexString(smUd));

        if (asm.size() > 1) {
          final UserDataHeader newUserDataHeader = new UserDataHeader();
          newUserDataHeader.addInformationElementConcatenated(
              (byte) (reference & (byte) 0xff), asm.size(), j + 1);
          if (j == 0 && udhi) {
            newUserDataHeader.addHeader(udh.getBytesWithoutLength());
          }
          sm.setShortMessage(ArrayUtils.addAll(newUserDataHeader.getBytes(), smUd));
          esmClass.setSpecificFeature(GSMSpecificFeature.UDHI);
        } else {
          if (!udhiPart) {
            sm.setShortMessage(smUd);
          }
        }
        sm.setEsmClass(esmClass.value());

        log.debug("PART[{}/{}]: UDHI:{} UD:{} LENGTH:{}",
            j + 1, asm.size(), GSMSpecificFeature.UDHI.containedIn(esmClass),
            Util.bytesToHexString(sm.getShortMessage()),
            sm.getShortMessage().length);

        log.debug("PART[{}/{}]: SM:{}", j + 1, asm.size(), Util.bytesToHexString(sm.getShortMessage()));
      }
      return asm.toArray(new ShortMessage[asm.size()]);
    } catch (Exception e) {
      log.error("error in split8bit: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

//  public String toE164(final String destination) {
//    return toE164(destination, ISO_3166_1_NL);
//  }
//
//  public String toE164(final String destination, final String isoCountry) {
//    try {
//      final PhoneNumber number = phoneUtil.parse(destination, isoCountry);
//      LOG.debug("Is {} number valid? {}", destination, phoneUtil.isValidNumber(number));
//      LOG.debug("Is {} number possible? {}", destination, phoneUtil.isPossibleNumber(number));
//      // skip '+' character
//      return phoneUtil.format(number, E164).substring(1);
//    } catch (final NumberParseException e) {
//      LOG.error("Could not parse destination '" + destination + "' as phonenumber", e);
//      return null;
//    }
//  }

  public ShortMessage createShortMessageFlash() {
    final ShortMessage shortMessage = new ShortMessage();
    shortMessage.setDataCodingScheme(new SimpleDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS0).toByte());
    shortMessage.setProtocolIdentifier((byte) 0x00);
    return shortMessage;
  }

  public ShortMessage createShortMessageMwi(final boolean active) {
    final ShortMessage shortMessage = new ShortMessage();
    final IndicationSense indicationSense = (active ? IndicationSense.ACTIVE : IndicationSense.INACTIVE);
    shortMessage.setDataCodingScheme(
        new MessageWaitingDataCoding(indicationSense, IndicationType.VOICEMAIL_MESSAGE_WAITING).toByte());
    //byte dcs = (number == 0 ? (byte) 0xd0 : (byte) 0xd8);
    shortMessage.setProtocolIdentifier((byte) 0x00);
    return shortMessage;
  }

  private int getTotalParts(final int udhl, final int length) {
    if (length <= 140) {
      return 1;
    } else {
      return 1 + ((length - (udhl == 0 ? 2 : 1)) / 134);
    }
  }

}
