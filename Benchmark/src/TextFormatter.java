import java.util.Date;

public class TextFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry entry) {
		long overallTime = entry.getDiffOpen() + entry.getDiffRead();
		return "Thread " + entry.getThreadName() + ", URL: " + entry.getUrl()
				+ ", start time: " + new Date(entry.getStartTime())
				+ ", time to open: " + entry.getDiffOpen() + " ms"
				+ ", time to read: " + entry.getDiffRead() + " ms"
				+ ", overall processing time: " + overallTime + " ms";

	}

	@Override
	public String getHeadLine() {
		return "DATABASE BENCHMARK RUN";
	}

}
