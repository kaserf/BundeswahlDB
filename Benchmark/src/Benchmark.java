import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class runs a multithreaded benchmark over several websites.
 * 
 * @author niessner
 * @author kaserf
 */
public class Benchmark {
	static final int DELAY = 1000;
	static final int WORKERS = 40;
	
	public static void main (String[] args) {
		// start log queue
		BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<LogEntry>();
		LogSaver logSaver = new LogSaver(logQueue);
		Thread logThread = new Thread(logSaver);
		logThread.start();
		
		// create CardDeck
		Map<String, Integer> urlQuantityMap = new HashMap<String, Integer>();
		urlQuantityMap.put("http://localhost/Q1.html", 25);
		urlQuantityMap.put("http://localhost/Q2.html", 10);
		urlQuantityMap.put("http://localhost/Q3.html", 25);
		urlQuantityMap.put("http://localhost/Q4.html", 10);
		urlQuantityMap.put("http://localhost/Q5.html", 10);
		urlQuantityMap.put("http://localhost/Q6.html", 20);
		CardDeck cardDeck = new CardDeck(urlQuantityMap);
		
		// start card queue
		BlockingQueue<Card> cardQueue = new LinkedBlockingQueue<Card>();
		cardQueue.addAll(cardDeck.getCards());
		List<UrlWorker> threadPool = new ArrayList<UrlWorker>();
		for (int i = 0; i < WORKERS; i++) {
			UrlWorker urlWorker = new UrlWorker(cardQueue, logQueue);
			threadPool.add(urlWorker);
			Thread urlThread = new Thread(urlWorker);
			urlThread.start();
		}
		
		while (!cardQueue.isEmpty()) {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (UrlWorker worker : threadPool) {
			worker.setStop(true);
		}
		logSaver.setStop(true);
	}
}
