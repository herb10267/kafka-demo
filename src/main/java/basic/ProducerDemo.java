package basic;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        // step1. create producer properties
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // step2. create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);

        // step3. create aproducer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "Hello world 123");

        // step4. send data - asynchronous
        producer.send(producerRecord);

        // step5. flush - synchronous
        producer.flush();// this step is really send data to producer

        // step6. close the producer
        producer.close();

    }
}
