mosquitto_sub --cafile ../../certs/rootCA.pem --cert ../../certs/cert.pem --key ../../certs/privkey.pem -h A3CJ7IIGECQ7LO.iot.us-east-1.amazonaws.com -p 8883 -q 1 -d -t vehicle/ -i testsubscriber
