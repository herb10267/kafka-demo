import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {

        // step1. create producer properties
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // step2. create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);

        // Sticky Partitioner - if sent data alot in once, it will be batching to same partition
        for(int i=0; i<10; i++){
            // step3. create aproducer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "Hello world " + i);

            // step4. send data - asynchronous
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if(e == null){
                        // the record was successfullly sent
                        log.info("Receiced new metadata /\n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Partition: " + metadata.partition() + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp() + "\n"
                        );
                    }else{
                        log.error("Erro while producing :" + e.toString());
                    }
                }
            });

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // step5. flush - synchronous
        producer.flush();// this step is really send data to producer

        // step6. close the producer
        producer.close();

    }
}
