import java.util.*;

public class PPrint {
	public static String pformat(Collection<?> c) {
		if(c.size() == 0) return "[]";
		StringBuilder result = new StringBuilder("[");
		for(Object item : c) {
			if(c.size() != 1) result.append("\n");
			result.append(item);
		}
		if(c.size() != 1) result.append("\n");
		result.append("]");
		return result.toString();	
	}
	public static void pprint(Collection<?> c) {
		System.out.println(pformat(c));
	}
	public static void pprint(Object[] c) {
		System.out.println(pformat(Arrays.asList(c)));
	}
}
