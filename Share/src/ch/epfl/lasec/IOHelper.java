package ch.epfl.lasec;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import ch.epfl.lasec.mova.YGroupElement;

/**
 * Class implementing static methods used for IOstreams
 * 
 * @author Sebastien Duc
 *
 */
public class IOHelper {
	
	/**
	 * Close quietly a Closeable object
	 * 
	 * @param s Object to close
	 */
	public static void closeQuietly(Closeable s){
		try {
			if(s != null){
				s.close();
			}
		} catch (IOException e) {
			// Ignore exception
		}
	}
	
	/**
	 * Method used to write in an outputStream an encoded version in byte [] of a BigInteger
	 * 
	 * @param os OutputStream used to write
	 * @param i BigInteger to send
	 * @throws IOException
	 */
	public static void writeEncodedBigInt(OutputStream os, BigInteger i) throws IOException{
		writeEncodedObject(os, i.toByteArray());
	}
	
	/**
	 * Method used to read in an InputStream an encoded BigInteger that was previously written 
	 * by writeEncodedBigInt
	 * 
	 * @param is InputStream used to read
	 * @return Returns the BigInteger that was read
	 * @throws IOException
	 */
	public static BigInteger readEncodedBigInt(InputStream is) throws IOException{
		return new BigInteger(readEncodedObject(is));
	}
	
	/**
	 * Method used to write an array of Ygroup Elements. In that case, since d:= |Ygroup| = 2,
	 * we need only one bit to represent one element.
	 * 
	 * @param os OutputStream used to write the array
	 * @param elements YGroupElements to write
	 * @throws IOException 
	 */
	public static void writeEncodedYGroupElements(OutputStream os, 
			YGroupElement [] elements) throws IOException{
		writeEncodedObject(os, YGroupElement.encodeYGroupElementSequence(elements));
	}
	
	/**
	 * Method used to read an array of Ygroup Elements that where previously written by 
	 * writeEncodedYGroupElements.
	 * 
	 * @param is InputStream used to read the encoded elements
	 * @return Returns the elements that where read from the InputStream.
	 * @throws IOException 
	 */
	public static YGroupElement [] readEncodedYGroupElements(InputStream is) throws IOException{
		return YGroupElement.decodeEncodedYGroupElementSequence(readEncodedObject(is));
	}
	
	
	/**
	 * Called to write an encoded object (in bytes) into a stream.
	 * @param os OutputStream in which enc is written
	 * @param enc Array of bytes encoding the object to be written
	 * @throws IOException 
	 */
	public static void writeEncodedObject(OutputStream os , byte [] enc) 
			throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(enc.length);
		os.write(enc);
	}
	
	/**
	 * Called to read an encoded object (in bytes) from a stream which was written
	 * using writeEncodedObject. 
	 * @param is InputStream from which the object is retrieved
	 * @return the encoded object (in bytes)
	 * @throws IOException
	 */
	public static byte [] readEncodedObject(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		int len = dis.readInt();
		byte [] enc = new byte [len];
		is.read(enc);
		return enc;
	}
	
	/**
	 * Write a string in a stream
	 * @param os stream
	 * @param s string to write
	 * @throws IOException
	 */
	public static void writeEncodedString(OutputStream os , String s) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		
		dos.writeInt(s.length());
		dos.writeChars(s);
		
	}
	
	/**
	 * Read a string from a stream
	 * @param in stream
	 * @return the string that was read
	 * @throws IOException
	 */
	public static String readEncodedString(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		
		int len = dis.readInt();
		String s = "";
		for (int i = 0; i < len; i++) {
			s += dis.readChar();
		}
		
		return s;
	}
	
}
