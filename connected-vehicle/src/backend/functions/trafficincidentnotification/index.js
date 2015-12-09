console.log('loading function');
var QUEUE_URL = 'https://sqs.us-east-1.amazonaws.com/673485280914/thirdpartynotification';
//var QUEUE_URL = 'https://sqs.us-east-1.amazonaws.com/673485280914/here';
var AWS = require('aws-sdk');
var sqs = new AWS.SQS({region : 'us-east-1'});
var moment = require('moment');

exports.handler = function(event, context) {
  //extract lat/lon from event
  var lat=event.state.reported.gps.latitudeDegrees;
  var lon=event.state.reported.gps.longitudeDegrees;

  //get the date and add 1 minute the expiration time
  var endtime = moment.utc().minutes(moment.utc().minutes()+5).format("MM/DD/YYYY HH:mm:ss");

  //set incidents id to vehicle VIN to avoid duplicate
  //var trafficitemid = event.state.reported.vin;
  var trafficitemid = "1000000";


  var xmloutput=
    '<TRAFFICML_INCIDENTS EXTENDED_COUNTRY_CODE="A0" VERSION="2.2">\n'
    + '  <TRAFFIC_ITEMS>\n'
    + '    <TRAFFIC_ITEM>\n'
    + '      <ORIGINAL_TRAFFIC_ITEM_ID>'+trafficitemid+'</ORIGINAL_TRAFFIC_ITEM_ID>\n'
    + '      <END_TIME>'+endtime+'</END_TIME>\n'
    + '        <CRITICALITY/>\n'
    + '        <RDS-TMC_LOCATIONS>\n'
    + '          <RDS-TMC>\n'
    + '            <ORIGIN>\n'
    + '              <EBU_COUNTRY_CODE>1</EBU_COUNTRY_CODE>\n'
    + '              <TABLE_ID>20</TABLE_ID>\n'
    + '            </ORIGIN>\n'
    + '          </RDS-TMC>\n'
    + '      </RDS-TMC_LOCATIONS>\n'
    + '      <LOCATION>\n'
    + '        <GEOLOC>\n'
    + '          <ORIGIN>\n'
    + '            <LATITUDE>'+lat+'</LATITUDE>\n'
    + '            <LONGITUDE>'+lon+'</LONGITUDE>\n'
    + '          </ORIGIN>\n'
    + '        </GEOLOC>\n'
    + '      </LOCATION>\n'
    + '    </TRAFFIC_ITEM>\n'
    + '  </TRAFFIC_ITEMS>\n'
    + '</TRAFFICML_INCIDENTS>'

    console.log(xmloutput);


  var params = {
    MessageBody: xmloutput,
    QueueUrl: QUEUE_URL
  };
  sqs.sendMessage(params, function(err,data){
   if(err) {
     console.log('error:',"Fail Send Message" + err);
     context.done('error', "ERROR Put SQS");  // ERROR with message
   }else{
     console.log('data:',data.MessageId);
     console.log('queue:',QUEUE_URL);
     context.done(null,'');  // SUCCESS
   }
  });
}
