import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class runs a multithreaded benchmark over several websites.
 * 
 * @author niessner
 * @author kaserf
 */
public class Benchmark {
	static int WORKERS = 1;
	static String FILE_PATH = "C:\\Users\\Eva\\Uni\\WS 1011\\Datenbanken\\BundeswahlDB\\Benchmark\\Results\\result-"
			+ System.currentTimeMillis() + ".csv";

	static int RUNTIME = 1000 * 60; // 20 seconds in millis.

	static int WAITING_TIME = 0;

	static Map<String, Integer> urlQuantityMap = new HashMap<String, Integer>();

	private static void parseArgs(String[] args) {
		String infoMessage = "Valid arguments are: --workers=<number of workers> "
				+ "--file_path=<path> --wait=<waiting time in millis> and --runtime=<runtime in millis> --url=<url>,<percent>";
		if (args.length == 1 && args[0].equals("--help")) {
			System.out.println(infoMessage);
			System.exit(0);
		}
		for (String arg : args) {
			if (!arg.contains("=") || !arg.startsWith("--")) {
				throw new RuntimeException(infoMessage);
			} else {
				String argName = arg.split("=", 2)[0];
				String argValue = arg.split("=", 2)[1];
				argName = argName.substring(2);
				System.out.println("Found arg: " + argName);
				if (argName.equals("workers")) {
					WORKERS = Integer.parseInt(argValue);
				} else if (argName.equals("wait")) {
					WAITING_TIME = Integer.parseInt(argValue);
				} else if (argName.equals("file_path")) {
					FILE_PATH = argValue;
				} else if (argName.equals("runtime")) {
					RUNTIME = Integer.parseInt(argValue);
				} else if (argName.equals("url")) {
					String url = argValue.split(",")[0];
					int percent = Integer.parseInt(argValue.split(",")[1]);
					urlQuantityMap.put(url, percent);
				} else {
					throw new RuntimeException(infoMessage);
				}
			}
		}
	}

	public static void main(String[] args) {
		parseArgs(args);
		// start log queue
		BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<LogEntry>();
		LogSaver logSaver = new LogSaver(logQueue, FILE_PATH,
				new CSVFormatter());
		Thread logThread = new Thread(logSaver);
		logThread.start();

		// create CardDeck
		if (urlQuantityMap.isEmpty()) {
			urlQuantityMap.put("sitzverteilung.jsp", 25);
			urlQuantityMap.put("mitglieder.jsp", 10);
			urlQuantityMap
					.put("wahlkreisuebersicht/wahlkreisprofile.jsp?live=false&wahlkreis=24",
							25);
			urlQuantityMap.put("wahlkreissieger.jsp", 10);
			urlQuantityMap.put(
					"ueberhangmandate/ueberhangmandattabelle.jsp?bundesland=1",
					10);
			urlQuantityMap.put("knappstesieger/top10.jsp?partei=3", 20);
			// This would be the URL for Q7:
			// urlQuantityMap
			// .put("wahlkreisuebersicht/wahlkreisprofile.jsp?live=true&wahlkreis=26",
			// 25);
		}
		long finishTime = System.currentTimeMillis() + RUNTIME;

		// start card queue
		Queue<Thread> workerThreads = new LinkedList<Thread>();
		for (int i = 0; i < WORKERS; i++) {
			UrlWorker urlWorker = new UrlWorker(urlQuantityMap, finishTime,
					WAITING_TIME, logQueue);
			Thread urlThread = new Thread(urlWorker);
			urlThread.start();
			workerThreads.add(urlThread);
		}

		while (!workerThreads.isEmpty()) {
			try {
				workerThreads.poll().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logSaver.setStop(true);
	}
}
