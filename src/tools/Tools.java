package tools;

/**
 * Provide static helper methods for this project
 */
public class Tools {
	/**
	 * Returns the index of the given string in the given string table
	 * @param array The string table
	 * @param string The string for which the index is requested
	 * @return The index of the string in the table
	 */
	public static int getArrayIndexOf(String[] array, String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(string)) return i;
		}
		return -1;
	}

	/**
	 * Returns true if the given string is contained in the given string table
	 * @param array The string table
	 * @param string The string for which the index is requested
	 * @return True if the string is contained in the string table, false otherwise
	 */
	public static boolean stringArrayContains(String[] array, String string) {
		return getArrayIndexOf(array, string) >= 0;
	}

}
