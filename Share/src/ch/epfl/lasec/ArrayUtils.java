package ch.epfl.lasec;

import java.util.Arrays;

/**
 * Utility class for arrays
 * 
 * @author Sebastien Duc
 * 
 *
 */
public class ArrayUtils {

	/**
	 * Method used to concatenate arrays first and rest of type T
	 * 
	 * @param first  first array
	 * @param rest  other arrays
	 * @return an array containing first all elements in first then elements in rest
	 * 
	 * @author Joachim Sauer
	 */
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * Method used to concatenate arrays first and rest of type byte
	 * 
	 * @param first first array
	 * @param rest other arrays
	 * @return an array containing first all elements in first then elements in rest
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * Method used to concatenate arrays first and rest of type int
	 * 
	 * @param first first array
	 * @param rest other arrays
	 * @return an array containing first all elements in first then elements in rest
	 */
	public static int[] concatAll(int[] first, int[]... rest) {
		int totalLength = first.length;
		for (int[] array : rest) {
			totalLength += array.length;
		}
		int[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (int[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * Method used to concatenate arrays first and rest of type double
	 * 
	 * @param first first array
	 * @param rest other arrays
	 * @return an array containing first all elements in first then elements in rest
	 */
	public static double[] concatAll(double[] first, double[]... rest) {
		int totalLength = first.length;
		for (double[] array : rest) {
			totalLength += array.length;
		}
		double[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (double[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
	
	/**
	 * Method used to concatenate arrays first and rest of type char
	 * 
	 * @param first first array
	 * @param rest other arrays
	 * @return an array containing first all elements in first then elements in rest
	 */
	public static char[] concatAll(char[] first, char[]... rest) {
		int totalLength = first.length;
		for (char[] array : rest) {
			totalLength += array.length;
		}
		char[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (char[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
}
