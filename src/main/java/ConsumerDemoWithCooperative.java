import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWithCooperative {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoWithCooperative.class.getSimpleName());

    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-third-application";
        String topic = "demo_java";

        // create property
        Properties prop = new Properties();
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // none -if no previous offsets are found then do not even start
        // earliest -read from the very beginning fo the topic
        // latest -only from the now of the topic
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        prop.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);

        // get a reference to the current thread
        final  Thread mainThread = Thread.currentThread();

        // adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run(){
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                consumer.wakeup();

                // join the main thread to allow the execution of the code int the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            // subscribe consumer to out topic(s)
            consumer.subscribe(Arrays.asList(topic));

            // poll for new data
            while (true) {
//                log.info("Polling");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }
            }
        } catch (WakeupException e) {
            log.info("Wake up exception!");
            // we ignore this as this is and expected exception when closing a consumer
        } catch (Exception e){
            log.info("Unexpected Exception " + e.toString());
        } finally {
            consumer.close(); // this will also commit the offsets if need be
            log.info("consumer is closed");
        }


    }
}
