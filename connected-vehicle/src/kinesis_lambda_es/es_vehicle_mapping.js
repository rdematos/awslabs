{
    "mappings": {
      "vehicledata": {
        "properties": {
          "state": {
            "properties": {
              "reported": {
                "properties": {
                  "airbagStatus": {
                    "properties": {
                      "driverAirbagDeployed": {
                        "type": "string"
                      },
                      "driverCurtainAirbagDeployed": {
                        "type": "string"
                      },
                      "driverKneeAirbagDeployed": {
                        "type": "string"
                      },
                      "driverSideAirbagDeployed": {
                        "type": "string"
                      },
                      "passengerAirbagDeployed": {
                        "type": "string"
                      },
                      "passengerCurtainAirbagDeployed": {
                        "type": "string"
                      },
                      "passengerKneeAirbagDeployed": {
                        "type": "string"
                      },
                      "passengerSideAirbagDeployed": {
                        "type": "string"
                      }
                    }
                  },
                  "externalTemperature": {
                    "type": "double"
                  },
                  "gps": {
                    "properties": {
                      "heading": {
                        "type": "long"
                      },
                      "latitudeDegrees": {
                        "type": "double"
                      },
                      "longitudeDegrees": {
                        "type": "double"
                      },
                      "speed": {
                        "type": "double"
                      },
                      "utcDay": {
                        "type": "long"
                      },
                      "utcHours": {
                        "type": "long"
                      },
                      "utcMinutes": {
                        "type": "long"
                      },
                      "utcMonth": {
                        "type": "long"
                      },
                      "utcSeconds": {
                        "type": "long"
                      },
                      "utcYear": {
                        "type": "long"
                      }
                    }
                  },
                  "pin": {
                    "properties": {
                      "location": {
                        "type": "geo_point"
                        }
                      }
                    }
                  },
                  "prndl": {
                    "type": "string"
                  },
                  "timestamp": {
                    "type": "date",
                    "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd"
                  },
                  "timestamp_es": {
                    "type": "date",
                    "store": true
                  },
                  "vin": {
                    "type": "string"
                  }
                }
              }
            }
          }
        }
      }
    }
}
