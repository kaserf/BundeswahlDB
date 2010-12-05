import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a collection of websites to be called. 
 * Each website is represented through several cards depending on the 
 * desired frequency of calls.
 * 
 * @author niessner
 */
public class CardDeck {
	
	/** The cards of the card deck. */
	private List<Card> cards = new ArrayList<Card>();
	
	/** Calculate greatest common divisor. */
	private int ggt(List<Integer> ints) {
		Set<Set<Integer>> divisors = new HashSet<Set<Integer>>();
		for (int teil : ints) {
			divisors.add(getDivisors(teil));
		}
		Set<Integer> finalSet = new HashSet<Integer>(divisors.iterator().next());
		for (Set<Integer> divisorSet : divisors) {
			finalSet.retainAll(divisorSet);
		}
		int maxVal = 1;
		for (Integer val : finalSet) {
			if (val > maxVal) {
				maxVal = val;
			}
		}
		return maxVal;
	}
	
	/** Gets all divisors from an int. */
	private Set<Integer> getDivisors(int number) {
		Set<Integer> divisorSet = new HashSet<Integer>();
		for (int i = 2; i <= number; i++) {
			if (number % i == 0) {
				divisorSet.add(i);
			}
		}
		return divisorSet;
	}

	/** The Constructor. Cards will be created and shuffled for random access.
	 * @param urlQuantityMap urls to be called combined with frequency of calls.
	 */
	public CardDeck(Map<String, Integer> urlQuantityMap) {
		List<Integer> lst = new ArrayList<Integer>();
		for (String url: urlQuantityMap.keySet()) {
			lst.add(urlQuantityMap.get(url));
		}
		int divisor = ggt(lst);
		for (String url : urlQuantityMap.keySet()) {
			int number = urlQuantityMap.get(url) / divisor;
			for (int i = 0; i < number; i++) {
				Card card = new Card(url);
				cards.add(card);
			}
		}
		Collections.shuffle(cards);
	}
	
	/** Gets cards. */
	public List<Card> getCards() {
		return cards;
	}
	
}
