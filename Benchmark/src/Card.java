/**
 * A card represents a website to be called.
 * 
 * @author niessner
 */
public class Card {
	
	/** The url of the Website to be called. */
	private String url;
	
	/** Constructor. */
	public Card(String url) {
		this.url = url;
	}
	
	/** Gets url. */
	public String getUrl() {
		return url;
	}
}
