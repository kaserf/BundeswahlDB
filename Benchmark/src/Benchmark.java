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
 */
public class Benchmark {
	
	public static void main (String[] args) {
		// start log queue
		BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<LogEntry>();
		LogSaver logSaver = new LogSaver(logQueue);
		Thread logThread = new Thread(logSaver);
		logThread.start();
		
		// create CardDeck
		Map<String, Integer> urlQuantityMap = new HashMap<String, Integer>();
		urlQuantityMap.put("http://www.web.de", 40);
		urlQuantityMap.put("http://www.ebay.de", 30);
		urlQuantityMap.put("http://www.in.tum.de", 80);
		urlQuantityMap.put("http://www.wetter.de", 96);
		CardDeck cardDeck = new CardDeck(urlQuantityMap);
		
		// start card queue
		int threadPoolSize = 40;
		BlockingQueue<Card> cardQueue = new LinkedBlockingQueue<Card>();
		cardQueue.addAll(cardDeck.getCards());
		List<UrlWorker> threadPool = new ArrayList<UrlWorker>();
		for (int i = 0; i < threadPoolSize; i++) {
			UrlWorker urlWorker = new UrlWorker(cardQueue, logQueue);
			threadPool.add(urlWorker);
			Thread urlThread = new Thread(urlWorker);
			urlThread.start();
		}
		
		while (!cardQueue.isEmpty()) {
			try {
				Thread.sleep(1000);
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
