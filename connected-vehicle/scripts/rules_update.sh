#cleanup
aws iot list-topic-rules
mosquitto_sub -h iot.eclipse.org -p 1883 -q 1 -d -t 9ddberufbghspoo/vehicle -i testsubscriber #should see nothing even when vehicle emits events



#all vehicle events
aws iot delete-topic-rule --rule-name vehicle
aws iot create-topic-rule --rule-name vehicle --topic-rule-payload file://vehicle.json
mosquitto_sub -h iot.eclipse.org -p 1883 -q 1 -d -t 9ddberufbghspoo/vehicle -i testsubscriber #should see vehicle events

#emergency events
aws lambda remove-permission --function-name geonotificationpublisher --statement-id statement003
aws lambda add-permission --function-name geonotificationpublisher --region us-east-1 --principal iot.amazonaws.com --source-arn arn:aws:iot:us-east-1:673485280914:rule/vehicleprndl --source-account 673485280914 --statement-id statement003 --action "lambda:InvokeFunction"
aws iot delete-topic-rule --rule-name vehicleprndl
aws iot create-topic-rule --rule-name vehicleprndl --topic-rule-payload file://vehicle.prndl.json #should see hazard notifications in vehicle

#re-broacast notifications to third party
aws iot delete-topic-rule --rule-name trafficincidentnotification
aws lambda remove-permission --function-name trafficincidentnotification --statement-id statement006
aws lambda add-permission --function-name trafficincidentnotification --region us-east-1 --principal iot.amazonaws.com --source-arn arn:aws:iot:us-east-1:673485280914:rule/trafficincidentnotification --source-account 673485280914 --statement-id statement006 --action "lambda:InvokeFunction"
aws iot create-topic-rule --rule-name trafficincidentnotification --topic-rule-payload file://trafficincidentnotification.json
