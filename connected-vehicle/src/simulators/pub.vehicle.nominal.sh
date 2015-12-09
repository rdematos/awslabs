#!/bin/bash

while true; do
mosquitto_pub --cafile ../../certs/rootCA.pem --cert ../../certs/cert.pem --key ../../certs/privkey.pem -h A3CJ7IIGECQ7LO.iot.us-east-1.amazonaws.com -p 8883 -q 1 -d -t vehicle/ -i 52-452-52-753 -m "{\"state\":{\"reported\":{\"vin\":\"52-452-52-753\",\"gps\":{\"speed\":\"25\",\"latitudeDegrees\":\"-83.0\",\"longitudeDegrees\":\"42.0\",\"heading\":\"173\",\"utcDay\":\"14\",\"utcYear\":\"2013\",\"utcHours\":\"13\",\"utcMinutes\":\"16\",\"utcSeconds\":\"54\"},\"airbagStatus\":{\"driverCurtainAirbagDeployed\":\"NO_EVENT\",\"passengerKneeAirbagDeployed\":\"NO_EVENT\",\"driverKneeAirbagDeployed\":\"NO_EVENT\",\"passengerSideAirbagDeployed\":\"NO_EVENT\",\"passengerAirbagDeployed\":\"NO_EVENT\",\"driverAirbagDeployed\":\"NO_EVENT\",\"passengerCurtainAirbagDeployed\":\"NO_EVENT\",\"driverSideAirbagDeployed\":\"NO_EVENT\"},\"externalTemperature\":\"23.0\"}}}"

sleep 5
done
