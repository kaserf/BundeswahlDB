import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * This class represents a thread that calls an url and takes the response time
 * by drawing a card from a card deck.
 * 
 * @author niessner
 */
public class UrlWorker implements Runnable {

	/** The queue of cards to be drawn from. */
	private Queue<Card> cardQueue;

	/** The queue of log entries to be filled. */
	private BlockingQueue<LogEntry> logQueue;

	private Map<String, Integer> urlQuantityMap;

	private long finish;

	private long waitingTime;

	private Random random = new Random();

	/** Constructor. */
	public UrlWorker(BlockingQueue<Card> cardQueue,
			BlockingQueue<LogEntry> logQueue) {

	}

	public UrlWorker(Map<String, Integer> urlQuantityMap, long finish,
			long waitingTime, BlockingQueue<LogEntry> logQueue) {
		this.urlQuantityMap = urlQuantityMap;
		this.logQueue = logQueue;
		this.finish = finish;
		this.waitingTime = waitingTime;
	}

	/**
	 * Performs the actual call of a website by drawing from the card queue,
	 * taking the response time and writing results to the log queue.
	 */
	private void doRun() throws Exception {
		while (System.currentTimeMillis() < finish) {

			Card card = getCard();
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
			while (urlStream.read(buffer) > 0) {
			}

			readTime = System.currentTimeMillis();
			long diffOpen = openTime - startTime;
			long diffRead = readTime - openTime;
			LogEntry logEntry = new LogEntry(Thread.currentThread().getName(),
					card.getUrl(), startTime, diffOpen, diffRead);
			logQueue.add(logEntry);
			urlStream.close();

			try {
				Thread.sleep(Math.abs((long) (random.nextGaussian() * 100 + waitingTime)));
			} catch (InterruptedException e) {

			}
		}
	}

	private Card getCard() {
		if (cardQueue == null || cardQueue.isEmpty()) {
			cardQueue = new LinkedList<Card>(
					new CardDeck(urlQuantityMap).getCards());
		}
		return cardQueue.poll();
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
