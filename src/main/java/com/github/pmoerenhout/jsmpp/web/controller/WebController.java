package com.github.pmoerenhout.jsmpp.web.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import com.github.pmoerenhout.jsmpp.web.Util;
import com.github.pmoerenhout.jsmpp.web.event.SmInEvent;
import com.github.pmoerenhout.jsmpp.web.jpa.model.SmIn;
import com.github.pmoerenhout.jsmpp.web.jpa.repository.DrRepository;
import com.github.pmoerenhout.jsmpp.web.jpa.repository.SmInRepository;
import com.github.pmoerenhout.jsmpp.web.model.DeliverWebDto;
import com.github.pmoerenhout.jsmpp.web.model.Greeting;
import com.github.pmoerenhout.jsmpp.web.model.HelloMessage;
import com.github.pmoerenhout.jsmpp.web.smpp.util.SmppUtil;
import lombok.extern.slf4j.Slf4j;

// @Secured(AUTHORITY_API)
@Slf4j
@EnableScheduling
@Controller
public class WebController {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private DrRepository drRepository;
  @Autowired
  private SmInRepository smInRepository;
  @Autowired
  private SimpMessagingTemplate template;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String getHome(final Authentication authentication, final ModelMap modelMap, final HttpServletRequest request) {
    modelMap.addAttribute("name", authentication.getPrincipal());
    return "home";
  }

  @RequestMapping(value = "/show", method = RequestMethod.GET)
  public String getRepository(final Authentication authentication, final ModelMap modelMap, final HttpServletRequest request) {
    final List<SmIn> smIns = smInRepository.findAll(Sort.by(Sort.Direction.DESC, "smscTimestamp"));
    final List<DeliverWebDto> delivers = new ArrayList<>();
    smIns.forEach(d -> {
      final String message = SmppUtil.isBinary(d.getDataCodingScheme()) ?
          Util.bytesToHexString(d.getShortMessage()) :
          SmppUtil.decode(d.getDataCodingScheme(), d.getEsmClass(), d.getShortMessage(), SmppUtil.GSM_PACKED_CHARSET);
      final String userDataHeader = GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()) ?
          Util.bytesToHexString(ArrayUtils.subarray(d.getShortMessage(), 1, 1 + d.getShortMessage()[0]))
          : "";
      delivers.add(
          new DeliverWebDto(
              "PDU: " + Util.bytesToHexString(RandomUtils.nextBytes(50)),
              d.getServiceType(),
              d.getSmscTimestamp(),
              getAddress(d.getSmscAddressTon(), d.getSmscAddressNpi(), d.getSmscAddress()),
              d.getDataCodingScheme(), d.getProtocolIdentifier(),
              getAddress(d.getSourceTon(), d.getSourceNpi(), d.getSource()),
              d.isMoreMessagesToSend(), GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()), GSMSpecificFeature.REPLYPATH.containedIn(d.getEsmClass()),
              userDataHeader, message));
    });
    // KPN Prepaid
    //modelMap.addAttribute("msisdn", "+31682346962");
    // T-Mobile Prepaid
    modelMap.addAttribute("msisdn", "+31642791436");
    modelMap.addAttribute("sms", delivers);
    return "show";
  }

  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(final HelloMessage message) throws Exception {
    return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
  }

//  @Scheduled(fixedRate = 5000)
//  // @MessageMapping("/hello")
//  @SendTo("/topic/greetings")
//  public Greeting scheduledGreeting() throws Exception {
//    LOG.info("scheduledGreeting");
//    return new Greeting("scheduledGreeting");
//  }

  private String getAddress(final byte ton, final byte npi, final String address) {
    final StringBuilder sb = new StringBuilder();
    sb.append(TypeOfNumber.valueOf(ton).name());
    sb.append('/');
    sb.append(NumberingPlanIndicator.valueOf(npi).name());
    sb.append(' ');
    sb.append(address);
    return sb.toString();
  }

  //ObjectMapper mapper = new ObjectMapper();
  //@Scheduled(fixedRate = 5000)
  public void sendSms() {
    log.info("sendSms");
    final List<SmIn> smIns = smInRepository.findAll();
    log.info("sendSms {}", smIns.size());
    smIns.forEach(d -> {
      final String message = SmppUtil.isBinary(d.getDataCodingScheme()) ?
          Util.bytesToHexString(d.getShortMessage()) :
          SmppUtil.decode(d.getDataCodingScheme(), d.getEsmClass(), d.getShortMessage(), SmppUtil.GSM_PACKED_CHARSET);
      log.info("message: {}", message);
      final String userDataHeader = GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()) ?
          Util.bytesToHexString(ArrayUtils.subarray(d.getShortMessage(), 1, 1 + d.getShortMessage()[0]))
          : "";
//      this.template.convertAndSend("/topic/greetings",
//          new DeliverWebDto(
//              d.getServiceType(),
//              getAddress(d.getSmscAddressTon(), d.getSmscAddressNpi(), d.getSmscAddress()),
//              d.getDataCodingScheme(), d.getProtocolIdentifier(),
//              getAddress(d.getSourceTon(), d.getSourceNpi(), d.getSource()),
//              d.isMoreMessagesToSend(), GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()), GSMSpecificFeature.REPLYPATH.containedIn(d.getEsmClass()),
//              userDataHeader, message)
//      );

      this.template.convertAndSend("/topic/smsrow",

          createRow(
              createRowData(d.getSmscTimestamp()) +
                  createRowData(getAddress(d.getSmscAddressTon(), d.getSmscAddressNpi(), d.getSmscAddress())) +
                  createRowData(d.isMoreMessagesToSend()) +
                  createRowData(GSMSpecificFeature.UDHI.containedIn(d.getEsmClass())) +
                  createRowData(GSMSpecificFeature.REPLYPATH.containedIn(d.getEsmClass())) +
                  createRowData(d.getDataCodingScheme()) +
                  createRowData(d.getProtocolIdentifier()) +
                  createRowData(getAddress(d.getSourceTon(), d.getSourceNpi(), d.getSource())) +
                  createRowData(userDataHeader) +
                  createRowData(message))
      );
    });
    // this.template.convertAndSend("/topic/greetings", new Greeting("scheduledGreetingsssssss222"));
  }

  //ObjectMapper mapper = new ObjectMapper();
  //@Scheduled(fixedRate = 5000)
  @EventListener
  public void sendStomp(final SmInEvent event) {
    log.info("smIn from {}", event.getConnectionId());
    final SmIn d = event.getSm();
    final String message = SmppUtil.isBinary(d.getDataCodingScheme()) ?
        Util.bytesToHexString(d.getShortMessage()) :
        SmppUtil.decode(d.getDataCodingScheme(), d.getEsmClass(), d.getShortMessage(), SmppUtil.GSM_PACKED_CHARSET);
    log.info("message: '{}'", Util.onlyPrintable(message.getBytes()));
    final String userDataHeader = GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()) ?
        Util.bytesToHexString(ArrayUtils.subarray(d.getShortMessage(), 1, 1 + d.getShortMessage()[0]))
        : "";
//      this.template.convertAndSend("/topic/greetings",
//          new DeliverWebDto(
//              d.getServiceType(),
//              getAddress(d.getSmscAddressTon(), d.getSmscAddressNpi(), d.getSmscAddress()),
//              d.getDataCodingScheme(), d.getProtocolIdentifier(),
//              getAddress(d.getSourceTon(), d.getSourceNpi(), d.getSource()),
//              d.isMoreMessagesToSend(), GSMSpecificFeature.UDHI.containedIn(d.getEsmClass()), GSMSpecificFeature.REPLYPATH.containedIn(d.getEsmClass()),
//              userDataHeader, message)
//      );

    this.template.convertAndSend("/topic/smsrow",

        createRow(
            createRowData(d.getSmscTimestamp()) +
                createRowData(getAddress(d.getSmscAddressTon(), d.getSmscAddressNpi(), d.getSmscAddress())) +
                createRowData(d.isMoreMessagesToSend()) +
                createRowData(GSMSpecificFeature.UDHI.containedIn(d.getEsmClass())) +
                createRowData(GSMSpecificFeature.REPLYPATH.containedIn(d.getEsmClass())) +
                createRowDataDcs(d.getDataCodingScheme()) +
                createRowData(d.getProtocolIdentifier()) +
                createRowData(getAddress(d.getSourceTon(), d.getSourceNpi(), d.getSource())) +
                createRowData(userDataHeader) +
                createRowData(message))
    );
  }

  private String createRow(final String row) {
    return "<tr>" + row + "</tr>";
  }

  private String createRowData(final String text) {
    return "<td>" + text + "</td>";
  }

  private String createRowData(final byte b) {
    return "<td>" + Util.bytesToHexString(b) + "</td>";
  }

  private String createRowDataDcs(final byte b) {
    return "<td title=\"" + dcsAsString(b) + "\">" + Util.bytesToHexString(b) + "</td>";
  }

  private String createRowData(final boolean b) {
    return "<td><span class=\"far " + (b ? "fa-check-square text-success" : "fa-square text-danger") + "\"></span></td>";
  }

  private String createRowData(final ZonedDateTime zonedDateTime) {
    return "<td>" + zonedDateTime.format(DATE_TIME_FORMATTER) + "</td>";
  }

  private String createSquare(final boolean b) {
    if (b) {
      return "<span class=\"far fa-check-square text-success'\"></span>";
    }
    return "<span class=\"far fa-square text-danger'\"></span>";
  }

  private String dcsAsString(final byte dcs) {
    //https://en.wikipedia.org/wiki/Data_Coding_Scheme
    switch (dcs) {
      case 0x00:
      case 0x01:
      case 0x02:
      case 0x03:
        return "GSM 7 bit, default Message Class";
      case 0x04:
      case 0x05:
      case 0x06:
      case 0x07:
        return "8 bit data, default Message Class";
      case 0x08:
      case 0x09:
      case 0x0a:
      case 0x0b:
        return "UCS2, default Message Class";
      case (byte) 0xf0:
        return "GSM 7 bit, Class 0 (Flash message)";
      case (byte) 0xf1:
        return "GSM 7 bit, Class 1 (ME-specific)";
      case (byte) 0xf2:
        return "GSM 7 bit, Class 2 (SIM/USIM-specific)";
      case (byte) 0xf3:
        return "GSM 7 bit, Class 3 (TE-specific)";
      case (byte) 0xf4:
        return "8 bit data, Class 0 (Flash message)";
      case (byte) 0xf5:
        return "8 bit data, Class 1 (ME-specific)";
      case (byte) 0xf6:
        return "8 bit data, Class 2 (SIM/USIM-specific)";
      case (byte) 0xf7:
        return "8 bit data, Class 3 (TE-specific)";
      default:
        return "Unknown DCS";
    }
  }
}
