var geohash = require('ngeohash');
var jsonPath = require('JSONPath');
var mqtt = require('mqtt');
var HOST = 'mqtt://iot.eclipse.org',
    PORT = 1883,
    TOPIC_ROOT = '9ddberufbghspoo/notification';
    

var options = {
  protocolVersion: 3,
    keepalive: 30,
    clientId: "thirdpartygeonotificationpublisher_mqtt"
};
 
exports.handler = function(event, context) {
var client  = mqtt.connect(HOST, PORT, options);
client.on('connect', function () {
	var LAT = jsonPath.eval(event, '$.state.reported.gps.latitudeDegrees');
  console.log(LAT);
  var LON = jsonPath.eval(event, '$.state.reported.gps.longitudeDegrees');
  console.log(LON);
  var GEO = geohash.encode(LAT, LON, precision=9);
  console.log(GEO);
  var TOPIC = TOPIC_ROOT+"/"+GEO.substring(0, 1)+"/"+GEO.substring(1, 2)+"/"+GEO.substring(2, 3)+"/"+GEO.substring(3, 4)+"/"
  +GEO.substring(4, 5)+"/"+GEO.substring(5, 6)+"/"+GEO.substring(7, 8);
  console.log(TOPIC);
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
