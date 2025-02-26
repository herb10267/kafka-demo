package serverSentEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
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

@WebServlet(
	urlPatterns={"/serverSentEventWithKafka"},
	asyncSupported = true
)
public class serverSentEventWithKafka extends HttpServlet {
	
	private Queue<AsyncContext> asyncsKafka;
	
	@Override
	public void init() throws ServletException {
		asyncsKafka = (Queue<AsyncContext>) getServletContext().getAttribute("asyncsKafka");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/event-stream");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		AsyncContext ctx = request.startAsync();
		ctx.setTimeout(Long.MAX_VALUE);
		
		ctx.addListener(new AsyncListener() {
			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				System.out.println("AsyncEvent onComplete");
				asyncsKafka.remove(ctx);
			}
			
			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				asyncsKafka.remove(ctx);
			}
			
			@Override
			public void onError(AsyncEvent event) throws IOException {
				asyncsKafka.remove(ctx);
			}
			
			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
				System.out.println("AsyncEvent onStartAsync");
			}
		});
		
		asyncsKafka.add(ctx);
	}
	
}
