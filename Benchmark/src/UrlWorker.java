import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a thread that calls an url and takes the response time 
 * by drawing a card from a card deck.
 * 
 * @author niessner
 */
public class UrlWorker implements Runnable {

	/** The queue of cards to be drawn from. */
	private BlockingQueue<Card> cardQueue;
	
	/** The queue of log entries to be filled. */
	private BlockingQueue<LogEntry> logQueue;
	
	/** Indicates whether thread was stopped. */
	private boolean stop = false;
	
	/** Constructor. */
	public UrlWorker(BlockingQueue<Card> cardQueue, 
			BlockingQueue<LogEntry> logQueue) {
		this.cardQueue = cardQueue;
		this.logQueue = logQueue;
	}
	
	/** Performs the actual call of a website by drawing from the card queue,
	 * taking the response time and writing results to the log queue.
	 */
	private void doRun() throws Exception {
		while (!stop) {
			Card card = cardQueue.poll(1, TimeUnit.SECONDS);
			if (card == null) {
				continue;
			}
			long startTime;
			long openTime;
			long readTime;
			
			// call the url in the card
			URL url = new URL(card.getUrl());
			
			// take time
			startTime = System.currentTimeMillis();
			InputStream urlStream = url.openStream();
			openTime = System.currentTimeMillis();
			
			byte[] buffer = new byte[1024];
			while (urlStream.read(buffer) > 0) {}
			
			readTime = System.currentTimeMillis();
			long diffOpen = openTime - startTime;
			long diffRead = readTime - openTime;
			LogEntry logEntry = new LogEntry(Thread.currentThread().getName(), 
				card.getUrl(), startTime, diffOpen, diffRead);
			logQueue.add(logEntry);
			urlStream.close();
		}
	}
	
	@Override
	public void run() {
		try {
			doRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Sets stop. */
	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
