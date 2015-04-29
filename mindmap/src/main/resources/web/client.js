var eb = new vertx.EventBus(
    window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus');
eb.onopen = function() {
    console.log("Event bus connected");
};