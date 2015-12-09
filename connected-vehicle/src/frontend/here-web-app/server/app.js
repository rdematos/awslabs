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
require('dotenv').load();


if (!process.env.THINGFABRIC_CONFIG) {
  throw new Error('`THINGFABRIC_CONFIG` missing!');
}

var config;
// config comes back different whether we are local or on heroku actual
try {
  config =  JSON.parse('{'.concat(process.env.THINGFABRIC_CONFIG).concat('}'));
} catch (error) {
  config =  JSON.parse('{"'.concat(process.env.THINGFABRIC_CONFIG).concat('"}'));
}
console.log(config);

var path = require('path'),
  http = require('http'),
  _ = require('underscore'),
  express = require('express'),
  mqtt = require('./mqtt'),
  WebSocketServer = require('ws').Server;

var app = express(),
  port = process.env.PORT || 3000,
  wss,
  connections = {};

app.use(express.logger());
app.use('/client', express.static(path.resolve(__dirname, '../client/')));
app.use('/', express.static(path.resolve(__dirname, '../client/')));
app.get('/cars', function(req, res) {
    //http://getmessag-getmessa-qjtd3sorj0mu-1306056233.us-west-2.elb.amazonaws.com/traffic/6.0/incidents.json?app_id=dHclbe4kurhteO3thGPl&app_code=4A04BJZiGAavKm867wcH8A&bbox=-71.212399,42.337989,-71.001135,42.427763
    //bbox bbox=-71.212399,42.337989;-71.001135,42.427763
    //$$c(W 71°12'44"--W 71°00'04"/N 42°25'39"--N 42°20'16")​
    res.send([
      {
        name:'car1',
        lat: 42.36,
        lng: -71.06
      },
      {
        name:'car2',
        lat: 42.359,
        lng: -71.059
      }
    ]);
});
app.get('/cars/:id', function(req, res) {
    res.send({id:req.params.id, name: "a car", description: "description"});
});

var server = http.createServer(app);
server.listen(port);

// Proxy Mqtt messages from ThingFabric Devices/Simulators over to browser pages with WebSocket connection open.
var messageProxy = function(message) {
  if (!wss) {
    return console.log('Mqtt -> WebSocket proxy not available!');
  }
  console.log('Mqtt -> WebSocket message being proxied:');
  console.log(message);
  for (id in connections) {
    connections[id].send(JSON.stringify(message), function() {
      console.log('Mqtt -> WebSocket message proxied to: %s', id);
    });
  }
};

server.listen(port, function() {
  console.log('NODE_ENV: ' + process.env.NODE_ENV);
  console.log('ThingFabric Heroku app started on port %s', port);
  mqtt({
    THINGFABRIC_USERNAME: config.THINGFABRIC_USERNAME,
    THINGFABRIC_PASSWORD: config.THINGFABRIC_PASSWORD,
    THINGFABRIC_M2M_ENDPOINT: config.THINGFABRIC_M2M_ENDPOINT,
    THINGFABRIC_M2M_DATA_CHANNEL: config.THINGFABRIC_M2M_DATA_CHANNEL
  }, messageProxy).then(function() {
    console.log('Starting WebSocket server...');
    wss = new WebSocketServer({
      server: server
    });
    wss.on('connection', function(ws) {
      var id = new Date().getTime();
      console.log('New WebSocket connection: %s', id);
      connections[id] = ws;
      console.log('Total connections: %s', _.size(connections));
      ws.on('close', function() {
        console.log('WebSocket connection closed: %s', id);
        delete connections[id];
        console.log('Total connections: %s', _.size(connections));
      });
    });
  });
});
