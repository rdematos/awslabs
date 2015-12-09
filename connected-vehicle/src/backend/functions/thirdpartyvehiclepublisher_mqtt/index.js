
var mqtt = require('mqtt');
var HOST = 'mqtt://iot.eclipse.org',
    PORT = 1883,        // Use Port 8883 if you're licensed for SSL
    TOPIC = '9ddberufbghspoo/vehicle';    

var options = {
  protocolVersion: 3,
  qos: 1,
  clientId: "thirdpartyvehiclepublisher"
};
 
exports.handler = function(event, context) {
var client  = mqtt.connect(HOST, PORT, options);
client.on('connect', function () {
  client.publish(TOPIC, JSON.stringify(event));
  client.end();
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
