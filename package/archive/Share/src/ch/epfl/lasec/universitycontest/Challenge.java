package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.lasec.mova.MovaSignature;
import ch.epfl.lasec.tuple.TupleType;


/**
 * Class used to represent a challenge given to a team
 * 
 * @author Sebastien Duc
 *
 */
/**
 * @author Sebastien Duc
 *
 */
abstract public class Challenge {
	
	/**
	 * Charset used to encode strings
	 */
	public final static String CHARSET = "UTF-8";
	
	
	public final static byte RIDDLE_TYPE = 0;
	
	public final static byte QUIZ_TYPE = 1;
	
	/**
	 * Date format used
	 */
	public static final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
	
	
	/**
	 * Couple (Challenge,MovaSignature) type.
	 */
	public static final TupleType CHALLENGE_SIGNATURE_COUPLE_TYPE = 
    		TupleType.DefaultFactory.create(Challenge.class,MovaSignature.class);

	
	/**
	 * Due to date for this challenge
	 */
	private Date dueTo;
	
	/**
	 * The title of the challenge
	 */
	private String title;
	
	/**
	 * Get the encoded version of the challenge
	 * @return the encoded version of the challenge
	 */
	abstract public byte [] getEncoded();
	
	/**
	 * Create a challenge which is due to dueTo
	 * @param dueTo
	 * @param title the title of the challenge
	 */
	public Challenge(Date dueTo, String title) {
		this.dueTo = (Date) dueTo.clone();
		this.title = title;
	}
	
	public Challenge() {
		this.dueTo = new Date();
		this.title = null;
	}
	
	/**
	 * Creates a challenge from  it's encoded version
	 * @param enc
	 * @return
	 */
	public static Challenge fromEncoded(byte [] enc) {
		byte type = enc[0];
		if (type == QUIZ_TYPE){
			return Quiz.fromEncoded(enc);
		}
		else {
			return Riddle.fromEncoded(enc);
		}
	}
	
	/**
	 * Write the challenge in a stream
	 * @param out stream
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	abstract public void write(OutputStream out) 
			throws UnsupportedEncodingException, IOException; 
	
	/**
	 * Read a challenge from a stream that was previously written using write.
	 * @param in stream
	 * @return the challenge that was read.
	 * @throws IOException 
	 */
	public static Challenge read(InputStream in) throws IOException {
		byte type = (byte) in.read();
		if (type == QUIZ_TYPE){
			return Quiz.read(in);
		}
		else {
			return Riddle.read(in);
		}
	}

	
	/**
	 * Create a challenge which is due to dueTo. Format of dueTo must be yyyy-MM-dd
	 * @param duteTo
	 * @throws ParseException if dueTo has wrong format
	 */
	public Challenge(String dueTo, String title) throws ParseException {
		this.dueTo = dfm.parse(dueTo);
		this.title = title;
	}
	
	/**
	 * Set the due to date to d
	 * @param d
	 */
	public void setDate(Date d){
		this.dueTo = (Date) d.clone();
	}
	
	/**
	 * Set the due to date to d
	 * @param d
	 */
	public void setDate(String d){
		try {
			this.dueTo = dfm.parse(d);
		} catch (ParseException e) {}
	}
	
	/**
	 * Getter for dueTo
	 * @return dueTo field
	 */
	public Date getDueToDate(){
		return this.dueTo;
	}
	
	/**
	 * Get the dueTo date in string format
	 * @return dueTo field in a string format
	 */
	public String getDueTo(){
		return dfm.format(dueTo);
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setTitle(byte [] enc){
		try {
			this.title = new String(enc,CHARSET);
		} catch (UnsupportedEncodingException e) {}
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
		Challenge c = (Challenge) obj;
		return this.title.equals(c.getTitle()) && this.dueTo.equals(c.dueTo);
	}

	@Override
	public String toString() {
		return "Challenge [dfm=" + dfm + ", dueTo=" + dueTo + ", title="
				+ title + "]";
	}
	
}
