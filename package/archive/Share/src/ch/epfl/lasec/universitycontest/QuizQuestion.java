package ch.epfl.lasec.universitycontest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import ch.epfl.lasec.ArrayUtils;
import ch.epfl.lasec.IOHelper;

/**
 * Class used to represent one question in a quiz
 * 
 * @author Sebastien Duc
 *
 */
public class QuizQuestion {
	
	/**
	 * Question
	 */
	private String question;
	
	
	/**
	 * Answer to the question added by the students
	 */
	private String answer = null;
	
	public static final int NULL = 0;
	public static final int NOT_NULL = 1;
	
	/**
	 * Create a question
	 * @param question
	 */
	public QuizQuestion(String question) {
		this.question = question;
	}
	
	/**
	 * Create a copy of question q
	 * @param q question to copy
	 */
	public QuizQuestion(QuizQuestion q) {
		this.question = q.getQuestion();
		this.answer = q.getAnswer();
	}
	
	/**
	 * Creates a quiz question from its encoded version.
	 * @param enc the encoded version of the quiz question
	 */
	public QuizQuestion(byte [] enc) {
		try {
			// recover the question
			int from = 1+enc[0];
			int to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
			byte [] questionEnc = Arrays.copyOfRange(enc, from, to);
			this.question = new String(questionEnc,Challenge.CHARSET);
			// recover the answer
			if (to < enc.length){
				enc = Arrays.copyOfRange(enc, to, enc.length);
				this.answer = new String(enc,Challenge.CHARSET);
			}
			
		} catch (UnsupportedEncodingException e) {}
	}
	
	/**
	 * Get the question
	 * @return the question
	 */
	public String getQuestion(){
		return this.question;
	}
	
	/**
	 * Get the encoded version of this question.
	 * @return the encoded version of this question
	 */
	public byte[] getEncoded(){
		try {
			byte[] questionEnc = this.question.getBytes(Challenge.CHARSET);
			byte[] questionEncLen = BigInteger.valueOf(questionEnc.length)
					.toByteArray();
			byte[] enc = ArrayUtils.concatAll(
					new byte[] { (byte) questionEncLen.length },
					questionEncLen, questionEnc);
			if (answer != null)
				enc = ArrayUtils.concatAll(enc,
						answer.getBytes(Challenge.CHARSET));
			return enc;
		} catch (UnsupportedEncodingException e) {}
		return null;
	}
	
	/**
	 * Creates a quiz question from its encoded version.
	 * @param enc the encoded version of the quiz question
	 * @return the new quiz question that was retrieved from enc
	 */
	public static QuizQuestion fromEncoded(byte [] enc) {
		return new QuizQuestion(enc);
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
		QuizQuestion q = (QuizQuestion) obj;
		return this.question.equals(q.question);
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getAnswer() {
		return this.answer;
	}

	@Override
	public String toString() {
		return "QuizQuestion [question=" + question + ", answer=" + answer
				+ "]";
	}
	
	/**
	 * Write this in a stream
	 * @param out stream
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void write(OutputStream out) throws UnsupportedEncodingException, IOException{
		IOHelper.writeEncodedString(out, this.question);
		
		if (this.getAnswer() == null) {
			out.write(NULL);
		}
		else {
			out.write(NOT_NULL);
			IOHelper.writeEncodedString(out, this.getAnswer());
		}
	}
	
	/**
	 * Read a quiz question from a stream that was previously written by write.
	 * @param in stream
	 * @return the quiz question that was read from the stream in
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static QuizQuestion read(InputStream in) 
			throws UnsupportedEncodingException, IOException {
		String question = IOHelper.readEncodedString(in);
		
		QuizQuestion qq = new QuizQuestion(question);
		
		if (in.read() == NOT_NULL){
			qq.setAnswer(IOHelper.readEncodedString(in));
		}
		return qq;
	}
	
}
