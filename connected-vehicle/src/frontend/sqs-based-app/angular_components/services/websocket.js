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



angular.module('com.2lemetry.heroku-demo.services.websocket', []).factory('HerokuDemoWebSocketService', [ 
  '$window',
  function($window) {
    var scope;
    return {
      open: function(scope) {
        scope = scope;
        console.log('WebSocket connection opening at: ' + $window.location.origin.replace(/^http/, 'ws'));
        var ws = new WebSocket($window.location.origin.replace(/^http/, 'ws'));
        ws.onopen = function() {
          console.log('WebSocket opened!');
        };
        ws.onmessage = function (event) {
          var message = JSON.parse(event.data);
          message.date = new Date();
          console.log(message);
          scope.$emit('HerokuDemoWebSocketService:message', message);
        };
      }
    };
  }
]);
