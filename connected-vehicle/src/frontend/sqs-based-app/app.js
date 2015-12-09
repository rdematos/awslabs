/*
 *  Copyright 2015 Amazon Web Services, Inc.
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

var platform = new H.service.Platform({
 'app_id': '17OyTnMIhdAINmTTe7vF',
 'app_code': 'Q-FMSAemR3cAyV6_VWENuw'
});


// Obtain the default map types from the platform object:
var defaultLayers = platform.createDefaultLayers();

// Instantiate (and display) a map object:
var maprect = new H.geo.Rect(42.3772, -71.0996, 42.3515, -71.0327);
var map = new H.Map(
  document.getElementById('here-map'),
  //defaultLayers.normal.map,
  defaultLayers.normal.traffic,
  { zoom: 14, center: { lat: 42.362, lng: -71.062 },
   //[[[-71.0996174861,42.3454411813],[-71.0996174861,42.3772205146],[-71.0327210585,42.3772205146],[-71.0327210585,42.3454411813],[-71.0996174861,42.3454411813]]],
   //bounds: maprect
  }
);
var behavior = new H.mapevents.Behavior(new H.mapevents.MapEvents(map));
var ui = H.ui.UI.createDefault(map, defaultLayers);
map.addLayer(defaultLayers.incidents);

//var carsvg = new H.map.Icon('img/car.svg', {size: {h: 22, w: 22}});
//var crashsvg = new H.map.Icon('img/crash.svg', {size: {h: 36, w: 36}});

function getRandomColor() {
    var r = (Math.round(Math.random() * 255)).toString(16);
    var g = (Math.round(Math.random() * 255)).toString(16);
    var b = (Math.round(Math.random() * 255)).toString(16);
    return r + g + b;
}
function createCarSVGIcon(color) {
  return new H.map.Icon('<svg version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"'+
  ' width="46.253px" height="46.253px" viewBox="0 0 46.253 46.253" style="enable-background:new 0 0 46.253 46.253;">'+
  '<g><g><path stroke="black" fill="#'+color+'" d="M45.105,27.255v-2.288c0-3.202-2.606-5.808-5.812-5.808h-4.643l-3.98-6.9c-0.318-0.55-0.906-0.889-1.54-0.889H19.6H8.84'+
  '      c-0.861,0-1.597,0.615-1.75,1.46l-1.168,6.435c-2.708,0.491-4.77,2.856-4.77,5.702v2.288C0.48,27.511,0,28.154,0,28.915'+
  '      c0,0.981,0.794,1.776,1.777,1.776h2.307c0.639,2.404,2.814,4.191,5.417,4.191c2.604,0,4.781-1.787,5.419-4.191h15.031'+
  '      c0.64,2.404,2.812,4.191,5.417,4.191c2.604,0,4.78-1.787,5.42-4.191h3.688c0.983,0,1.777-0.795,1.777-1.776'+
  '      C46.256,28.154,45.775,27.511,45.105,27.255z M9.49,31.318c-1.139,0-2.06-0.922-2.06-2.062c0-1.137,0.921-2.059,2.06-2.059'+
  '      c1.137,0,2.059,0.922,2.059,2.059C11.549,30.396,10.628,31.318,9.49,31.318z M17.821,19.16H9.553l0.768-4.234h7.5V19.16'+
  '      L17.821,19.16z M21.375,19.16v-4.234h6.729l2.443,4.234H21.375z M35.372,31.318c-1.138,0-2.06-0.922-2.06-2.062'+
  '      c0-1.137,0.922-2.059,2.06-2.059c1.137,0,2.058,0.922,2.058,2.059C37.43,30.396,36.509,31.318,35.372,31.318z"/>'+
  '  </g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></svg>', {size: {h: 22, w: 22}});
}

function createCrashSVGIcon(color) {
  return new H.map.Icon('<svg version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"'+
    ' viewBox="0 0 480.825 480.825" style="enable-background:new 0 0 480.825 480.825;">'+
    '<g fill="#'+color+'">'+
    '<path stroke="black" d="M290.207,188.296c-2.823-1.735-6.519-0.856-8.254,1.966l-45.567,74.047c-1.737,2.822-0.857,6.518,1.965,8.255'+
    '    c0.98,0.603,2.066,0.891,3.139,0.891c2.015,0,3.982-1.015,5.116-2.856l45.567-74.047'+
    '    C293.909,193.729,293.029,190.033,290.207,188.296z"/>'+
    '<path stroke="black" d="M222.466,244.96c1.166,1.676,3.033,2.574,4.931,2.574c1.182,0,2.377-0.35,3.421-1.075c2.72-1.893,3.392-5.632,1.499-8.352'+
    '    l-34.175-49.127c-1.892-2.719-5.631-3.39-8.352-1.499c-2.72,1.893-3.392,5.632-1.499,8.353L222.466,244.96z"/>'+
    '<path stroke="black" d="M240.793,227.091c0.52,0.139,1.04,0.205,1.554,0.205c2.65,0,5.076-1.77,5.793-4.452l19.224-71.91'+
    '    c0.856-3.201-1.045-6.49-4.247-7.347c-3.204-0.854-6.491,1.046-7.346,4.247l-19.224,71.91'+
    '    C235.69,222.945,237.592,226.234,240.793,227.091z"/>'+
    '<path stroke="black" d="M274.393,249.834c1.03,2.154,3.178,3.412,5.417,3.412c0.868,0,1.749-0.189,2.584-0.588l16.375-7.831'+
    '    c2.99-1.43,4.254-5.012,2.825-8.002c-1.429-2.989-5.012-4.253-8.001-2.824l-16.375,7.831'+
    '    C274.228,243.262,272.964,246.844,274.393,249.834z"/>'+
    '<path stroke="black" d="M221.889,201.065c0.716,2.685,3.141,4.456,5.793,4.456c0.512,0,1.032-0.066,1.55-0.204'+
    '    c3.202-0.855,5.105-4.142,4.251-7.344l-11.391-42.72c-0.854-3.201-4.141-5.103-7.343-4.252c-3.202,0.854-5.105,4.142-4.251,7.344'+
    '    L221.889,201.065z"/>'+
    '<path stroke="black" d="M234.214,287.646h-0.403v-5.336c0-4.824-3.917-7.322-7.247-8.35l-4.479-8.423c-0.61-1.148-1.741-1.928-3.031-2.091'+
    '    l-34.26-4.324l-31.767-50.333c-0.733-1.161-2.01-1.865-3.383-1.865H60.69c-1.641,0-3.115,1.002-3.719,2.527l-18.482,46.66H21.128'+
    '    c-8.945,0-14.075,5.13-14.075,14.075v17.713C3.012,288.872,0,292.517,0,296.852v6.92c0,5.076,4.13,9.205,9.206,9.205h20.963'+
    '    c2.054,13.824,13.998,24.466,28.384,24.466c14.387,0,26.332-10.642,28.386-24.466l69.541,0.001'+
    '    c2.054,13.824,13.998,24.465,28.383,24.465c14.387,0,26.332-10.641,28.386-24.465h20.964c5.076,0,9.205-4.13,9.205-9.206v-6.92'+
    '    C243.419,291.775,239.29,287.646,234.214,287.646z M58.553,319.442c-5.899,0-10.698-4.8-10.698-10.7c0-5.9,4.799-10.7,10.698-10.7'+
    '    c5.9,0,10.7,4.8,10.7,10.7C69.253,314.643,64.453,319.442,58.553,319.442z M184.864,319.442c-5.899,0-10.698-4.8-10.698-10.7'+
    '    c0-5.9,4.799-10.7,10.698-10.7c5.9,0,10.701,4.8,10.701,10.7C195.564,314.643,190.764,319.442,184.864,319.442z"/>'+
    '<path stroke="black" d="M473.772,287.899v-17.713c0-8.945-5.13-14.075-14.076-14.075h-17.361l-18.482-46.66c-0.604-1.525-2.078-2.527-3.719-2.527'+
    '    h-76.493c-1.373,0-2.65,0.704-3.383,1.865l-31.767,50.333l-34.26,4.324c-1.29,0.163-2.42,0.942-3.031,2.091l-4.479,8.423'+
    '    c-3.331,1.027-7.247,3.525-7.247,8.35v5.336h-0.403c-5.076,0-9.206,4.13-9.206,9.206v6.92c0,5.076,4.129,9.206,9.206,9.206h20.964'+
    '    c2.054,13.824,13.999,24.465,28.386,24.465c14.385,0,26.33-10.641,28.383-24.465l57.081-0.001'+
    '    c2.054,13.824,13.999,24.466,28.386,24.466c14.386,0,26.33-10.642,28.384-24.466h20.963c5.076,0,9.206-4.129,9.206-9.205v-6.92'+
    '    C480.825,292.517,477.814,288.872,473.772,287.899z M308.422,319.442c-5.9,0-10.701-4.8-10.701-10.7c0-5.9,4.8-10.7,10.701-10.7'+
    '    c5.899,0,10.698,4.8,10.698,10.7C319.121,314.643,314.321,319.442,308.422,319.442z M422.272,319.442c-5.9,0-10.7-4.8-10.7-10.7'+
    '    c0-5.9,4.8-10.7,10.7-10.7c5.899,0,10.698,4.8,10.698,10.7C432.97,314.643,428.171,319.442,422.272,319.442z"/>'+
    '</g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></svg>', {size: {h: 36, w: 36}});
}

var attentionSvg = new H.map.Icon('<svg xmlns="http://www.w3.org/2000/svg" width="26px" height="31px" baseProfile="tiny" version="1.1" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 26 31">'+
  '<path id="SHADOW_2_" fill="#868686" d="M16.995,28.979c0,1.116-1.791,2.021-4,2.021c-2.209,0-4-0.905-4-2.021'+
  '  c0-1.117,1.791-2.025,4-2.025C15.204,26.953,16.995,27.861,16.995,28.979"/>'+
  '<path fill="#D5232F" d="M1.702,17.693c-1.573-1.566-1.584-3.856-0.027-5.698l8.409-10.098c0.773-0.901,1.807-1.394,2.915-1.394'+
  '  c1.125,0,2.213,0.525,2.911,1.404l8.402,10.09c1.602,1.867,1.592,4.052-0.027,5.693l-11.29,11.286L1.702,17.693z"/>'+
  '<path fill="#FFFFFF" d="M12.998,1.007c0.971,0,1.909,0.445,2.516,1.213l8.41,10.099c1.375,1.603,1.494,3.504,0,5.017l-10.93,10.929'+
  '  L2.058,17.337c-1.392-1.385-1.39-3.375,0-5.017l8.411-10.099C11.191,1.38,12.11,1.007,12.998,1.007 M12.998,0L12.998,0'+
  '  c-1.258,0-2.427,0.555-3.295,1.563l-8.42,10.112c-1.732,2.047-1.708,4.611,0.063,6.374l10.935,10.928l0.713,0.712l0.713-0.712'+
  '  l10.93-10.93c1.799-1.82,1.818-4.324,0.055-6.383L16.288,1.576C15.514,0.597,14.278,0,12.998,0L12.998,0z"/>'+
  '<path fill="#FFFFFF" d="M9.673,18.902l-0.357-0.301l-0.902-0.758c-0.406-0.34-0.457-0.949-0.115-1.354l0.014-0.02l-0.975-0.841'+
  '  c-0.33-0.275-0.373-0.771-0.096-1.1l0.012-0.012c0.275-0.332,0.773-0.372,1.1-0.1l0.979,0.84l4.414-5.262l-0.978-0.838'+
  '  c-0.324-0.274-0.373-0.768-0.094-1.102l0.012-0.013c0.277-0.328,0.771-0.372,1.099-0.099l0.98,0.842l0.014-0.02'+
  '  c0.342-0.407,0.947-0.459,1.355-0.119l0.393,0.33l0.506,0.428l0.803,0.672c0.406,0.342,0.459,0.948,0.117,1.352l-0.135,0.158'+
  '  l1.301,2.4c0.059,0.146,0.318,0.9-0.105,1.406l-3.287,1.367l-0.043,1.479c-0.043,0.762-1.369,0.764-1.459,0.748l-2.562-0.061'+
  '  C11.335,19.094,10.095,19.113,9.673,18.902z M17.096,12.184l-4.984,5.939l2.189-0.064c0.105,0.014,0.609,0.004,0.594-0.471'+
  '  l0.09-1.515l3.244-1.331c0.07-0.081,0.037-0.362-0.012-0.507L17.096,12.184z M15.174,11.561c0.391,0.326,0.967,0.279,1.293-0.111'+
  '  c0.326-0.387,0.273-0.969-0.113-1.291c-0.391-0.326-0.969-0.275-1.293,0.111C14.735,10.661,14.786,11.237,15.174,11.561z'+
  '   M9.923,17.818c0.387,0.326,0.969,0.279,1.293-0.115c0.326-0.389,0.273-0.965-0.111-1.289c-0.393-0.326-0.969-0.28-1.295,0.111'+
  '  C9.487,16.916,9.538,17.492,9.923,17.818z"/>'+
  '<path fill="#FFFFFF" d="M8.255,19.088l-3.09-1.094l1.094,1.094H8.255z"/>'+
  '<path fill="#FFFFFF" d="M16.014,19.088h3.941l1.596-1.543L16.014,19.088z"/>'+
  '</svg>', {size: {h: 36, w: 36}});

//var attentionSvg = new H.map.Icon('<svg width="26" height="32" xmlns="http://www.w3.org/2000/svg"><path d="m 16.8,29.4 c 0,-1.1 -1.8,-2 -4,-2 -2.2,0 -4.01,0.9 -4.01,2 0,1.1 1.81,2 4.01,2 2.2,0 4,-0.9 4,-2" style="fill:#878787"/><path d="m 24.1,17.8 c 1.6,-1.6 1.6,-3.8 0,-5.7 L 15.8,1.9 C 15,0.998 14,0.498 12.9,0.498 11.8,0.498 10.7,1.1 10,1.9 L 1.7,12.1 c -1.6,1.9 -1.6,4.1 0,5.7 L 12.9,29 24.1,17.8 z" stroke="#fff" stroke-width="1" fill="#323232" /><path d="m 12.9,15.1 c 0.6,0 1.1,-0.5 1.1,-1 l 0.4,-5.9 c 0,-0.6 -0.6,-1.04 -1.5,-1.04 -0.8,0 -1.4,0.44 -1.4,1.04 l 0.3,5.9 c 0,0.5 0.6,1 1.1,1 m 0,1.3 c -0.7,0 -1.3,0.6 -1.3,1.3 0,0.8 0.6,1.4 1.3,1.4 0.8,0 1.4,-0.6 1.4,-1.4 0,-0.7 -0.6,-1.3 -1.4,-1.3" fill="#ffffff" /></svg>', {});

// add some test markers
//var marker = new H.map.Marker({lat: 42.363, lng: -71.05}, {icon: createCarSVGIcon(getRandomColor())});
//var car1 = map.addObject(marker);
//var crashmarker = new H.map.Marker({lat: 42.359, lng: -71.059}, {icon: createCrashSVGIcon(getRandomColor())});
//var crash1 = map.addObject(crashmarker);
var cars = {};
var carColors = {};
var carsMarkers = new H.map.Group();
map.addObject(carsMarkers);
var incidents = {};
var incidentMarkers = new H.map.Group();
map.addObject(incidentMarkers);

// connect to websocket
/*
//var client = new Paho.MQTT.Client('ws://iot.eclipse.org/ws', 'webclient_' + parseInt(Math.random() * 100, 10));
//var client = new Paho.MQTT.Client('broker.iot.cloud-native.de', 4172, '/ws', 'webclient_' + parseInt(Math.random() * 100, 10));
//var client = new Messaging.Client("iot.eclipse.org", 80, "webclient_" + parseInt(Math.random() * 100, 10));
var client = new Messaging.Client('broker.iot.cloud-native.de', 4172, "webclient_" + parseInt(Math.random() * 100, 10));
client.onConnectionLost = function (responseObject) { console.log("connection lost: " + responseObject.errorMessage); };
client.onMessageArrived = function (message) {
  //console.log(message.payloadString);
  addCarPosition(message.payloadString);
};
client.connect({
  timeout: 3,
  onSuccess: function () {
    console.log("connected");
    client.subscribe('9ddberufbghspoo/vehicle​', {qos: 2});
    //client.subscribe('vehicle​', {qos: 1});
  },
  onFailure: function (message) {
    console.log("Connection failed: " + message.errorMessage);
  }
});
*/

// connected car from sqs
AWS.config.region = 'us-east-1';
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
  //IdentityPoolId: 'us-east-1:5f065b4d-58d3-4321-b503-b4ec71d3bd30',
  IdentityPoolId: 'us-east-1:fd8e94a7-38cb-4894-8dbf-abbeeadb090c',
});
var sqs;
//var queueUrl = 'https://sqs.us-east-1.amazonaws.com/118374316100/connected-car-dashboard';
var queueUrl = 'https://sqs.us-east-1.amazonaws.com/673485280914/vehicle';
function pollForIncommingMessages() {
  sqs.receiveMessage({
    "QueueUrl": queueUrl,
    "MaxNumberOfMessages": 1,
    "VisibilityTimeout": 40,
    "WaitTimeSeconds": 20
  }, function(err, data) {
    if (err) {
      console.log(err, err.stack);
    } if (data.Messages && data.Messages.length > 0) {
      for (var i = 0; i < data.Messages.length; i++) {
        //console.log('got messages:', data.Messages[i]);
        processMessage(data.Messages[i]);
        sqs.deleteMessage({
          "QueueUrl": queueUrl,
          "ReceiptHandle": data.Messages[i].ReceiptHandle
        }, function(err, data){
          //console.log('deleted message');
        });
      }
      pollForIncommingMessages();
    } else  {
      //console.log('no messages');
      pollForIncommingMessages();
    }
  });
}
AWS.config.credentials.get(function(){
  sqs = new AWS.SQS({region:'us-east-1'});
  pollForIncommingMessages();
});
function processMessage(message) {
  if (!message) {
      console.log('no message to process');
      return;
  }
  //console.log(message.Body);
  addCarPosition(message.Body);
}

function addCarPosition(message) {
  var jsonMsg = JSON.parse(message);
  //console.log(jsonMsg);
  var vid = jsonMsg.state.reported.vin,
    lat = jsonMsg.state.reported.gps.latitudeDegrees,
    lng = jsonMsg.state.reported.gps.longitudeDegrees,
    crash = jsonMsg.state.reported.prndl != null && jsonMsg.state.reported.prndl == 'PARK';
  if (cars[vid] == undefined) {
    //console.log('create car '+vid+' lat: '+lat+' lng:'+lng);
    //cars[vid] = map.addObject(new H.map.Marker({lat: lat, lng: lng}, {icon: crash? crashsvg : carsvg}));
    carColors[vid] = getRandomColor();
    cars[vid] = new H.map.Marker({lat: lat, lng: lng}, {icon: crash? createCrashSVGIcon(carColors[vid]) : createCarSVGIcon(carColors[vid])});
    carsMarkers.addObjects([cars[vid]]);
  } else {
    //console.log('update car '+vid);
    cars[vid].setPosition({lat: lat, lng: lng});
    if (crash) {
      cars[vid].setIcon(createCrashSVGIcon(carColors[vid]))
    }
  }
  if(!(new H.map.Rect(map.getViewBounds())).getBounds().containsLatLng(lat, lng)) {
   //map.setViewBounds(carsMarkers.getBounds());
   map.setCenter(carsMarkers.getBounds().getCenter());
  }
  //map.setViewBounds(carsMarkers.getBounds());
  //console.log((new H.map.Rect(map.getViewBounds())).getBounds().contains({lat: lat, lng: lng}));
  //map.setCenter({lat: lat, lng: lng});
}

var $jsonp = (function(){
  var that = {};

  that.send = function(src, options) {
    var callback_name = options.callbackName || 'callback',
      on_success = options.onSuccess || function(){},
      on_timeout = options.onTimeout || function(){},
      timeout = options.timeout || 10; // sec

    var timeout_trigger = window.setTimeout(function(){
      window[callback_name] = function(){};
      on_timeout();
    }, timeout * 1000);

    window[callback_name] = function(data){
      window.clearTimeout(timeout_trigger);
      on_success(data);
    }

    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.async = true;
    script.src = src;

    document.getElementsByTagName('head')[0].appendChild(script);
  }

  return that;
})();

function addIncidents2Mapv60(json) {
  if (!json.TRAFFIC_ITEMS || !json.TRAFFIC_ITEMS.TRAFFIC_ITEM) {
    return;
  }
  for (index = 0; index < json.TRAFFIC_ITEMS.TRAFFIC_ITEM.length; ++index) {
    var incidentid = json.TRAFFIC_ITEMS.TRAFFIC_ITEM[index].TRAFFIC_ITEM_ID,
      lat = json.TRAFFIC_ITEMS.TRAFFIC_ITEM[index].LOCATION.GEOLOC.ORIGIN.LATITUDE,
      lng = json.TRAFFIC_ITEMS.TRAFFIC_ITEM[index].LOCATION.GEOLOC.ORIGIN.LONGITUDE;
    if (!incidents[incidentid]) {
      incidents[incidentid] = new H.map.Marker({lat: lat, lng: lng}, {icon: attentionSvg});
      incidentMarkers.addObjects([incidents[incidentid]]);
    }
  }
}

function addIncidents2Mapv61(json) {
  if (!json.TRAFFICITEMS || !json.TRAFFICITEMS.TRAFFICITEM) {
    return;
  }
  for (index = 0; index < json.TRAFFICITEMS.TRAFFICITEM.length; ++index) {
    var incidentid = json.TRAFFICITEMS.TRAFFICITEM[index].TRAFFICITEMID,
      lat = json.TRAFFICITEMS.TRAFFICITEM[index].LOCATION.GEOLOC.ORIGIN.LATITUDE,
      lng = json.TRAFFICITEMS.TRAFFICITEM[index].LOCATION.GEOLOC.ORIGIN.LONGITUDE;
    if (!incidents[incidentid]) {
      incidents[incidentid] = new H.map.Marker({lat: lat, lng: lng}, {icon: attentionSvg});
      incidentMarkers.addObjects([incidents[incidentid]]);
    }
  }
}

function getTrafficInfo() {
  setTimeout(function () {
    var url = 'http://getmessag-getmessa-qjtd3sorj0mu-1306056233.us-west-2.elb.amazonaws.com/traffic/6.0/incidents.json?app_id=dHclbe4kurhteO3thGPl&app_code=4A04BJZiGAavKm867wcH8A&bbox=42.3,-71.1;42.4,-71.2&jsoncallback=updateIncidents';
    //var url = 'http://traffic.api.here.com/traffic/6.1/incidents/json/14/4949/6061?xnlp=CL_JSMv3.0.12.2&app_id=17OyTnMIhdAINmTTe7vF&app_code=Q-FMSAemR3cAyV6_VWENuw&criticality=major%2Ccritical&jsoncallback=updateIncidents';
    $jsonp.send(url, {
        callbackName: 'updateIncidents',
        onSuccess: addIncidents2Mapv61,
        onTimeout: function(){
            console.log('timeout to incidents');
        },
        timeout: 5
    });
    //addIncidents2Mapv61({"TRAFFICITEMS":{"TRAFFICITEM":[{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245727563,"ENDTIME":"10/08/2015 05:22:07","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245741483,"ENDTIME":"10/08/2015 05:22:21","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245683381,"ENDTIME":"10/08/2015 05:21:23","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245687904,"ENDTIME":"10/08/2015 05:21:27","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245686508,"ENDTIME":"10/08/2015 05:21:26","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245699909,"ENDTIME":"10/08/2015 05:21:39","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245740449,"ENDTIME":"10/08/2015 05:22:20","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245688923,"ENDTIME":"10/08/2015 05:21:28","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245689553,"ENDTIME":"10/08/2015 05:21:29","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245765068,"ENDTIME":"10/08/2015 05:22:45","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245717442,"ENDTIME":"10/08/2015 05:21:57","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245710131,"ENDTIME":"10/08/2015 05:21:50","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245737729,"ENDTIME":"10/08/2015 05:22:17","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245684583,"ENDTIME":"10/08/2015 05:21:24","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]},{"TRAFFICITEMID":0,"ORIGINALTRAFFICITEMID":1444245739623,"ENDTIME":"10/08/2015 05:22:19","CRITICALITY":{},"VERIFIED":false,"RDSTMCLOCATIONS":{"RDSTMC":[{"ORIGIN":{"EBUCOUNTRYCODE":"1","TABLEID":20}}]},"LOCATION":{"GEOLOC":{"ORIGIN":{"LATITUDE":42.3574846378,"LONGITUDE":-71.1798286001}}},"TRAFFICITEMDESCRIPTION":[]}]},"TIMESTAMP":"10/08/2015 04:52:29 GMT","VERSION":5.0});
    getTrafficInfo();
  }, 2000);
}
getTrafficInfo();
