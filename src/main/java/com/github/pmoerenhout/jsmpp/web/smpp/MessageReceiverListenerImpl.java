/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.pmoerenhout.jsmpp.web.smpp;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.github.pmoerenhout.jsmpp.web.Util;
import com.github.pmoerenhout.jsmpp.web.event.SmInEvent;
import com.github.pmoerenhout.jsmpp.web.jpa.model.SmIn;
import com.github.pmoerenhout.jsmpp.web.smpp.util.SmppUtil;
import com.github.pmoerenhout.jsmpp.web.sms.SmsService;
import com.github.pmoerenhout.pduutils.gsm0340.Pdu;
import com.github.pmoerenhout.pduutils.gsm0340.PduParser;
import com.github.pmoerenhout.pduutils.gsm0340.SmsDeliveryPdu;

public class MessageReceiverListenerImpl implements MessageReceiverListener {
  private static final Logger LOG = LoggerFactory.getLogger(MessageReceiverListenerImpl.class);

  private String connectionId;
  private Charset defaultCharset;
  private SmsService smsService;
  private SmppClientService smppClientService;
  private ApplicationEventPublisher applicationEventPublisher;

  public MessageReceiverListenerImpl(final String connectionId,
                                     final Charset defaultCharset,
                                     final SmsService smsService,
                                     final SmppClientService smppClientService,
                                     final ApplicationEventPublisher applicationEventPublisher) {
    this.connectionId = connectionId;
    this.defaultCharset = defaultCharset;
    this.smsService = smsService;
    this.smppClientService = smppClientService;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void onAcceptDeliverSm(final DeliverSm deliverSm)
      throws ProcessRequestException {

    LOG.info("onAcceptDeliverSm");

    final Connection connection = smppClientService.getSmppConnection(connectionId);

    if (deliverSm.isSmscDeliveryReceipt()) {
      // if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
      // this message is delivery receipt
      try {
        final DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

        LOG.info("Received receipt: {} {}", delReceipt.getId(), delReceipt.getFinalStatus());

        // lets cover the id to hex string format
        // long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
        // String messageId = Long.toString(id, 16).toUpperCase();
        final String messageId = delReceipt.getId();

//        /*
//         * you can update the status of your submitted message on the database based on messageId
//         */
//        LOG.info("Pending messageId {}", messageId);
//        final PendingReceipt<Command> c = smsService.removePendingResponse(messageId);
//        if (c != null) {
//          c.done(deliverSm);
//        }
//
//        LOG.info("Receiving delivery receipt on {} for message '{}' from {} to {}: {}",
//            connectionId, messageId, deliverSm.getSourceAddr(), deliverSm.getDestAddress(), delReceipt);
//        applicationEventPublisher.publishEvent(new DeliveryReceiptEvent(this, connectionId, delReceipt, deliverSm));

      } catch (InvalidDeliveryReceiptException e) {
        LOG.error("Failed getting delivery receipt", e);
      }
    } else {
      // this message is regular short message
      final OptionalParameter.OctetString messagePayload = deliverSm.getOptionalParameter(OptionalParameter.Message_payload.class);
      LOG.debug("SM: {}", Util.bytesToHexString(deliverSm.getShortMessage()));
      try {
        final byte[] message = SmppUtil.getShortMessageOrPayload(deliverSm.getShortMessage(), messagePayload);
        if (SmppUtil.isBinary(deliverSm.getDataCoding())) {
          LOG.debug("Receiving deliver_sm message: ESM:{} DCS:{} PID:{} DATA:{}",
              Util.bytesToHexString(deliverSm.getEsmClass()),
              Util.bytesToHexString(deliverSm.getDataCoding()),
              Util.bytesToHexString(deliverSm.getProtocolId()),
              Util.bytesToHexString(message));
        } else {
          final String decodedMessage = SmppUtil.decode(deliverSm.getDataCoding(), deliverSm.getEsmClass(), message, defaultCharset);
          LOG.debug("Receiving deliver_sm message: ESM:{} DCS:{} PID:{} DATA:{} TEXT:'{}'",
              Util.bytesToHexString(deliverSm.getEsmClass()),
              Util.bytesToHexString(deliverSm.getDataCoding()),
              Util.bytesToHexString(deliverSm.getProtocolId()),
              Util.bytesToHexString(message),
              decodedMessage);
        }

        SmppUtil.logOptionalParameters(deliverSm.getOptionalParameters(), " ");
        final String smscAddress = deliverSm.getOptionalParameter((short) 8193) != null ?
            ((OptionalParameter.OctetString) deliverSm.getOptionalParameter((short) 8193)).getValueAsString() :
            null;
        final byte smscAddressTon = getOptionalByte(deliverSm.getOptionalParameter((short) 8194));
        final byte smscAddressNpi = getOptionalByte(deliverSm.getOptionalParameter((short) 8195));
        final String smscTimestamp = getOptionalOctetString(deliverSm.getOptionalParameter((short) 8196));

        final byte[] pduRaw = getOptionalByteArray(deliverSm.getOptionalParameter((short) 8200));
        if (pduRaw != null) {
          PduParser pduParser = new PduParser();
          LOG.info("HEX: '{}'", Util.bytesToHexString(pduRaw));
          Pdu pdu = pduParser.parsePdu(Util.bytesToHexString(pduRaw));
          if (pdu instanceof SmsDeliveryPdu) {
            LOG.trace("SCTS: {}", ((SmsDeliveryPdu) pdu).getServiceCentreTimestamp());
          }
        }

        // final String messageId = deliverSm.getId();
        // final String msisdn = deliverSm.getSourceAddr();

        // ShortMessage sm = new ShortMessage();
        final SmIn sm = new SmIn();
        sm.setTimestamp(Instant.now());
        sm.setServiceType(deliverSm.getServiceType());
        sm.setSource(deliverSm.getSourceAddr());
        sm.setSourceTon(deliverSm.getSourceAddrTon());
        sm.setSourceNpi(deliverSm.getSourceAddrNpi());
        sm.setDestination(deliverSm.getDestAddress());
        sm.setDestinationTon(deliverSm.getDestAddrTon());
        sm.setDestinationNpi(deliverSm.getDestAddrNpi());
        sm.setDataCodingScheme(deliverSm.getDataCoding());
        sm.setProtocolIdentifier(deliverSm.getProtocolId());
        sm.setEsmClass(deliverSm.getEsmClass());
        sm.setPriorityFlag(deliverSm.getPriorityFlag());
        sm.setValidityPeriod(deliverSm.getValidityPeriod());
        sm.setScheduled(deliverSm.getScheduleDeliveryTime());
        sm.setReplaceIfPresentFlag(deliverSm.getReplaceIfPresent());
        // Optional
        sm.setSmscAddress(smscAddress);
        sm.setSmscAddressTon(smscAddressTon);
        sm.setSmscAddressNpi(smscAddressNpi);

        if (StringUtils.isNoneBlank(smscTimestamp)) {
          sm.setSmscTimestamp(ZonedDateTime.parse(smscTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        sm.setShortMessage(message);
        final SmIn saved = smsService.saveSmIn(sm);

        final Long id = saved.getId();
        LOG.info("SmIn database id is {}", id);
        applicationEventPublisher.publishEvent(new SmInEvent(this, connectionId, saved));

      } catch (InvalidMessagePayloadException e) {
        throw new ProcessRequestException(e.getMessage(), 6000);
      }
    }
  }

  public void onAcceptAlertNotification(AlertNotification alertNotification) {
    LOG.info("Receiving alert notification from {}: {}", alertNotification.getSourceAddr(), alertNotification.getEsmeAddr());
  }

  public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
      throws ProcessRequestException {
    LOG.info("Receiving data sm from {} to {}", dataSm.getSourceAddr(), dataSm.getDestAddress());
    throw new ProcessRequestException("data_sm is not implemented", 99);
  }

  private byte getOptionalByte(final OptionalParameter optionalParameter) {
    // jSMPP will give as Octet String
    final OptionalParameter.OctetString octetString = (OptionalParameter.OctetString) optionalParameter;
    if (octetString == null) {
      return 0x00;
    }
    final byte[] value = octetString.getValue();
    if (value.length != 1) {
      throw new IllegalArgumentException("The optional parameter " + optionalParameter.tag + " has invalid contents for Byte");
    }
    return value[0];
  }

  private String getOptionalOctetString(final OptionalParameter optionalParameter) {
    // jSMPP will give as Octet String
    if (optionalParameter == null) {
      return null;
    }
    return ((OptionalParameter.OctetString) optionalParameter).getValueAsString();
  }

  private byte[] getOptionalByteArray(final OptionalParameter optionalParameter) {
    // jSMPP will give as Octet String
    if (optionalParameter == null) {
      return null;
    }
    return ((OptionalParameter.OctetString) optionalParameter).getValue();
  }
}