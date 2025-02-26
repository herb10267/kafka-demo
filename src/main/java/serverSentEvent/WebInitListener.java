package serverSentEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener()
public class WebInitListener implements ServletContextListener {
	
	private static final Logger log = LoggerFactory.getLogger(WebInitListener.class.getSimpleName());
	
	// 所有非同步請求的  AsyncContext 將儲存至這個  Queue
	private Queue<AsyncContext> asyncs = new ConcurrentLinkedQueue<>();
	private Queue<AsyncContext> asyncsKafka = new ConcurrentLinkedQueue<>();
	
	// create consumer
	KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getPropertyConfig());
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		sce.getServletContext().setAttribute("asyncs", asyncs);
		sce.getServletContext().setAttribute("asyncsKafka", asyncsKafka);
		
		// asyncs
		new Thread(() -> {
			while (true) {
				try {
					// 模擬不定時
					Thread.sleep((int) (Math.random() * 5000));
					// 隨機產生數字
					response(Math.random() * 10);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		// asyncsKafka
		new Thread(() -> {
			
			// subscribe consumer to out topic(s)
			consumer.subscribe(Arrays.asList(topic));
			
			while (true) {
				try {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
					for (ConsumerRecord<String, String> record : records) {
						responseKafka(record.value());
						log.info("Key: " + record.key() + ", Value: " + record.value());
						log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		consumer.close();
	}
	
	private void response(double num) {
		// 逐一完成非同步請求
		asyncs.forEach(ctx -> {
			try {
				PrintWriter out = ctx.getResponse().getWriter();
				out.printf("data: %s\n\n", num);
				out.flush();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
	
	private void responseKafka(String data) {
		// 逐一完成非同步請求
		asyncsKafka.forEach(ctx -> {
			try {
				PrintWriter out = ctx.getResponse().getWriter();
				out.printf("data: %s\n\n", data);
				out.flush();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
	
	private static String bootstrapServers = "localhost:9094";
	private static String groupId = "my-first-application";
	private static String topic = "dbserver1.inventory.customers";
	
	private Properties getPropertyConfig(){
		// create property
		Properties prop = new Properties();
		prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
		prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
		// none -if no previous offsets are found then do not even start
		// earliest -read from the very beginning fo the topic
		// latest -only from the now of the topic
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		return prop;
	}
}