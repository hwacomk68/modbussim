# About

Modbus simulator

# Getting Started

Clone smartq-ems repository

## Prerequisites

- Docker
- Java 17
- Maven

## Configuration
- Modbus slave config
```
cd smartq-ems/modbussim/src/main/resources
vim application.properties

#Modbus
modbus.slave.port=10502
modbus.slave.unitId=1
# Modbus slave data update rate
modbus.update.rate=5000
```
```
cd smartq-ems/modbussim/src/main/resources
vim datapoints.json

[
  {
    "id": 1, -> unique id
    "min": 0, -> minimum value of random numbers
    "max": 5, -> maximum value of random numbers
    "start": 4167, -> data point start address
    "end": 4168, -> data point end address
    "acc": true, -> data accumulate or not
    "specify": false, -> whether to generate value at a specified time
    "specifyTimeStart": "1430", -> specify time from
    "specifyTimeEnd": "1500", -> specify time to
    "specifyMin": 6, -> minimum value of random numbers
    "specifyMax": 10 -> maximum value of random numbers
    # in specify time range, simulator will generate random value between specifyMin and specifyMax
  },
  {
    "id": 2,
    "min": 0,
    "max": 5,
    "start": 4169,
    "end": 4170,
    "acc": false,
    "specify": true,
    "specifyTimeStart": "1630",
    "specifyTimeEnd": "1635",
    "specifyMin": 10,
    "specifyMax": 100
  }
]
```

## Run simulator
- to simulator project directory

```
cd smartq-ems
cd modbussim
```

- modify docker image name from pom.xml
```
vim pom.xml
edit following line to specify a docker image name
...
<imageName>meter_office_101</imageName>
...
```

- Build docker images

```
mvn clean package docker:build
```

- Run container

```
docker run -d -p [port]:[port] --name [container name] [image name]:latest

ex.
docker run -d -p 10502:10502 --name meter_office_101 meter_office_101:latest
```