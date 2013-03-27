(function() {
  window.addEventListener("message", function(event) {
    if (event.data === "hello") {
      var command = "client-frame:" + JSON.stringify({
        url: window.location.href
      });
      event.source.postMessage(command, "*");
    }
  }, false);
}());
