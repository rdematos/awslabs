/*
 *  Copyright 2015 2lemetry, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */



var q = require('q'),
  mqtt = require('mqtt'),
  md5 = require('MD5'),
  uuid = require('node-uuid');
var errorMappings = {
  'Connection refused: Bad username or password': 'exception',
  'connect ECONNREFUSED': 'exception',
  'getaddrinfo ENOENT': 'exception'
};
var parseError = function(error) {
  error = error.toString();
  if (error.indexOf('[') !== -1) {
    var start = error.indexOf('[') + 1;
    var end = error.indexOf(']');
    error = error.substring(start, end);
  }
  return error.replace('Error: ', '');
};
var parse = function(topic, message) {
  try {
    var message = JSON.parse(message);
    var stuff = topic.split('/')[1];
    var thing = topic.split('/')[2];
    return {
      stuff: stuff,
      thing: thing,
      message: message
    };
  } catch (error) {
    return console.log('Could not parse Mqtt message received as JSON on topic ' + topic);
  }
};
module.exports = function(connOpts, messageProxy) {
  var defer = q.defer();
  console.log(JSON.stringify(connOpts, null, 2));
  var opts = {
    protocolVersion: 3,
    username: connOpts.THINGFABRIC_USERNAME,
    password: md5(connOpts.THINGFABRIC_PASSWORD),
    keepalive: 30,
    clientId: connOpts.THINGFABRIC_M2M_CLIENT_ID
  };
  console.log('Connecting Mqtt client to %s:%s:', connOpts.THINGFABRIC_M2M_ENDPOINT.split(':')[0], 1883);
  console.log(JSON.stringify(opts, null, 2));
  var client = mqtt.createClient(1883, connOpts.THINGFABRIC_M2M_ENDPOINT.split(':')[0], opts);
  client.on('connect', function() {
    console.log('Mqtt ' + opts.clientId + ' connected and subscribing to %s', connOpts.THINGFABRIC_M2M_DATA_CHANNEL);
    client.subscribe(connOpts.THINGFABRIC_M2M_DATA_CHANNEL, {
      qos: 1
    });
    return defer.resolve();
  });
  client.on('message', function(topic, payload) {
    console.log('Mqtt client ' + opts.clientId + ' received message!');
    var parsed = parse(topic, payload);
    if (parsed) {
      return messageProxy(parsed);
    }
  });
  client.on('error', function(error) {
    console.log('Mqtt client ' + opts.clientId + ' error: ' + parseError(error));
  });
  client.on('disconnect', function() {
    console.log('Mqtt client ' + opts.clientId + ' disconnected!');
  });
  return defer.promise;
};
