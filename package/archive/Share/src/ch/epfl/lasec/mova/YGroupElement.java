package ch.epfl.lasec.mova;


/**
 * 
 * Class representing the elements of the YGroup.
 * In this implementation YGroup is {-1,+1}
 * It is represented by a boolean
 * 
 * @author Sebastien Duc
 *
 */
public class YGroupElement {
	
	/**
	 * boolean value corresponding to 1
	 */
	public final static boolean ONE = true;
	
	/**
	 * boolean value corresponding to -1
	 */
	public final static boolean MINUS_ONE = false;
	
	/**
	 * order of the group
	 */
	public final static int ORDER = 2;
	
	/**
	 * value of the YGroupElement
	 */
	private boolean value;
	
	public YGroupElement(final boolean VALUE){
		this.value = VALUE;
	}
	
	/**
	 * 
	 * Construct a YGroupElement from value which must be 1 or -1. If not then an exception is thrown
	 * 
	 * @param value Value to set to the YGroupElement
	 * @throws IllegalArgumentException Thrown when int value is neither 1 nor -1
	 */
	public YGroupElement(int value) throws IllegalArgumentException{
		if (value == 1 || value == -1)
			this.value = (value == 1)?YGroupElement.ONE:YGroupElement.MINUS_ONE;
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * 
	 * Constructor of copies
	 * 
	 * @param e YGroupElement to copy
	 */
	public YGroupElement(YGroupElement e){
		this.value = e.getValue();
	}
	
	/**
	 * Get the value of this.
	 * @return
	 */
	public boolean getValue(){
		return this.value;
	}
	
	/**
	 * Test if this is 1.
	 * @return true if this is 1 
	 */
	public boolean isOne(){
		return this.value == YGroupElement.ONE;
	}
	
	/**
	 * Test if this is -1
	 * @return true if this is -1
	 */
	public boolean isMinusOne(){
		return this.value == YGroupElement.MINUS_ONE;
	}
	
	/**
	 * Method used to encode a sequence of YGroupElements into a array of bytes<br/>
	 * <br/>
	 * Each element takes only one bit but the number of elements in the sequence is 
	 * not always a multiple of 8, so we have to pad 0. We use 1 byte to encode pad_length<br/>
	 * <dd>........................................................................<br/>
	 * <dd>| pad_length | 0 padding | seqence[length_sequence -1] ... sequence[0] |<br/>
	 * <dd>''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''<br/>
	 * One are represented by a bit set to 1 and minus one are represented by a bit set to 0.
	 * 
	 * @param sequence Sequence of YGroupElement to encode
	 * @return Returns the encoded sequence as an array of bytes
	 */
	public static byte [] encodeYGroupElementSequence(YGroupElement [] sequence){
		//Compute pad_length
		int pLen = 8 - (sequence.length % 8);
		int isPad = (pLen == 0)?0:1;
		// 1 for the pad_length encoding, isPad = 1 when padding, 0 otherwise, and the the sequence
		// grouped into bytes.
		byte [] enc = new byte [1+isPad+(sequence.length/8)];
		// initialize the array to 0 everywhere
		for (int i = 0; i < enc.length; i++) {
			enc[i] = 0;
		}
		// fill the array with the sequence
		for (int i = 0; i < sequence.length; i++) {
			byte tmp = (byte) ((sequence[i].isOne())?1:0);
			tmp = (byte) (tmp << i%8);
			enc[i/8] = (byte) (enc[i/8] | tmp);
		}
		// add as first byte the length of the 0 padding
		enc[enc.length-1] = (byte)pLen; 
		return enc;
	}
	
	/**
	 * Method used to decode an sequence of YGroupElements that was previously encoded using
	 * method encodeYGroupElementSequence.
	 * 
	 * @param encodedSequence The encoded sequence of YGroupElements represented as an array of bytes
	 * @return Returns the sequence of YGroupElements as an array of YGroupElements
	 */
	public static YGroupElement [] decodeEncodedYGroupElementSequence(byte [] encodedSequence){
		int pLen = encodedSequence[encodedSequence.length-1];
		// size of the decoded sequence
		int sequenceSize = 8*(encodedSequence.length - 1 - ((pLen > 0)?1:0)) + ((8-pLen)%8);
		YGroupElement [] sequence = new YGroupElement[sequenceSize];
		for (int i = 0; i < sequence.length; i++) {
			byte mask = (byte) (1 << i%8);
			sequence[i] = (mask == (encodedSequence[i/8] & mask))? 
					YGroupElement.one(): YGroupElement.minusOne();
		}
		return sequence;
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
		YGroupElement g = (YGroupElement) obj;
		return this.value == g.getValue();
	}
	
	/**
	 * Compute this multiplied with e.
	 * g.multiply(e) = g*e
	 * 
	 * @param e 
	 * @return Returns an YGroupElement which is g*e
	 */
	public YGroupElement multiply(YGroupElement e){
		return (this.isOne())? 
				new YGroupElement(e):new YGroupElement(
						(e.isOne())?
								YGroupElement.MINUS_ONE:YGroupElement.ONE);
	}
	
	/**
	 * Compute the inverse of this, 1/g.
	 * @return the inverse of this 1/g
	 */
	public YGroupElement inverse(){
		return new YGroupElement(this);
	}
	
	/**
	 * Compute the power of p of the YGroupElement<br/>
	 * <dd>g.pow(p) = g<sup>p</sup><br/>
	 * In this implementation YGroup = {-1,+1}, so (+1)^p = +1
	 * and (-1)^p = -1 when p is odd and +1 when p is even 
	 * 
	 * @param p 
	 * @return Returns an YGroupElement which is g^p
	 */
	public YGroupElement pow(int p){
		return (p%2 == 0) ? YGroupElement.one(): new YGroupElement(this.value);
	}
	
	/**
	 * Create an YGroupElement which is 1
	 * @return Returns an YGroupElement equal to 1
	 */
	public static YGroupElement one(){
		return new YGroupElement(YGroupElement.ONE);
	}
	
	/**
	 * Create an YGroupElement which is -1
	 * @return Returns an YGroupElement equal to -1
	 */
	public static YGroupElement minusOne(){
		return new YGroupElement(YGroupElement.MINUS_ONE);
	}

	@Override
	public String toString() {
		int v = (this.isOne())? 1:-1;
		return ""+v;
	}
	
	
	
}
