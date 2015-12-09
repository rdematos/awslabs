console.log('Loading event');
//Required for SQS

var QUEUE_URL = 'https://sqs.us-east-1.amazonaws.com/673485280914/thirdpartynotification'; 
//var QUEUE_URL = https://sqs.us-east-1.amazonaws.com/673485280914/here   //for use when transformation is ready
var AWS = require('aws-sdk');
var sqs = new AWS.SQS({region : 'us-east-1'});
var http = require('https');

exports.handler = function(event, context) {

  //Send SQS message with details of file uploaded to S3.
  var params = {
    MessageBody: JSON.stringify(event),
    QueueUrl: QUEUE_URL
  };

  sqs.sendMessage(params, function(err,data){

    if(err) {
      console.log('error:',"Fail Send Message" + err);
      context.done('error', "ERROR Put SQS");  // ERROR with message

    }else{

      console.log('data:',data.MessageId);
      context.done(null,'');  // SUCCESS
    }
  });
}