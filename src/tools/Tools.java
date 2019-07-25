package tools;

public class Tools {
	public static int getArrayIndexOf(String[] array, String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(string)) return i;
		}
		return -1;
	}
}
