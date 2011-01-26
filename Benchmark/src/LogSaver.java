import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a thread that saves log entries to a log file.
 * 
 * @author niessner
 */
public class LogSaver implements Runnable {

	/** The queue of log entries. */
	private BlockingQueue<LogEntry> entryQueue;

	/** The print writer. */
	private PrintStream printWriter;

	/** Indicates whether thread is stopped. */
	private boolean stop = false;

	private LogEntryFormatter formatter;

	public LogSaver(BlockingQueue<LogEntry> entryQueue, String filename,
			LogEntryFormatter formatter) {
		this.entryQueue = entryQueue;
		try {
			printWriter = new PrintStream(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Using System.out");
			printWriter = System.out;
		}
		this.formatter = formatter;
	}

	/** Constructor. */
	public LogSaver(BlockingQueue<LogEntry> entryQueue, String filename) {
		this(entryQueue, filename, new TextFormatter());
	}

	/** Constructor with writing to the console. */
	public LogSaver(BlockingQueue<LogEntry> entryQueue) {
		this.entryQueue = entryQueue;
		printWriter = System.out;
		this.formatter = new TextFormatter();
	}

	/** Sets stop. */
	public void setStop(boolean stop) {
		this.stop = stop;
	}

	/** Performs the actual writing of log entries to the log file. */
	private void doRun() throws Exception {
		printWriter.println(formatter.getHeadLine());
		while (!stop) {
			LogEntry entry = entryQueue.poll(1, TimeUnit.SECONDS);
			if (entry != null) {
				printWriter.println(formatter.format(entry));
				System.out.println(formatter.format(entry));
			}
		}
		synchronized (entryQueue) {
			while (!entryQueue.isEmpty()) {
				LogEntry entry = entryQueue.poll();
				if (entry != null) {
					printWriter.println(formatter.format(entry));
					System.out.println(formatter.format(entry));
				}
			}
		}
		if (printWriter != System.out) {
			printWriter.close();
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

}
