# KAFKA-DEMO
ENV
```
windows 10 
1. install docker
2. install linux under wsl2
```

# Using Docker-Compose To Start Up Container
```
docker-compose -f docker-compose-mysql.yaml up
```
# Setting Debezium Connector Config
```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-mysql.json
```

# Using Kafdrop Watching Kafka
```
docker run -d --rm -p 9001:9000 --network debezium_default --link kafka -e KAFKA_BROKERCONNECT=kafka:9092 -e JVM_OPTS="-Xms32M -Xmx64M" -e SERVER_SERVLET_CONTEXTPATH="/" obsidiandynamics/kafdrop
```