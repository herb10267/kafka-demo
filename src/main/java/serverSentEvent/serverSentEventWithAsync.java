package serverSentEvent;

import java.io.IOException;
import java.util.Properties;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
	urlPatterns={"/serverSentEventWithAsync"},
	asyncSupported = true
)
public class serverSentEventWithAsync extends HttpServlet {
	
	private static final Logger log = LoggerFactory.getLogger(serverSentEventWithAsync.class.getSimpleName());
	private Queue<AsyncContext> asyncs;
	private static String bootstrapServers = "localhost:9094";
	private static String groupId = "my-first-application";
	private static String topic = "dbserver1.inventory.customers";
	
	@Override
	public void init() throws ServletException {
		asyncs = (Queue<AsyncContext>) getServletContext().getAttribute("asyncs");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/event-stream");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		AsyncContext ctx = request.startAsync();
		ctx.setTimeout(30 * 1000);
		
		ctx.addListener(new AsyncListener() {
			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				asyncs.remove(ctx);
			}
			
			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				asyncs.remove(ctx);
			}
			
			@Override
			public void onError(AsyncEvent event) throws IOException {
				asyncs.remove(ctx);
			}
			
			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {}
		});
		
		asyncs.add(ctx);
	}
	
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
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return prop;
	}
	
}
