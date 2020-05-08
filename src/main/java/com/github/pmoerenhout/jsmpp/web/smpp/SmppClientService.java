package com.github.pmoerenhout.jsmpp.web.smpp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SessionStateListener;
import org.jsmpp.session.connection.socket.SocketConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.github.pmoerenhout.jsmpp.web.exception.ConnectionNotFoundException;
import com.github.pmoerenhout.jsmpp.web.sms.SmsService;

@Service
public class SmppClientService implements ApplicationContextAware {

  private static final Logger LOG = LoggerFactory.getLogger(SmppClientService.class);

  private ApplicationContext applicationContext;

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  private SmppConfiguration smppConfiguration;

  private Map<String, Connection> pooledSmppSessions = new HashMap<>();

  @Autowired
  private SessionStateListener sessionStateListener;

  @Autowired
  private SmsService smsService;

  @Autowired
  private SmppClientService smppClientService;

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @EventListener
  public void start(final ApplicationReadyEvent event) {
    if (event.getApplicationContext().getParent() != null) {
      return;
    }
    LOG.info("Received an application ready event ({}), starting SMPP connections", event.getApplicationContext());
    final List<SmppConfiguration.SmppConnection> smppConnectionList = smppConfiguration.getConnections();
    for (SmppConfiguration.SmppConnection smppConnection : smppConnectionList) {
      final String connectionId = smppConnection.getId();
      if (!smppConnection.isEnabled()) {
        LOG.info("SMPP connection '{}' is not enabled, skipping...", connectionId);
        continue;
      }
      try {
        final String contextId = smppConnection.getContextId();
        final String description = smppConnection.getDescription();
        final String serviceType = smppConnection.getServiceType();
        final Charset defaultCharset = smppConnection.getCharset();
        final boolean transmitable = smppConnection.getBindType().isTransmitable();
        final boolean longSmsEnabled = smppConnection.isLongSmsEnabled();
        final int longSmsMaxSize = smppConnection.getLongSmsMaxSize();
        final Integer singleSmsMaxSize = smppConnection.getSingleSmsMaxSize();

        final MessageReceiverListener messageReceiverListener = createMessageReceiverListenerBean(connectionId, defaultCharset, smsService, applicationEventPublisher);
        // this.sessionStateListener = new SessionStateListenerImpl();

        final SMPPSession session = new SMPPSession(SocketConnectionFactory.getInstance());
        session.setMessageReceiverListener(messageReceiverListener);
        session.setEnquireLinkTimer(smppConnection.getEnquireLinkTimer());
        session.connectAndBind(smppConnection.getHost(),
            smppConnection.getPort(),
            new BindParameter(smppConnection.getBindType(),
                smppConnection.getSystemId(),
                smppConnection.getPassword(),
                smppConnection.getSystemType(), TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, ""));

        final Connection connection = new Connection(connectionId, contextId, description, serviceType, defaultCharset, transmitable,
            longSmsEnabled, longSmsMaxSize, singleSmsMaxSize, session);
        pooledSmppSessions.put(connectionId, connection);
        LOG.info(
            "Created pooled SMPP session for connection '{}' '{}' ({}) with serviceType {}, charset {}, transmitable {}, longSms:{} {}, sms size {}",
            connectionId, contextId, description, serviceType, defaultCharset, transmitable, longSmsEnabled, longSmsMaxSize, singleSmsMaxSize);
      } catch (Exception e) {
        LOG.error("Could not create the SMPP session with connection '" + connectionId + "' to " + smppConnection.getHost() + ':' + smppConnection.getPort(),
            e);
      }
    }
  }

  public String sendSubmitSm(final String connectionId,
                             final String serviceType,
                             final TypeOfNumber sourceAddrTon,
                             final NumberingPlanIndicator sourceAddrNpi,
                             final String sourceAddr,
                             final TypeOfNumber destAddrTon,
                             final NumberingPlanIndicator destAddrNpi,
                             final String destAddr,
                             final ESMClass esmClass,
                             final byte protocolId,
                             final byte priorityFlag,
                             final String scheduleDeliveryTime,
                             final String validityPeriod,
                             final RegisteredDelivery registeredDelivery,
                             final byte replaceIfPresentFlag,
                             final DataCoding dataCoding,
                             final byte smDefaultMsgId,
                             final byte[] shortMessage,
                             final OptionalParameter... optionalParameters
  ) throws Exception {
    final Connection connection = pooledSmppSessions.get(connectionId);
    final SMPPSession smppSession = connection.getSession();
    try {
      try {
        final String messageId = smppSession.submitShortMessage(
            connection.getServiceType() != null ? connection.getServiceType() : serviceType,
            sourceAddrTon, sourceAddrNpi, sourceAddr,
            destAddrTon, destAddrNpi, destAddr,
            esmClass,
            protocolId, priorityFlag,
            scheduleDeliveryTime, validityPeriod, registeredDelivery,
            replaceIfPresentFlag,
            dataCoding,
            smDefaultMsgId,
            shortMessage,
            optionalParameters);
        LOG.debug("Submitted message ID {} on session {}", messageId, smppSession.getSessionId());
        return messageId;
      } catch (ResponseTimeoutException e) {
        LOG.error("Response timeout: {}", e.getMessage());
      } catch (NegativeResponseException e) {
        LOG.error("Negative response: {}", e.getMessage());
      } catch (InvalidResponseException e) {
        LOG.error("Invalid response: {}", e.getMessage());
      } catch (PDUException e) {
        LOG.error("PDU exception: {}", e.getMessage());
      } catch (IOException e) {
        LOG.error("IO exception: {}", e.getMessage());
      } finally {
      }
    } catch (Exception ee) {
      LOG.error("Error in pool", ee);
    }
    return null;
  }

  public Map<String, Connection> getSmppSessions() {
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
    definitionBuilder.addConstructorArgValue(smppClientService);
    definitionBuilder.addConstructorArgValue(applicationEventPublisher);
    final String beanName = "messageReceiverListenerImpl" + connectionId;
    registry.registerBeanDefinition(beanName, definitionBuilder.getBeanDefinition());

    LOG.debug("Create MessageReceiverListenerImpl bean with name {}", beanName);
    final MessageReceiverListenerImpl messageReceiverListener = applicationContext.getBean(beanName, MessageReceiverListenerImpl.class);
    return messageReceiverListener;
  }

  public String getConnectionContextId(final String connectionId) throws ConnectionNotFoundException {
    final Iterator<SmppConfiguration.SmppConnection> iterator = smppConfiguration.getConnections().iterator();
    while (iterator.hasNext()) {
      final SmppConfiguration.SmppConnection smppConnection = iterator.next();
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
