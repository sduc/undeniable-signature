package ch.epfl.lasec.universitycontest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import ch.epfl.lasec.ArrayUtils;

/**
 * Class used to represent a multiple choice question
 * 
 * @author Sebastien Duc
 *
 */
public class MultipleChoiceQuestion extends QuizQuestion {
	
	/**
	 * Possible answers for the question
	 */
	private String [] possibleAnswers;
	
	/**
	 * Create a multiple choice question
	 * @param question
	 * @param answers
	 */
	public MultipleChoiceQuestion(String question, String [] answers) {
		super(question);
		this.possibleAnswers = Arrays.copyOf(answers, answers.length);
	}
	
	/**
	 * Create a copy of question m
	 * @param m question to copy
	 */
	public MultipleChoiceQuestion(MultipleChoiceQuestion m) {
		super(m);
		this.possibleAnswers = m.getPossibleAnswers();
	}
	
	public MultipleChoiceQuestion(byte[] enc) {
		// set the question
		super(Arrays.copyOfRange(enc, 
				1+enc[0], 
				1+enc[0]+new BigInteger(Arrays.copyOfRange(enc, 1, 1+enc[0])).intValue()));
		byte [] rest = Arrays.copyOfRange(enc, 
				1+enc[0]+new BigInteger(Arrays.copyOfRange(enc, 1, 1+enc[0])).intValue(), 
				enc.length);
		
		// set the possible answers
		this.possibleAnswers = new String [rest[0]];
		rest = Arrays.copyOfRange(rest, 1, rest.length);
		
		for (int i = 0; i < possibleAnswers.length; i++) {
			int from = 1 + rest[0];
			int to = from + new BigInteger(Arrays.copyOfRange(rest, 1, 1+rest[0])).intValue();
			try {
				possibleAnswers[i] = new String(Arrays.copyOfRange(rest, from, to),
						Challenge.CHARSET);
			} catch (UnsupportedEncodingException e) {}
			rest = Arrays.copyOfRange(rest, to, rest.length);
		}
		
	}

	/**
	 * Get the possible answers for this question
	 * @return an array containing the possible answers
	 */
	public String [] getPossibleAnswers() {
		return Arrays.copyOf(possibleAnswers, possibleAnswers.length);
	}
	
	@Override
	public byte [] getEncoded(){
		byte [] enc = super.getEncoded();
		// encode the question and the length of the possible answers
		byte [] encLen = BigInteger.valueOf(enc.length).toByteArray();
		enc = ArrayUtils.concatAll(new byte[]{(byte)encLen.length},encLen, enc ,
				new byte[]{(byte)this.possibleAnswers.length});
		// encode the possible answers
		for (String s : this.possibleAnswers) {
			try {
				byte [] strEnc = s.getBytes(Challenge.CHARSET);
				byte [] strEncLen = BigInteger.valueOf(strEnc.length).toByteArray();
				enc = ArrayUtils.concatAll(enc, 
						new byte []{(byte) strEncLen.length}, strEncLen,
						strEnc);
			} catch (UnsupportedEncodingException e) {}
		}
		return enc;
	}
	
	/**
	 * Creates a multiple choice question from its encoded version
	 * @param enc the encoded version of the MCQ
	 * @return the MCQ that was previously encoded to enc
	 */
	public static MultipleChoiceQuestion fromEncoded(byte [] enc) {
		return new MultipleChoiceQuestion(enc);
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
		MultipleChoiceQuestion q = (MultipleChoiceQuestion) obj;
		return super.getQuestion().equals(q.getQuestion()) && 
				Arrays.deepEquals(this.possibleAnswers, q.possibleAnswers);
	}
	
	/**
	 * Get answer as an integer which is the index of the element in possibleAnswers.
	 * @return the index of the answer in possibleAnswers. 
	 * Returns -1 if no answer was selected.
	 */
	public int getIntAnswer() {
		if (super.getAnswer() == null)
			return -1;
		return Integer.parseInt(super.getAnswer());
	}
	
	/**
	 * Set the answer to index answer
	 * @param answer
	 */
	public void setAnswer(int answer) {
		super.setAnswer(""+answer);
	}

	@Override
	public String toString() {
		String ans = (getIntAnswer()==-1)?null:possibleAnswers[getIntAnswer()];
		return "MultipleChoiceQuestion [question=" + super.getQuestion() + 
				", answer=" +  ans
				+ " possibleAnswers="
				+ Arrays.toString(possibleAnswers) + "]";
	}
	
	
}
