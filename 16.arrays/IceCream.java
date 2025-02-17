import java.util.*;

public class IceCream {
	private static Random rand = new Random(88);
	static final String[] FLAVORS = {
		"Chocolate", "Strawberry", "Vanilla Fudge Swirl",
		"Mint Chip", "Mocha Alomnd Fudge", "Rum Raisin",
		"Praline Cream", "Mud Pie"
	};
	public static String[] flavorSet(int n) {
		if(n > FLAVORS.length) {
			throw new IllegalArgumentException("Set too big");
		}
		String[] results = new String[n];
		boolean[] picked = new boolean[FLAVORS.length];
		for(int i = 0; i < n; ++i) {
			int t;
			do {
				t = rand.nextInt(FLAVORS.length);
			} while(picked[t]);
			picked[t] = true;
			results[i] = FLAVORS[t];
		}
		return results;
	}
	public static void main(String[] args) {
		for(int i = 0; i < 7; ++i) {
			System.out.println(Arrays.toString(flavorSet(3)));
		}
	}
}
