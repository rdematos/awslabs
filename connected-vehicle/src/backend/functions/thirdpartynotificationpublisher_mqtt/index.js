
var mqtt = require('mqtt');
var HOST = 'mqtt://iot.eclipse.org',
    PORT = 1883,        // Use Port 8883 if you're licensed for SSL
    TOPIC = '9ddberufbghspoo/notification';
    

var options = {
  protocolVersion: 3,
    qos: 1,
    keepalive: 30,
    clientId: "thirdpartynotificationpub"
};
 
exports.handler = function(event, context) {
var client  = mqtt.connect(HOST, PORT, options);
client.on('connect', function(err, output) {
  client.publish(TOPIC, JSON.stringify(event));
  client.end();
  if (err) {
            console.log('pub failed:', err);
            context.fail(err);
        } else {
            console.log('pub succeed');
            context.succeed(output);
        }
});

}


var context = {
  done: function contextDoneHandler(err, message) {
    if (err) {
      console.log('Event completed with error ' + err);
    } else {
      console.log('Event executed successfully');
    }
  }
};

exports.handler(null,context);
