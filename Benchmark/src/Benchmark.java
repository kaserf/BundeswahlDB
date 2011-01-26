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
	static final int WORKERS = 50;
	static final String FILE_PATH = "C:\\Users\\Eva\\Uni\\WS 1011\\Datenbanken\\BundeswahlDB\\Benchmark\\Results\\result-"
			+ System.currentTimeMillis() + ".csv";
	static final int MULTIPLIER = 20;

	public static void main(String[] args) {
		// start log queue
		BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<LogEntry>();
		LogSaver logSaver = new LogSaver(logQueue, FILE_PATH,
				new CSVFormatter());
		Thread logThread = new Thread(logSaver);
		logThread.start();

		// create CardDeck
		Map<String, Integer> urlQuantityMap = new HashMap<String, Integer>();
		urlQuantityMap.put("sitzverteilung.jsp", 25);
		urlQuantityMap.put("mitglieder.jsp", 10);
		urlQuantityMap.put(
				"wahlkreisuebersicht/wahlkreisprofile.jsp?wahlkreis=24", 25);
		// urlQuantityMap.put("wahlkreissieger.jsp", 10);
		urlQuantityMap.put(
				"ueberhangmandate/ueberhangmandattabelle.jsp?bundesland=1", 10);
		// urlQuantityMap.put("knappstesieger/top10.jsp?partei=3", 20);
		CardDeck cardDeck = new CardDeck(urlQuantityMap, MULTIPLIER);

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
