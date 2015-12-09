#!/bin/bash

while true; do
mosquitto_pub --cafile ../../certs/rootCA.pem --cert ../../certs/cert.pem --key ../../certs/privkey.pem -h g.us-east-1.pb.iot.amazonaws.com -p 8883 -q 1 -d -t vehicle/ -i 52-452-52-752 -m "{\"state\":{\"reported\":{\"vin\":\"52-452-52-752\",\"gps\":{\"speed\":\"26\",\"latitudeDegrees\":\"-83.0\",\"longitudeDegrees\":\"42.0\",\"heading\":\"173\",\"utcDay\":\"14\",\"utcYear\":\"2013\",\"utcHours\":\"13\",\"utcMinutes\":\"16\",\"utcSeconds\":\"54\"},\"airbagStatus\":{\"driverCurtainAirbagDeployed\":\"NO_EVENT\",\"passengerKneeAirbagDeployed\":\"NO_EVENT\",\"driverKneeAirbagDeployed\":\"NO_EVENT\",\"passengerSideAirbagDeployed\":\"NO_EVENT\",\"passengerAirbagDeployed\":\"NO_EVENT\",\"driverAirbagDeployed\":\"DEPLOYED\",\"passengerCurtainAirbagDeployed\":\"NO_EVENT\",\"driverSideAirbagDeployed\":\"NO_EVENT\"},\"externalTemperature\":\"23.0\"}}}"

sleep 10
done
