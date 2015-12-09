var aws = require('aws-sdk');
var geohash = require('ngeohash');
var jsonPath = require('JSONPath');
var TOPIC_ROOT = 'notification';


//var iot = new aws.Service({apiConfig: require('./iot-service-model.json'), endpoint: "t71u6yob51.execute-api.us-east-1.amazonaws.com/beta" });
//var iotData = new aws.Service({apiConfig: require('./iot-data-service-model.json'), endpoint: "g.us-east-1.pb.iot.amazonaws.com" });

var iotData = new aws.Service({apiConfig: require('./iot-data-service-model.json'), endpoint: "A3CJ7IIGECQ7LO.iot.us-east-1.amazonaws.com" });

exports.handler = function(event, context) {

  var LAT = jsonPath.eval(event, '$.state.reported.gps.latitudeDegrees');
  console.log(LAT);
  var LON = jsonPath.eval(event, '$.state.reported.gps.longitudeDegrees');
  console.log(LON);
  var GEO = geohash.encode(LAT, LON, precision=7);
  console.log(GEO);
  var TOPIC = TOPIC_ROOT+"/"+GEO.substring(0, 1)+"/"+GEO.substring(1, 2)+"/"+GEO.substring(2, 3)+"/"+GEO.substring(3, 4)+"/"+GEO.substring(4, 5)+"/"+GEO.substring(5, 6)+"/"
  +GEO.substring(6,7);
  console.log(TOPIC);


//TODO: check ddb if event has been notified; if not, notify
  var params = {
        "topic" : TOPIC,
        "payload" : JSON.stringify(event)
    };

    iotData.publish(params, function(err, data) {
      console.log(err, data);
		context.done();
    });
};
