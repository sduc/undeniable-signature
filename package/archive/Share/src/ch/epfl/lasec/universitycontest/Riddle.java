package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import ch.epfl.lasec.ArrayUtils;
import ch.epfl.lasec.IOHelper;

/**
 * Class representing a riddle for the university contest.
 * 
 * @author Sebastien Duc
 *
 */
public class Riddle extends Challenge {
	
	/**
	 * String containing the riddle
	 */
	private String riddle;
	
	/**
	 * String containing the answer added by the students
	 */
	private String answer = null;
	
	/**
	 * Creates a riddle
	 * @param riddle String representing the riddle
	 * @param dueTo date when the answer is due.
	 * @param title the title of the riddle
	 */
	public Riddle(String riddle, Date dueTo, String title) {
		super(dueTo, title);
		this.riddle = riddle;
	}
	
	/**
	 * Creates a riddle
	 * @param riddle String representing the riddle
	 * @param dueTo the date when the answer is due
	 * @param title the title of the riddle
	 * @throws ParseException
	 */
	public Riddle(String riddle, String dueTo, String title) throws ParseException {
		super(dueTo, title);
		this.riddle = riddle;
	}
	
	/**
	 * Creates a riddle from an encoded version.
	 * @param enc the encoded version of the riddle
	 */
	public Riddle(byte [] enc) {
		enc = Arrays.copyOfRange(enc, 1, enc.length);
		try {
			// recover title
			int from = 1 + enc[0];
			int to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
			byte [] titleEnc = Arrays.copyOfRange(enc, from, to);
			enc = Arrays.copyOfRange(enc, to, enc.length);
			super.setTitle(titleEnc);
			
			// recover date
			String date = new String(Arrays.copyOfRange(enc, 1, 1+enc[0]),CHARSET);
			super.setDate(date);
			
			enc = Arrays.copyOfRange(enc, 1+enc[0], enc.length);
			// recover riddle string
			from = 1 + enc[0];
			to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
			byte [] riddleEnc = Arrays.copyOfRange(enc, from, to);
			this.riddle = new String(riddleEnc, CHARSET);
			// recover answer if any
			if (to < enc.length){
				enc = Arrays.copyOfRange(enc, to, enc.length);
				this.answer = new String(enc,CHARSET);
			}
		} catch (UnsupportedEncodingException e) {}
	}
	
	/**
	 * Get the riddle string of characters
	 * @return the riddle in String of characters
	 */
	public String getRiddle(){
		return this .riddle;
	}
	
	/**
	 * Get the byte encoded version of the riddle.
	 * @return an array of bytes (byte[]) which encodes this.
	 */
	public byte [] getEncoded(){
		try {
			byte [] titleEnc = super.getTitle().getBytes(CHARSET);
			byte [] titleEncLen = BigInteger.valueOf(titleEnc.length).toByteArray();
			byte [] dateEnc = super.getDueTo().getBytes(CHARSET);
			byte [] riddleEnc = riddle.getBytes(CHARSET);
			byte [] riddleEncLen = BigInteger.valueOf(riddleEnc.length).toByteArray();
			byte [] enc = ArrayUtils.concatAll(new byte [] {RIDDLE_TYPE},
					new byte []{(byte) titleEncLen.length},
					titleEncLen,
					titleEnc,
					new byte []{(byte) dateEnc.length},
					dateEnc,
					new byte []{(byte) riddleEncLen.length},
					riddleEncLen,
					riddleEnc);
			if(answer != null){
				byte [] answEnc = answer.getBytes(CHARSET);
				enc = ArrayUtils.concatAll(enc, answEnc);
			}
			return enc;
		} catch (UnsupportedEncodingException e) {}
		return null;
	}
	
	/**
	 * Creates a new riddle from an encoded version
	 * @param enc the encoded version of the riddle
	 * @return the riddle that gets encoded to enc
	 */
	public static Riddle fromEncoded(byte [] enc){
		return new Riddle(enc);
	}
	
	/**
	 * Set the answer to answer.
	 * @param answer
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Get the answer.
	 * @return the answer
	 */
	public String getAnswer() {
		return this.answer;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj == this){
			return true;
		}
		if(obj.getClass() != this.getClass()){
			return false;
		}
		Riddle q = (Riddle) obj;
		boolean b = false;
		if (this.answer == null && q.answer == null)
			b = true;
		else if (this.answer != null && this.answer.equals(q.answer))
			b = true;
		return this.riddle.equals(q.riddle) && b && super.equals(q);
	}

	@Override
	public String toString() {
		return "Riddle [riddle=" + riddle + ", answer=" + answer + "]" + super.toString();
	}

	@Override
	public void write(OutputStream out) throws UnsupportedEncodingException,
			IOException {
		out.write(RIDDLE_TYPE);
		IOHelper.writeEncodedObject(out, this.getEncoded());
		
	}
	
	public static Riddle read(InputStream in) throws IOException {
		return new Riddle(IOHelper.readEncodedObject(in));
	}
	
}
