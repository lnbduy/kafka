# kafka
Learning Kafka
 Commands
 start zoo keeper
 zookeeper-server-start.sh kafka_2.13-2.8.0/config/zookeeper.properties
 start kafka
 kafka-server-start.sh kafka_2.13-2.8.0/config/server.properties
 consumer
 kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic group my-third-application
 kafka-consumer-groups.sh --bootsrap-server localhost:9092 -group my-first-application --reset-offsets --to-earliest --execute --topic first_topic 
 
