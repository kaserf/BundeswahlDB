import java.util.Date;

public class CSVFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry entry) {
		long overallTime = entry.getDiffOpen() + entry.getDiffRead();
		return entry.getThreadName() + ";" + entry.getUrl() + ";"
				+ new Date(entry.getStartTime()) + ";" + entry.getDiffOpen()
				+ ";" + entry.getDiffRead() + ";" + overallTime;
	}

	@Override
	public String getHeadLine() {
		return "Thread Name;URL;Start Time;Open Time;Read Time;Overall Time";
	}

}
