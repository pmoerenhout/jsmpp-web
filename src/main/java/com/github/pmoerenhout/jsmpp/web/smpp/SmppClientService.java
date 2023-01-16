package com.github.pmoerenhout.jsmpp.web.smpp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Address;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.bean.UnsuccessDelivery;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SessionStateListener;
import org.jsmpp.session.SubmitMultiResult;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.github.pmoerenhout.jsmpp.pool.PooledSMPPSession;
import com.github.pmoerenhout.jsmpp.pool.ThrottledSMPPSession;
import com.github.pmoerenhout.jsmpp.web.exception.ConnectionNotFoundException;
import com.github.pmoerenhout.jsmpp.web.sms.SmsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class SmppClientService implements ApplicationContextAware {

  private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();

  private ApplicationEventPublisher applicationEventPublisher;
  private SmppConfiguration smppConfiguration;
  private SessionStateListener sessionStateListener;
  private SmsService smsService;

  private ApplicationContext applicationContext;

  private final Map<String, Connection> pooledSmppSessions = new HashMap<>();

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @EventListener
  public void start(final ApplicationReadyEvent event) {
    if (event.getApplicationContext().getParent() != null) {
      return;
    }
    log.info("Received an application ready event ({}), starting SMPP connections", event.getApplicationContext());
    final List<SmppConfiguration.SmppConnection> smppConnectionList = smppConfiguration.getConnections();
    for (SmppConfiguration.SmppConnection smppConnection : smppConnectionList) {
      final String connectionId = smppConnection.getId();
      if (StringUtils.isBlank(connectionId)) {
        throw new IllegalArgumentException("ConnectionId is mandatory");
      }

      if (!smppConnection.isEnabled()) {
        log.info("SMPP connection '{}' is not enabled, skipping...", connectionId);
        continue;
      }
      PooledSMPPSession pooledSMPPSession = null;
      try {
        final String contextId = smppConnection.getContextId();
        final String description = smppConnection.getDescription();
        final String serviceType = smppConnection.getServiceType();
        final Charset defaultCharset = smppConnection.getCharset();
        final boolean transmittable = smppConnection.getBindType().isTransmittable();
        final boolean longSmsEnabled = smppConnection.isLongSmsEnabled();
        final int longSmsMaxSize = smppConnection.getLongSmsMaxSize();
        final Integer singleSmsMaxSize = smppConnection.getSingleSmsMaxSize();

        final MessageReceiverListener messageReceiverListener = createMessageReceiverListenerBean(connectionId, defaultCharset, smsService,
            applicationEventPublisher);
        // this.sessionStateListener = new SessionStateListenerImpl();

        pooledSMPPSession = new PooledSMPPSession(
            smppConnection.getHost(), smppConnection.getPort(),
            smppConnection.isSsl(),
            smppConnection.getSystemId(), smppConnection.getPassword(), smppConnection.getSystemType(), messageReceiverListener,
            sessionStateListener,
            smppConnection.getEnquireLinkTimer(),
            smppConnection.getTransactionTimer(),
            smppConnection.getBindTimeout(),
            smppConnection.getPoolMaxTotal(),
            smppConnection.getPoolMinIdle(),
            smppConnection.getPoolMaxIdle(),
            smppConnection.getPoolRate(),
            smppConnection.getMaxConcurrentRequests(),
            smppConnection.getPduProcessorDegree());
        log.info("POOL: {} {} {}", smppConnection.getPoolMaxTotal(), smppConnection.getPoolMinIdle(), smppConnection.getPoolMaxIdle());

//        final SMPPSession session = new SMPPSession(SocketConnectionFactory.getInstance());
//        session.setMessageReceiverListener(messageReceiverListener);
//        session.setEnquireLinkTimer(smppConnection.getEnquireLinkTimer());
//        session.connectAndBind(smppConnection.getHost(),
//            smppConnection.getPort(),
//            new BindParameter(smppConnection.getBindType(),
//                smppConnection.getSystemId(),
//                smppConnection.getPassword(),
//                smppConnection.getSystemType(), TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, ""));

        final Connection connection = new Connection(connectionId, contextId, description, serviceType, defaultCharset, transmittable,
            longSmsEnabled, longSmsMaxSize, singleSmsMaxSize, pooledSMPPSession);
        pooledSmppSessions.put(connectionId, connection);

        // sendSubmitMulti(connectionId);

        log.info(
            "Created pooled SMPP session for connection '{}' '{}' ({}) with serviceType {}, charset {}, transmittable {}, longSms:{} {}, sms size {}",
            connectionId, contextId, description, serviceType, defaultCharset, transmittable, longSmsEnabled, longSmsMaxSize, singleSmsMaxSize);
      } catch (Exception e) {
        log.error("Could not create the SMPP session with connection '" + connectionId + "' to " + smppConnection.getHost() + ':' + smppConnection.getPort(),
            e);
        if (pooledSMPPSession != null) {
          pooledSMPPSession.close();
        }
        if (connectionId != null) {
          pooledSmppSessions.remove(connectionId);
        }
      }
    }
  }

//  public String sendSubmitSm(final String connectionId,
//                             final String serviceType,
//                             final TypeOfNumber sourceAddrTon,
//                             final NumberingPlanIndicator sourceAddrNpi,
//                             final String sourceAddr,
//                             final TypeOfNumber destAddrTon,
//                             final NumberingPlanIndicator destAddrNpi,
//                             final String destAddr,
//                             final ESMClass esmClass,
//                             final byte protocolId,
//                             final byte priorityFlag,
//                             final String scheduleDeliveryTime,
//                             final String validityPeriod,
//                             final RegisteredDelivery registeredDelivery,
//                             final byte replaceIfPresentFlag,
//                             final DataCoding dataCoding,
//                             final byte smDefaultMsgId,
//                             final byte[] shortMessage,
//                             final OptionalParameter... optionalParameters
//  ) throws Exception {
//    final Connection connection = pooledSmppSessions.get(connectionId);
//    final SMPPSession smppSession = connection.getSession();
//    try {
//      try {
//        final String messageId = smppSession.submitShortMessage(
//            connection.getServiceType() != null ? connection.getServiceType() : serviceType,
//            sourceAddrTon, sourceAddrNpi, sourceAddr,
//            destAddrTon, destAddrNpi, destAddr,
//            esmClass,
//            protocolId, priorityFlag,
//            scheduleDeliveryTime, validityPeriod, registeredDelivery,
//            replaceIfPresentFlag,
//            dataCoding,
//            smDefaultMsgId,
//            shortMessage,
//            optionalParameters);
//        LOG.debug("Submitted message ID {} on session {}", messageId, smppSession.getSessionId());
//        return messageId;
//      } catch (ResponseTimeoutException e) {
//        LOG.error("Response timeout: {}", e.getMessage());
//      } catch (NegativeResponseException e) {
//        LOG.error("Negative response: {}", e.getMessage());
//      } catch (InvalidResponseException e) {
//        LOG.error("Invalid response: {}", e.getMessage());
//      } catch (PDUException e) {
//        LOG.error("PDU exception: {}", e.getMessage());
//      } catch (IOException e) {
//        LOG.error("IO exception: {}", e.getMessage());
//      } finally {
//      }
//    } catch (Exception ee) {
//      LOG.error("Error in pool", ee);
//    }
//    return null;
//  }

  public String sendSubmitMulti(final String connectionId) throws Exception {
    final Connection connection = pooledSmppSessions.get(connectionId);
    final PooledSMPPSession<ThrottledSMPPSession> pooledSMPPSession = connection.getPooledSMPPSession();
    try {
      ThrottledSMPPSession throttledSMPPSession = null;
      try {

        Address address1 = new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "628176504657");
        Address address2 = new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "628176504658");
        Address[] addresses = new Address[]{ address1, address2 };

        throttledSMPPSession = pooledSMPPSession.borrowObject();

        SubmitMultiResult result = throttledSMPPSession.submitMultiple("CMT", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "1616",
            addresses, new ESMClass(), (byte) 0, (byte) 1, TIME_FORMATTER.format(new Date()), null,
            new RegisteredDelivery(SMSCDeliveryReceipt.FAILURE), ReplaceIfPresentFlag.REPLACE,
            new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte) 0,
            "jSMPP simplifies SMPP on Java platform".getBytes());
        log.info("{} messages submitted, result message id {}", addresses.length, result.getMessageId());
        for (UnsuccessDelivery unsuccessDelivery : result.getUnsuccessDeliveries()) {
          log.info("Unsuccessful delivery to {}: {}", unsuccessDelivery.getDestinationAddress(), unsuccessDelivery.getErrorStatusCode());
        }

        return "73746567346573249";
      } catch (ResponseTimeoutException e) {
        log.error("Response timeout: {}", e.getMessage());
      } catch (NegativeResponseException e) {
        log.error("Negative response: {}", e.getMessage());
      } catch (InvalidResponseException e) {
        log.error("Invalid response: {}", e.getMessage());
      } catch (PDUException e) {
        log.error("PDU exception: {}", e.getMessage());
      } catch (IOException e) {
        log.error("IO exception: {}", e.getMessage());
      } finally {
        pooledSMPPSession.returnObject(throttledSMPPSession);
      }
    } catch (Exception ee) {
      log.error("Error in SMPP connection pool", ee);
    }
    return null;
  }

  public Map<String, Connection> getPooledSmppSessions() {
    return pooledSmppSessions;
  }

  public Connection getSmppConnection(final String id) {
    return pooledSmppSessions.get(id);
  }

  private MessageReceiverListener createMessageReceiverListenerBean(final String connectionId,
                                                                    final Charset defaultCharset,
                                                                    final SmsService smsService,
                                                                    final ApplicationEventPublisher applicationEventPublisher) {
    final AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
    final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
    final BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MessageReceiverListenerImpl.class);
    definitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
    definitionBuilder.addConstructorArgValue(connectionId);
    definitionBuilder.addConstructorArgValue(defaultCharset);
    definitionBuilder.addConstructorArgValue(smsService);
    definitionBuilder.addConstructorArgValue(this);
    definitionBuilder.addConstructorArgValue(applicationEventPublisher);
    final String beanName = "messageReceiverListenerImpl" + connectionId;
    registry.registerBeanDefinition(beanName, definitionBuilder.getBeanDefinition());

    log.debug("Create MessageReceiverListenerImpl bean with name {}", beanName);
    final MessageReceiverListenerImpl messageReceiverListener = applicationContext.getBean(beanName, MessageReceiverListenerImpl.class);
    return messageReceiverListener;
  }

  public String getConnectionContextId(final String connectionId) throws ConnectionNotFoundException {
    for(final SmppConfiguration.SmppConnection smppConnection : smppConfiguration.getConnections())
    {
      if (StringUtils.equals(smppConnection.getId(), connectionId)) {
        return smppConnection.getContextId();
      }
    }
    throw new ConnectionNotFoundException("The connection " + connectionId + " was not found");
  }

  public List<String> getConnectionsInSameContext(final String connectionId) throws ConnectionNotFoundException {
    final String contextId = getConnectionContextId(connectionId);
    final List<String> connectionIds = new ArrayList<>();
    final Iterator<SmppConfiguration.SmppConnection> iterator = smppConfiguration.getConnections().iterator();
    while (iterator.hasNext()) {
      final SmppConfiguration.SmppConnection smppConnection = iterator.next();
      if (StringUtils.equals(smppConnection.getContextId(), contextId)) {
        connectionIds.add(smppConnection.getId());
      }
    }
    return connectionIds;
  }

}
