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
     defaultLayers.normal.map,
     {
       zoom: 14,
       //center: { lat: 42.362, lng: -71.062 },
       //[[[-71.0996174861,42.3454411813],[-71.0996174861,42.3772205146],[-71.0327210585,42.3772205146],[-71.0327210585,42.3454411813],[-71.0996174861,42.3454411813]]]
       bounds: maprect
     });
map.addLayer(defaultLayers.incidents);

     // Define a variable holding SVG mark-up that defines an icon image:
    var carsvg = new H.map.Icon('client/img/car.svg', {size: {h: 22, w: 22}});
    var crashsvg = new H.map.Icon('client/img/crash.svg', {size: {h: 36, w: 36}});

    //place markers
    // this get will be replaced with sqs or iot data
     $.get( "/cars" )
         .done(function(data) {
            //$("#demo-table").html(JSON.stringify(data));

            //code to place markers on map
            for (var i = 0; i < data.length; i++){
              var obj = data[i];
              $('#myTable tr:last').after('<tr>'+obj.name+'</tr><tr>...</tr>');
            }
         });


     // Create an icon, an object holding the latitude and longitude, and a marker:
     var coords = {lat: 42.363, lng: -71.05},
         marker = new H.map.Marker(coords, {icon: carsvg}),
         crashmarker = new H.map.Marker({lat: 42.359, lng: -71.059}, {icon: crashsvg});

     // Add the marker to the map and center the map at the location of the marker:
     map.addObject(marker);
     map.addObject(crashmarker);

     /*angular.module('com.2lemetry.heroku-demo', [
       'com.2lemetry.heroku-demo.services.websocket',
       'com.2lemetry.heroku-demo.directives.table',
       'com.2lemetry.heroku-demo.directives.map'
     ]).controller('HerokuDemoController', [
       '$scope',
       'HerokuDemoWebSocketService',
       function($scope, HerokuDemoWebSocketService) {
         $scope.messages = [];
         $scope.$on('HerokuDemoWebSocketService:message', function(event, message) {
           $scope.$apply(function() {
             $scope.currentMessage = message.message;
             $scope.messages.unshift(message);
             if ($scope.messages.length > 10) {
               $scope.messages = _.first($scope.messages, 10);
             }
           });
         });
         HerokuDemoWebSocketService.open($scope);
       }
     ]);
     */
