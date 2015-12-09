# thingfabric-heroku-app

This Node.js / AngularJS application demonstrates how to use ThingFabric and the ThingFabric Heroku add-on to build applications _on top of_ ThingFabric. 

## Pre-reqs

### Heroku

You will need a Heroku account in order to use this sample application and add-on. Your Heroku account also needs a valid billing method on file (though the basic Heroku app and this add-on are free to use).

### Node.js + NPM

1. [Node.js](http://nodejs.org) (version 0.10.18)
1. [NPM](http://npm.org) (version 1.2.32)
1. [Bower](http://bower.io) as `sudo npm install -g bower@1.3.3`
1. [Foreman](https://github.com/strongloop/node-foreman) as `sudo npm install -g foreman@0.3.0`

## Setup

### Deploy From Here

You can click the button below, which will take you to Heroku and do all the hard work for you! After deploying, you will be taken to the application's address.

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

### Or, do it manually...

    git clone https://github.com/m2mIO/thingfabric-heroku-app.git
    cd thingfabric-heroku-app
    npm install
    bower install
    heroku create 
    heroku addons:add thingfabric --app <YOUR_APP_NAME_HERE>
    heroku plugins:install git://github.com/ddollar/heroku-config.git
    heroku config:pull --app <YOUR_APP_NAME_HERE>

Optionally set the application port (Foreman defaults to `5000`):

    export PORT=3000

Run the application locally:

    foreman start

Visit the `localhost` URL with `PORT` you specified!

Pushing the application to Heroku:

    git push heroku master
    heroku open

Login to ThingFabric _by means of the Heroku add-on link_, then start the default Device Simulator in ThingFabric to see data come through!

## License

Copyright 2015 2lemetry, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
