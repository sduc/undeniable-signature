package ch.epfl.lasec.universitycontest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ch.epfl.lasec.ArrayUtils;
import ch.epfl.lasec.IOHelper;

/**
 * Class representing a quiz. Quiz is a type of challenge that is used in University Contest
 * application.
 * 
 * @author Sebastien Duc
 *
 */
public class Quiz extends Challenge {

	/**
	 * List of questions that are contained in the quiz
	 */
	ArrayList<QuizQuestion> questions;
	
	/**
	 * Type of quiz_question
	 */
	public final static byte QUIZ_QUESTION_TYPE = 0;
	
	/**
	 * Type of Multiple Choice question 
	 */
	public final static byte MCQ_TYPE = 1;
	
	/**
	 * Create a quiz due to dueTo
	 * @param dueTo
	 * @param title the title of the quiz
	 */
	public Quiz(Date dueTo, String title) {
		super(dueTo,title);
		questions = new ArrayList<QuizQuestion>();
	}
	

	/**
	 * Create a quiz due to dueTo
	 * @param dueTo
	 * @param title the title of the quiz
	 * @throws ParseException
	 */
	public Quiz(String dueTo, String title) throws ParseException {
		super(dueTo, title);
		questions = new ArrayList<QuizQuestion>();
	}
	
	/**
	 * Create a quiz from its encoded version.
	 * @param enc the encoded version of the quiz
	 */
	public Quiz(byte [] enc) {
		this.questions = new ArrayList<QuizQuestion>();
		// ignore the first byte
		enc = Arrays.copyOfRange(enc, 1, enc.length);

		//handle the title
		byte [] encLen = Arrays.copyOfRange(enc, 1, 1 + enc[0]);
		int _from = 1 + enc[0];
		int _to = _from + new BigInteger(encLen).intValue();
		String title = null;
		try {
			title = new String(Arrays.copyOfRange(enc, _from, _to),CHARSET);
		} catch (UnsupportedEncodingException e) {}
		super.setTitle(title);
		enc = Arrays.copyOfRange(enc, _to, enc.length);
		
		//handle the date
		String dueTo = null;
		try {
			dueTo = new String(Arrays.copyOfRange(enc, 1, 1 + enc[0]),CHARSET);
		} catch (UnsupportedEncodingException e) {}
		enc = Arrays.copyOfRange(enc, 1 + enc[0], enc.length);
		super.setDate(dueTo);

		// handle questions
		int size = new BigInteger(Arrays.copyOfRange(enc, 1, 1 + enc[0]))
				.intValue();
		enc = Arrays.copyOfRange(enc, 1 + enc[0], enc.length);
		for (int i = 0; i < size; i++) {
			// handle question depending on question type
			byte type = enc[0];
			enc = Arrays.copyOfRange(enc, 1, enc.length);
			int from = 1 + enc[0];
			int to = from
					+ new BigInteger(Arrays.copyOfRange(enc, 1, 1 + enc[0]))
							.intValue();
			if (type == MCQ_TYPE)
				this.questions.add(new MultipleChoiceQuestion(Arrays
						.copyOfRange(enc, from, to)));
			else
				this.questions.add(new QuizQuestion(Arrays.copyOfRange(enc,
						from, to)));
			enc = Arrays.copyOfRange(enc, to, enc.length);
		}
	}
	
	/**
	 * Add a question in the quiz
	 * @param q question to add
	 */
	public void addQuestion(QuizQuestion q){
		questions.add(new QuizQuestion(q));
	}
	
	/**
	 * Add a MCQ in the quiz
	 * @param q MCQ to add
	 */
	public void addMultipleChoiceQuestion(MultipleChoiceQuestion q) {
		questions.add(q);
	}
	
	/**
	 * Get the encoded version of this.
	 * @return the encoded version of this
	 */
	@Override
	public byte[] getEncoded() {
		byte [] enc = new byte [] {QUIZ_TYPE};
		// encode the title
		byte [] titleEnc = null;
		try {
			titleEnc = super.getTitle().getBytes(CHARSET);
		} catch (UnsupportedEncodingException e1) {}
		byte [] titleEncLen = BigInteger.valueOf(titleEnc.length).toByteArray();
		enc = ArrayUtils.concatAll(enc, new byte []{(byte) titleEncLen.length},
				titleEncLen,titleEnc);
		
		// encode the date due to
		byte[] dueToEnc = null;
		try {
			dueToEnc = super.getDueTo().getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {}
		enc = ArrayUtils.concatAll(enc,new byte []{(byte) dueToEnc.length}, dueToEnc);
		// encode the size of questions
		byte[] questionsEnc = BigInteger.valueOf(questions.size())
				.toByteArray();
		enc = ArrayUtils.concatAll(enc,
				new byte[] { (byte) questionsEnc.length }, questionsEnc);
		// encode the questions
		for (QuizQuestion q : this.questions) {
			if (q.getClass() == QuizQuestion.class)
				enc = ArrayUtils.concatAll(enc, new byte []{QUIZ_QUESTION_TYPE});
			else
				enc = ArrayUtils.concatAll(enc, new byte []{MCQ_TYPE});
			// encode the size of the question followed by the question
			byte[] quEnc = q.getEncoded();
			byte[] quEncLen = BigInteger.valueOf(quEnc.length).toByteArray();
			enc = ArrayUtils.concatAll(enc,
					new byte[] { (byte) quEncLen.length }, quEncLen, quEnc);
		}
		return enc;
	}
	
	/**
	 * Create a quiz from its encoded version.
	 * @param enc the encoded version of the quiz
	 * @return the quiz that was retrieved from enc
	 */
	public static Quiz fromEncoded(byte [] enc) {
		return new Quiz(enc);
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
		Quiz q = (Quiz) obj;
		return super.equals(q) && this.questions.equals(q.questions);
	}
 	
	public QuizQuestion getQuestion(int index){
		return this.questions.get(index);
	}
	
	public int getQuestionIndex(QuizQuestion q) {
		return this.questions.indexOf(q);
	}
	
	public void setQuestionAnswer(int index, String answer) {
		this.questions.get(index).setAnswer(answer);
	}
	
	public void setQuestionAnswer(QuizQuestion q, String answer) {
		int index = this.questions.indexOf(q);
		setQuestionAnswer(index, answer);
	}
	
	public ArrayList<QuizQuestion> getQuestions() {
		return this.questions;
	}


	@Override
	public String toString() {
		return super.toString()+"\n" + 
				"Quiz [questions=" + questions + "]";
	}
	
	/**
	 * Get a new quiz which is a copy of this but without the solutions
	 * @return
	 */
	public Quiz getNoSolution(){
		Quiz q = new Quiz(this.getEncoded());
		for (QuizQuestion qq : q.questions) {
			qq.setAnswer(null);
		}
		return q;
	}
	
	@Override
	public void write(OutputStream out) throws UnsupportedEncodingException, IOException{
		out.write(QUIZ_TYPE);
		
		IOHelper.writeEncodedObject(out, this.getTitle().getBytes(CHARSET));
		IOHelper.writeEncodedObject(out, this.getDueTo().getBytes(CHARSET));
		
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeInt(this.questions.size());
		
		for (QuizQuestion qq : this.questions) {
			if (qq.getClass() == MultipleChoiceQuestion.class) {
				out.write(MCQ_TYPE);
				IOHelper.writeEncodedObject(out, qq.getEncoded());
			}
			else {
				out.write(QUIZ_QUESTION_TYPE);
				qq.write(out);
			}
			
		}
	}
	
	/**
	 * Read a quiz from a stream
	 * @param in stream
	 * @return the quiz that was read from the stream
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static Quiz read(InputStream in) throws UnsupportedEncodingException, IOException{
		String title = new String(IOHelper.readEncodedObject(in),CHARSET);
		String date = new String(IOHelper.readEncodedObject(in),CHARSET);
		
		Quiz q = null;
		try {
			q = new Quiz(date, title);
		} catch (ParseException e) {
			assert false;
		}
		DataInputStream dis = new DataInputStream(in);
		
		int qNumber = dis.readInt();
		for (int i = 0; i < qNumber; i++) {
			int TYPE = in.read();
			if (TYPE == MCQ_TYPE) {
				q.addMultipleChoiceQuestion(new MultipleChoiceQuestion(IOHelper
						.readEncodedObject(in)));
			} else {
				q.addQuestion(QuizQuestion.read(in));
			}
		}
		return q;
	}
	
}