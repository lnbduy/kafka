package consumer;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ElasticSearchConsumer {
    private static final JsonParser jsonParser = new JsonParser();

    public static RestHighLevelClient createClient() {
        String hostname = "kafka-8947707175.ap-southeast-2.bonsaisearch.net";
        String username = "u9qtapv8mn";
        String password = "endq2nw2uv";

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
        RestHighLevelClient client = createClient();

        KafkaConsumer<String, String> consumer = createConsumer("twitter_tweets");
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            int recordCounts = records.count();
            logger.info("Received " + recordCounts + " records");
            BulkRequest bulkRequest = new BulkRequest();
            for (ConsumerRecord<String, String> record : records) {
                // 2 strategies
                // kafka generic id
                // String id = record.topic() + "_" + record.partition() + "_" + record.offset();

                // twitter specific id
                String id = extractIdFromTweet(record.value());

                IndexRequest indexRequest = new IndexRequest("twitter",
                        "tweets", id) // id is to make our consumer idempotent
                        .source(record.value(), XContentType.JSON);
                bulkRequest.add(indexRequest);
            }
            if (recordCounts > 0) {
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                logger.info("Committing offsets..");
                consumer.commitSync();
                logger.info("Offsets have been committed");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //client.close();
    }

    public static KafkaConsumer<String, String> createConsumer(String topic) {
        String bootstrapServer = "127.0.0.1:9092";
        String groupId = "kafka-demo-elasticsearch2";

        // create consumer config
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    private static String extractIdFromTweet(String tweetJson) {
        return jsonParser.parse(tweetJson).getAsJsonObject()
                .get("id_str")
                .getAsString();

    }
}
