package com.github.pmoerenhout.jsmpp.web.sms;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.OptionalParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pmoerenhout.jsmpp.web.Util;
import com.github.pmoerenhout.jsmpp.web.event.DeliveryReceiptEvent;
import com.github.pmoerenhout.jsmpp.web.exception.ConnectionNotFoundException;
import com.github.pmoerenhout.jsmpp.web.jpa.model.Dr;
import com.github.pmoerenhout.jsmpp.web.jpa.model.SmIn;
import com.github.pmoerenhout.jsmpp.web.jpa.model.SmOut;
import com.github.pmoerenhout.jsmpp.web.jpa.repository.DrRepository;
import com.github.pmoerenhout.jsmpp.web.jpa.repository.SmInRepository;
import com.github.pmoerenhout.jsmpp.web.jpa.repository.SmOutRepository;
import com.github.pmoerenhout.jsmpp.web.smpp.SmppClientService;
import com.github.pmoerenhout.jsmpp.web.smpp.util.SmppUtil;
import com.github.pmoerenhout.jsmpp.web.sms.util.model.ShortMessage;

@Service
public class SmsService {
  private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);

  private final static OptionalParameter[] NO_OPTIONAL_PARAMETERS = new OptionalParameter[]{};

  @Autowired
  private SmInRepository smInRepository;
  @Autowired
  private SmOutRepository smOutRepository;
  @Autowired
  private DrRepository drRepository;
  @Autowired
  private SmppClientService smppClientService;
  @Autowired
  private ApplicationEventPublisher publisher;

  @Transactional(readOnly = true)
  public void showLastOrderByTimestamp(final int size) {
    LOG.info("Show last {} entries", size);
    final PageRequest firstPage = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "timestamp"));
//    final PageRequest firstPage = new PageRequest(0, size,
//        new Sort.Order(Sort.Direction.DESC, "timestamp")
////        new Sort(
////            new Sort.Order(Sort.Direction.DESC, "timestamp")
////        )
//    );
    final Page<SmOut> smOuts = smOutRepository.findAll(firstPage);
    smOuts.forEach(o -> {
      LOG.info("SM {}: user:{} conn:{} timestamp:{} src:{} dst:{} msgId:{}", o.getId(), o.getUser(), o.getConnectionId(), o.getTimestamp(), o.getSource(),
          o.getDestination(), o.getMessageId());
    });
    final Page<Dr> drs = drRepository.findAll(firstPage);
    drs.forEach(o -> {
      LOG.info("DR {}: conn:{} timestamp:{} src:{} dst:{} msgId:{} status:{} error:{} text:{}", o.getId(), o.getConnectionId(), o.getTimestamp(), o.getSource(),
          o.getDestination(), o.getMessageId(), Util.bytesToHexString(o.getState()), o.getError(), o.getText() != null ? new String(o.getText()) : "");
    });
  }

//  @Scheduled(initialDelay = 2000, fixedRate = 10000)
//  @Transactional
//  public void sendAll() throws IOException {
//    try (final Stream<SmOut> sms = smRepository.findAllByMessageIdIsNull()) {
//      sms.forEach(sm -> {
//        LOG.info("Send SMS {}: user:{} {} -> {}", sm.getId(), sm.getUser(), sm.getSource(), sm.getDestination());
//        sendSingleSm(sm);
//      });
//    }
//  }

  public SmOut findOneByConnectionIdAndMessageIdAndSourceAndDestination(
      final String connectionId,
      final String messageId,
      final String source,
      final String destination)
      throws ConnectionNotFoundException {
    final List<String> connectionIds = smppClientService.getConnectionsInSameContext(connectionId);
    return smOutRepository.findOneByConnectionIdInAndMessageIdAndSourceAndDestination(connectionIds, messageId, source, destination);
  }

  public SmOut saveSm(final UUID operationId, final String user, final ShortMessage shortMessage) {
    final SmOut smOut = new SmOut();
    smOut.setTimestamp(Instant.now());
    smOut.setOperationId(operationId);
    smOut.setUser(user);

    smOut.setServiceType(shortMessage.getServiceType());

    // Destination as MSISDN in E.164 format (without +)
    smOut.setDestinationTon(shortMessage.getDestinationTon());
    smOut.setDestinationNpi(shortMessage.getDestinationNpi());
    smOut.setDestination(shortMessage.getDestination());
    smOut.setSourceTon(shortMessage.getSourceTon());
    smOut.setSourceNpi(shortMessage.getSourceNpi());
    smOut.setSource(shortMessage.getSource());
    smOut.setDataCodingScheme(shortMessage.getDataCodingScheme());
    smOut.setProtocolIdentifier(shortMessage.getProtocolIdentifier());
    smOut.setEsmClass(shortMessage.getEsmClass());
    smOut.setPriorityFlag(shortMessage.getPriorityFlag());
    smOut.setShortMessage(shortMessage.getShortMessage());
    return smOutRepository.save(smOut);
  }

  public SmIn saveSmIn(final UUID operationId, final String user, final ShortMessage shortMessage) {
    final SmIn smIn = new SmIn();
    smIn.setTimestamp(Instant.now());
    // smIn.setUser(user);
    smIn.setSmscAddress(shortMessage.getSmscAddress());
    return smInRepository.save(smIn);
  }

  public Optional<SmIn> findSmIn(final SmIn smIn) {
    List<SmIn> list = smInRepository.findBySmscTimestampAndSourceAndDestinationAndShortMessage(
        smIn.getSmscTimestamp(), smIn.getSource(),
        smIn.getDestination(),
        smIn.getShortMessage());
    smInRepository.findAll().stream().forEach(r -> {
      final String message = SmppUtil.decode(r.getDataCodingScheme(), r.getEsmClass(), r.getShortMessage(), SmppUtil.GSM_PACKED_CHARSET);
      LOG.debug("Record {}: {} {}", r.getId(), r.getSmscTimestamp(), message);
    });
    if (list.size() == 0) {
      return Optional.empty();
    } else if (list.size() == 1) {
      return Optional.of(smIn);
    }
    throw new IllegalStateException("Too many records found");
  }

  public SmIn saveSmIn(final SmIn smIn) {
    return smInRepository.save(smIn);
  }

  @EventListener
  protected void onDeliveryReceipt(final DeliveryReceiptEvent event) throws ConnectionNotFoundException {

    final String connectionId = event.getConnectionId();
    final DeliveryReceipt deliveryReceipt = event.getDeliveryReceipt();
    final DeliverSm deliverSm = event.getDeliverSm();

    final String messageId = deliveryReceipt.getId();

    LOG.info("Received DeliveryReceiptEvent on connection '{}' for message id '{}' from {} to {}",
        connectionId, messageId, deliverSm.getSourceAddr(), deliverSm.getDestAddress());

    // showLastOrderByTimestamp(5);

    final Dr dr = new Dr();
    dr.setTimestamp(Instant.now());
    dr.setConnectionId(event.getConnectionId());
    dr.setMessageId(deliverSm.getId());

    dr.setServiceType(deliverSm.getServiceType());

    dr.setSourceTon(deliverSm.getSourceAddrTon());
    dr.setSourceNpi(deliverSm.getSourceAddrNpi());
    dr.setSource(deliverSm.getSourceAddr());

    dr.setDestinationTon(deliverSm.getDestAddrTon());
    dr.setDestinationNpi(deliverSm.getDestAddrNpi());
    dr.setDestination(deliverSm.getDestAddress());

    dr.setMessageId(messageId);
    dr.setDelivered((byte) (deliveryReceipt.getDelivered() & 0xff));
    dr.setSubmitted((byte) (deliveryReceipt.getSubmitted() & 0xff));
    LOG.debug("Delivery receipt {} text is '{}'", messageId, deliveryReceipt.getText());
    if (deliveryReceipt.getText() != null) {
      dr.setText(deliveryReceipt.getText().getBytes());
    }
    dr.setSubmitDate(deliveryReceipt.getSubmitDate().toInstant());
    dr.setDoneDate(deliveryReceipt.getDoneDate().toInstant());
    dr.setError(deliveryReceipt.getError());
    dr.setState((byte) (deliveryReceipt.getFinalStatus().value() & 0xff));
    final Dr drSaved = drRepository.save(dr);
    LOG.debug("Saved delivery receipt with id {} for {}", drSaved.getId(), deliverSm.getSourceAddr());

    final List<String> connectionIds = smppClientService.getConnectionsInSameContext(connectionId);

    // The delivery receipt source and destination are reversed for the incoming message
    // final SmOut smOut = findSmFromReceipt(connectionIds, deliverSm.getDestAddress(), deliverSm.getSourceAddr(), deliveryReceipt.getId());

//    if (smOut == null) {
//      LOG.error("The SM entry could not be found from delivery receipt ({},{},{},{})",
//          connectionId, deliverSm.getDestAddress(), deliverSm.getSourceAddr(), deliveryReceipt.getId());
//      showLastOrderByTimestamp(5);
//      return;
//    }
//
//    final UUID operationId = smOut.getOperationId();
//
//    publisher
//        .publishEvent(
//            new ProcessDeliveryReceiptEvent(this, operationId, connectionId,
//                smOut.getId(), smOut.getDataCodingScheme(), smOut.getProtocolIdentifier(),
//                deliverSm.getDestAddress(), deliverSm.getSourceAddr(),
//                deliveryReceipt));
//
//    // TODO: From session get the charset to decode
//    final String shortMessageAsString = SmppUtil.decode(deliverSm.getDataCoding(), deliverSm.getEsmClass(), deliverSm.getShortMessage());
//    LOG.info("Receiving deliver_sm with delivery receipt message: {}", shortMessageAsString);
  }

}
