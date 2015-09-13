package ch.epfl.lasec.universitycontest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import ch.epfl.lasec.ArrayUtils;

public class ChallengeScore extends Score {
	
	private int [] questionScore; 
	private Challenge cPointerAnswer;

	public ChallengeScore(Challenge c, int score, int questionNumber) {
		super(c.getTitle(), score);
		this.cPointerAnswer = c;
		questionScore = new int [questionNumber];
	}
	
	public ChallengeScore(Challenge c, int [] allScore){
		cPointerAnswer = c;
		questionScore = Arrays.copyOf(allScore,allScore.length);
		int sum = 0;
		for (int i : allScore) {
			sum += i;
		}
		super.setScore(sum);
		super.setScoreOf(c.getTitle());
	}
	
	public ChallengeScore(byte [] enc) {
		
		//--- handle score part ---
		
		int from = 1 + enc[0];
		int to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
		Score tmp = new Score(Arrays.copyOfRange(enc, from, to));
		this.setScore(tmp.getScore());
		this.setScoreOf(tmp.getScoreOf());
		enc = Arrays.copyOfRange(enc, to, enc.length);
		
		
		//--- challenge score part ---
		
		//- decode the pointer to the answer -
		from = 1 + enc[0];
		to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
		this.cPointerAnswer = Challenge.fromEncoded(Arrays.copyOfRange(enc, from, to));
		enc = Arrays.copyOfRange(enc, to, enc.length);
		
		//- decode the array of scores for each question -

		int questionScoreLen = new BigInteger(Arrays.copyOfRange(enc, 1,
				1 + enc[0])).intValue();
		enc = Arrays.copyOfRange(enc, 1 + enc[0], enc.length);
		this.questionScore = new int[questionScoreLen];
		for (int i = 0; i < questionScoreLen; i++) {
			this.questionScore[i] = new BigInteger(Arrays.copyOfRange(enc, 1,
					1 + enc[0])).intValue();
			enc = Arrays.copyOfRange(enc, 1 + enc[0], enc.length);
		}
	}

	public int[] getQuestionScore() {
		return questionScore;
	}

	public Challenge getcPointer() {
		return cPointerAnswer;
	}
	
	public byte[] getEncoded() {
		
		//--- encode the Score (super class) part ---
		
		byte[] superEnc = super.getEncoded();
		byte[] superEncLen = BigInteger.valueOf(superEnc.length).toByteArray();
		byte[] enc = ArrayUtils
				.concatAll(new byte[] { (byte) superEncLen.length },
						superEncLen, superEnc);

		
		//--- encode the ChallengeScore (this) part ---
		
		// - encode the pointer to the answer -
		byte[] cPointerAnswerEnc = cPointerAnswer.getEncoded();
		byte[] cPointerAnswerEncLen = BigInteger.valueOf(
				cPointerAnswerEnc.length).toByteArray();
		enc = ArrayUtils.concatAll(enc,
				new byte[] { (byte) cPointerAnswerEncLen.length },
				cPointerAnswerEncLen, cPointerAnswerEnc);
		
		// - encode the questions score -
		byte[] questionScoreLen = BigInteger.valueOf(questionScore.length)
				.toByteArray();
		enc = ArrayUtils
				.concatAll(enc, new byte[] { (byte) questionScoreLen.length },
						questionScoreLen);

		for (int i = 0; i < this.questionScore.length; i++) {
			byte[] questionScoreEnc = BigInteger.valueOf(this.questionScore[i])
					.toByteArray();
			enc = ArrayUtils.concatAll(enc,
					new byte[] { (byte) questionScoreEnc.length },
					questionScoreEnc);
		}
		return enc;
	}

	@Override
	public String toString() {
		return "ChallengeScore [questionScore="
				+ Arrays.toString(questionScore) + ", cPointerAnswer="
				+ cPointerAnswer + "]";
	}
	
	/**
	 * Write this in a stream
	 * @param os
	 * @throws IOException
	 */
	public void write(OutputStream os) throws IOException{
		
		this.cPointerAnswer.write(os);
		
		DataOutputStream dos = new DataOutputStream(os);
		
		for (int i = 0; i < this.questionScore.length; i++) {
			dos.writeInt(questionScore[i]);
		}
		
	}
	
	/**
	 * Read a challenge score from a stream that was previously written using write method.
	 * 
	 * @param is
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static ChallengeScore read(InputStream is) 
			throws UnsupportedEncodingException, IOException {
		
		Challenge c = Challenge.read(is);
		
		DataInputStream dis = new DataInputStream(is);
		
		int [] qscores = new int[((Quiz) c).questions.size()];
		for (int i = 0; i < qscores.length; i++) {
			qscores[i] = dis.readInt();
		}
		
		return new ChallengeScore(c, qscores);
	}
	
	
	
}
