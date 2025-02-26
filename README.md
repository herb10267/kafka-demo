# KAFKA-DEMO
ENV
```
windows 10 
1. install docker
2. install linux under wsl2
```

# Using Docker-Compose To Start Up Container
```
docker-compose up -d
```
# Setting Debezium Connector Config
```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-mysql.json
```