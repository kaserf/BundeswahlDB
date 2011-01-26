import java.util.Date;

/**
 * This class represents one entry to the log file.
 * 
 * @author niessner
 */
public class LogEntry {

	/** The name of the thread. */
	private String threadName;

	/** The url. */
	private String url;

	/** The start time. */
	private long startTime;

	/** The time to open the url. */
	private long diffOpen;

	/** The time to read from the url. */
	private long diffRead;

	public String getThreadName() {
		return threadName;
	}

	public String getUrl() {
		return url;
	}

	public long getDiffOpen() {
		return diffOpen;
	}

	public long getDiffRead() {
		return diffRead;
	}

	public long getStartTime() {
		return startTime;
	}

	/** Constructor. */
	public LogEntry(String threadName, String url, long startTime,
			long diffOpen, long diffRead) {
		this.threadName = threadName;
		this.url = url;
		this.startTime = startTime;
		this.diffOpen = diffOpen;
		this.diffRead = diffRead;
	}

	/** Returns the log entry string representation. */
	public String toString() {
		long overallTime = diffOpen + diffRead;
		return "Thread " + threadName + ", URL: " + url + ", start time: "
				+ new Date(startTime) + ", time to open: " + diffOpen + " ms"
				+ ", time to read: " + diffRead + " ms"
				+ ", overall processing time: " + overallTime + " ms";
	}
}