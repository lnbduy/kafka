# kafka
## Learning Kafka
 ### Commands
 #### start zoo keeper
 zookeeper-server-start.sh kafka_2.13-2.8.0/config/zookeeper.properties
 #### start kafka
 kafka-server-start.sh kafka_2.13-2.8.0/config/server.properties
 #### topics
 - create topic kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1
 - list topics kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
 - View a topic kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic firstopic --describe
 - Delete a topic kafka-topics.sh --delete --topic first_topic --zookeeper localhost:2181 
 #### producer
 - create producer kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
 - with property create producer kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --producer-property acks=all
 #### consumer
 - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
 - kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
 - with group kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-third-application
 #### consumer group
 - List all consumer groups: kafka-consumer-groups.sh --bootsrap-server localhost:9092 --list
 - Describe a consumer group: kafka-consumer-groups.sh --bootsrap-server localhost:9092 --describe --group my-application
 - kafka-consumer-groups.sh --bootsrap-server localhost:9092 --topic first_topic --group my-application
 - Reset offset kafka-consumer-groups.sh --bootsrap-server localhost:9092 -group my-first-application --reset-offsets --to-earliest --execute --topic first_topic 
 jXBN8Vr3B0ICbZtJS6VAfw6Wc
ubJqI7X57F2MRyVc5DkCtCxZk8aeAHkjsUEGAPD0eXwCZhjvgf

1107800174-pJEJ1ggGIonFw12tiPdc7Vwmi90mkdWEvXjp3QW

Frr4CNdBAoMQOpDfBgnvpHouUbtdFSu9vDJkOmOY2qkOL
