/*
 * Sample node.js code for AWS Lambda to upload the JSON documents
 * pushed from Kinesis to Amazon Elasticsearch.
 *
 *
 * Copyright 2015- Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at http://aws.amazon.com/asl/
 * or in the "license" file accompanying this file.  This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * express or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

/* == Imports == */
var AWS = require('aws-sdk');
var path = require('path');

/* == Globals == */
var esDomain = {
    region: 'us-east-1',
    endpoint: 'search-vehicledata-mk7cr3lmcl4ghbew7y2e7dehji.us-east-1.es.amazonaws.com',
    index: 'vehicle6',
    doctype: 'vehicledata'
};
var endpoint = new AWS.Endpoint(esDomain.endpoint);
/*
 * The AWS credentials are picked up from the environment.
 * They belong to the IAM role assigned to the Lambda function.
 * Since the ES requests are signed using these credentials,
 * make sure to apply a policy that allows ES domain operations
 * to the role.

Implementation assumes that the following vehicle data payload is retrieved from Kinesis

{"state":{"reported":{"vin":"3dd0b73a-4e70-4b0c-8bb6-e72048f705be","prndl":"DRIVE","gps":{"speed":80.0,"latitudeDegrees":42.3574911198,"longitudeDegrees":-71.16254944,"heading":173},"externalTemperature":23.0}}}

Function adds timestamp and pin location to event to simplify the indexing of geopoints in ElasticSearch

{
    "state": {
        "reported": {
            "vin": "3dd0b73a-4e70-4b0c-8bb6-e72048f705be",
            "prndl": "DRIVE",
            "gps": {
                "speed": 80,
                "latitudeDegrees": 42.3577775399,
                "longitudeDegrees": -71.1535898969,
                "heading": 173
            },
            "externalTemperature": 23,
            "timestamp": "2016/1/2 17:51:16",
            "pin": {
                "location": {
                    "lat": 42.3577775399,
                    "lon": -71.1535898969
                }
            }
        }
    }
}

 */
var creds = new AWS.EnvironmentCredentials('AWS');


/* Lambda "main": Execution begins here */
exports.handler = function(event, context) {
   console.log(JSON.stringify(event, null, '  '));
    //console.log(JSON.stringify(context, null, '  '));
    event.Records.forEach(function(record) {
        record.kinesis.data.timestamp = Date.now();
        var jsonDoc = new Buffer(record.kinesis.data, 'base64');
        var jsonObj = JSON.parse(jsonDoc);

        var now = new Date();
        var month = now.getMonth()+1;
        var h, m, s;

        if (now.getHours() < 10) {
            h = "0" + now.getHours().toString();
        } else {
            h = now.getHours().toString();
        }
        if (now.getMinutes() < 10) {
            m = "0" +now.getMinutes().toString();
        } else {
            m = now.getMinutes().toString();
        }
        if (now.getSeconds() < 10) {
            s = "0" +  now.getSeconds().toString();
        } else {
            s =  now.getSeconds().toString();
        }

        jsonObj.state.reported.timestamp = now.getFullYear() + '/' + month + '/' + now.getDate() + ' ' + h + ':' + m + ':' + s;
        var lat = jsonObj.state.reported.gps.latitudeDegrees;
        var lon = jsonObj.state.reported.gps.longitudeDegrees;
        var location = {};
        location["lat"] = lat;
        location["lon"] = lon;
        var pin = {};
        pin["location"]=location;
        jsonObj.state.reported.pin = pin;
        console.log(JSON.stringify(jsonObj, null, '  '));
        postToES(JSON.stringify(jsonObj, null, '  '), context);

    });
}


/*
 * Post the given document to Elasticsearch
 */
function postToES(doc, context) {
    var req = new AWS.HttpRequest(endpoint);

    req.method = 'POST';
    req.path = path.join('/', esDomain.index, esDomain.doctype);
    req.region = esDomain.region;
    req.headers['presigned-expires'] = false;
    req.headers['Host'] = endpoint.host;
    req.body = doc;

    console.log(req.body);


    var signer = new AWS.Signers.V4(req , 'es');  // es: service code
    signer.addAuthorization(creds, new Date());

    var send = new AWS.NodeHttpClient();
    send.handleRequest(req, null, function(httpResp) {
        var respBody = '';
        httpResp.on('data', function (chunk) {
            respBody += chunk;
        });
        httpResp.on('end', function (chunk) {
            console.log('Response: ' + respBody);
            context.succeed('Lambda added document ' + doc);
        });
    }, function(err) {
        console.log('Error: ' + err);
        context.fail('Lambda failed with error ' + err);
    });
}
