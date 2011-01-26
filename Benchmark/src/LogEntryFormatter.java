public interface LogEntryFormatter {
	public String getHeadLine();

	public String format(LogEntry entry);
}
