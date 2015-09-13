package ch.epfl.lasec.universitycontest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import ch.epfl.lasec.ArrayUtils;

public class Score {
	
	private String scoreOf;
	private int score;
	public static final String CHARSET = "UTF-8";
	
	public Score(){
		this.score = 0;
		this.scoreOf = null;
	}
	
	public Score(String scoreOf, int score){
		this.scoreOf = scoreOf;
		this.score = score;
	}
	
	public Score(byte [] enc) {
		int from = 1 + enc[0];
		int to = from + new BigInteger(Arrays.copyOfRange(enc, 1, from)).intValue();
		try {
			this.scoreOf = new String(Arrays.copyOfRange(enc, from, to),CHARSET);
		} catch (UnsupportedEncodingException e) {}
		
		enc = Arrays.copyOfRange(enc, to, enc.length);
		this.score = new BigInteger(enc).intValue();
	}

	public String getScoreOf() {
		return scoreOf;
	}

	public void setScoreOf(String scoreOf) {
		this.scoreOf = scoreOf;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Score [" + scoreOf + ": " + score + "]";
	}
	
	public byte[] getEncoded() {
		byte[] enc = null;
		byte[] scoreOfEnc = null;
		try {
			scoreOfEnc = scoreOf.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {}
		byte[] scoreOfLenEnc = BigInteger.valueOf(scoreOfEnc.length)
				.toByteArray();
		enc = ArrayUtils.concatAll(new byte[] { (byte) scoreOfLenEnc.length },
				scoreOfLenEnc, scoreOfEnc);
		
		byte[] scoreEnc = BigInteger.valueOf(this.score).toByteArray();
		enc = ArrayUtils.concatAll(enc, scoreEnc);
		return enc;
	}
	
	public static Score fromEncoded(byte [] enc) {
		return new Score(enc);
	}
	
	
}
