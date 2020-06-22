var socket = null;
var stompClient = null;
var recInterval = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  }
  else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  socket = new SockJS('websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    stompClient.subscribe('/topic/greetings', function (greeting) {
      if (JSON.parse(greeting.body).moreMessagesToSend) {
        t = ''
      }
      showSms(
          JSON.parse(greeting.body).serviceType,
          JSON.parse(greeting.body).smscAddress,
          JSON.parse(greeting.body).dataCodingScheme,
          JSON.parse(greeting.body).protocolId,
          JSON.parse(greeting.body).originatorAddress,
          JSON.parse(greeting.body).moreMessagesToSend,
          JSON.parse(greeting.body).userDataHeaderIndicator,
          JSON.parse(greeting.body).replyPath,
          JSON.parse(greeting.body).userDataHeader,
          JSON.parse(greeting.body).message
      );
    });
    stompClient.subscribe('/topic/smsrow', function (greeting) {
      console.log(greeting.body);
      showSmsRow(greeting.body);
    });
  });

  window.clearInterval(recInterval);

  socket.onclose = function () {
    console.log('socket closed');
    recInterval = window.setInterval(function () {
      connect();
    }, 2000);
  };
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  stompClient.send("/app/hello", {}, JSON.stringify({'name': 'Pim'}));
}

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showSms(serviceType, smscAddress, dataCodingScheme, protocolId, originatorAddress, moreMessagesToSend, userDataHeaderIndicator, replyPath, userDataHeader, message) {
  $("#sms").append(
      "<tr>" +
      "<td>" + smscAddress + "</td>" +
      "<td>" + moreMessagesToSend + "</td>" +
      "<td>" + userDataHeaderIndicator + "</td>" +
      "<td>" + replyPath + "</td>" +
      "<td>" + dataCodingScheme + "</td>" +
      "<td>" + protocolId + "</td>" +
      "<td>" + originatorAddress + "</td>" +
      "<td>" + userDataHeader + "</td>" +
      "<td>" + message + "</td>" +
      "</tr>");
}

function showSmsRow(row) {
  // $("#sms tbody").prepend("<tr />").children('tr:first').append(row);
  $("#sms tr:first").after(row);
}

$(function () {
  $("form").on('submit', function (e) {
    e.preventDefault();
  });
  $("#connect").click(function () {
    connect();
  });
  $("#disconnect").click(function () {
    disconnect();
  });
  $("#send").click(function () {
    sendName();
  });
});

function executeQuery() {
  // $.ajax({
  //   url: 'url/path/here',
  //   success: function (data) {
  //     // do something with the return value here if you like
  //   }
  // });
  sendName();
  setTimeout(executeQuery, 5000); // you could choose not to continue on failure...
}

$(document).ready(function () {
  // run the first time; all subsequent calls will take care of themselves
  connect();
  // setTimeout(executeQuery, 5000);
});