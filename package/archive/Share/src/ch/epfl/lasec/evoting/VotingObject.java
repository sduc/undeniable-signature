package ch.epfl.lasec.evoting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ch.epfl.lasec.IOHelper;

/**
 * abstract class representing an object submitted to a vote.
 * The object has a subject and a type. The type is either election or question.
 * 
 * @author Sebastien Duc
 */
public abstract class VotingObject {

	/**
	 * Constant value used to set the type of the VotingObject to ElectionObject.
	 * The value of this constant is {@value}
	 */
	public static final int TYPE_ELECTION = 0;
	
	/**
	 * Constant value used to set the type of the VotingObject to QuestionObject.
	 * The value of this constant is {@value}
	 */
	public static final int TYPE_QUESTION = 1;
	
	/**
	 * Subject of the voting object.
	 */
	private String subject;
	
	
	/**
	 * Type of the voting object. It can be {@code TYPE_ELECTION} or {@code TYPE_QUESTION}
	 */
	private int type;
	
	
	/**
	 * The answer that the user has set to the object
	 */
	private int answer;
	
	/**
	 * Constant for the answer. It is used to set the answer to yes
	 */
	public static final int ANSWER_YES = 1;
	
	/**
	 * Constant for the answer. It is used to set the answer to yes
	 */
	public static final int ANSWER_NO = 0;
	
	/**
	 * Constant for the answer. It is used to set the answer to "no answer"
	 */
	public static final int ANSWER_WHITE = -1;

	/**
	 * Constructor for VotingObject. It sets the subject to {@code subject} and the type to
	 * {@code TYPE}.
	 * 
	 * @param subject
	 * @param TYPE
	 */
	public VotingObject(String subject, final int TYPE){
		this.subject = subject;
		this.type = TYPE;
		this.answer = VotingObject.ANSWER_WHITE;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getAnswer() {
		return answer;
	}

	/**
	 * Set the answer to answer.
	 * @param answer
	 */
	public void setAnswer(int answer) {
		this.answer = answer;
	}

	/**
	 * Method used to serialize the question and save it using a BufferedWriter
	 * @param bw BufferedWriter used to store the question
	 * @throws IOException
	 */
	public abstract void serialize(BufferedWriter bw) throws IOException;
	
}

/**
 * class used for a question submitted on vote. It can must be answered by yes/no.
 * It contains a question.
 * 
 * @author Sebastien Duc
 */
class QuestionObject extends VotingObject {
	
	/**
	 * String representing the question asked to the voters
	 */
	private String question;
	
	/**
	 * Constructor for QuestionObject. Set the subject of the object to subject and the
	 * object to be voted to question.
	 * 
	 * @param subject Subject of the object
	 * @param question Question asked to the voters
	 */
	public QuestionObject (String subject, String question){
		super(subject, TYPE_QUESTION);
		this.question = question;
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.write(QuestionObject.class.getName());
		bw.newLine();
		bw.write("answer");
		bw.newLine();
		bw.write((new Integer(super.getAnswer())).toString());
		bw.newLine();
		bw.write("subject");
		bw.newLine();
		bw.write(this.getSubject());
		bw.newLine();
		bw.write("question");
		bw.newLine();
		bw.write(this.question);
		bw.newLine();
	}
	
	/**
	 * Method used to deserialize a QuestionObject.
	 * @param br BufferedReader used to retrieve the QuestionObject
	 * @return Returns the QuestionObject retrieved from BufferedReader
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static QuestionObject deserialize(BufferedReader br) throws IOException, ClassNotFoundException{
		if (!br.readLine().equals(QuestionObject.class.getName())){
			throw new ClassNotFoundException();
		}
		if (!br.readLine().equals("answer")){
			throw new ClassNotFoundException();
		}
		int answer = new Integer(br.readLine()).intValue();
		if (!br.readLine().equals("subject")){
			throw new ClassNotFoundException();
		}
		String subject = br.readLine();
		if (!br.readLine().equals("question")){
			throw new ClassNotFoundException();
		}
		String question = br.readLine();
		QuestionObject qo = new QuestionObject(subject, question);
		qo.setAnswer(answer);
		return qo;
	}

	public String getQuestion() {
		return question;
	}
	
}

/**
 * class used for an election submitted on vote.
 * It contains a list of candidates.
 * 
 * @author Sebastien Duc
 */
class ElectionObject extends VotingObject {
	
	/**
	 * List of candidates that will be voted.
	 */
	private ArrayList <Candidate> candidates = new ArrayList<Candidate>();
	
	/**
	 * Constructor for ElectionObject. Set the subject of election to {@code subject} and the
	 * list of candidates to {@code candidateList}.
	 * @param subject
	 * @param candidateList
	 */
	public ElectionObject(String subject, Candidate [] candidateList){
		super(subject, TYPE_ELECTION);
		for (Candidate candidate : candidateList) {
			this.candidates.add(new Candidate(candidate));
		}
	}
	
	
	/**
	 * Add a candidate in the candidate list.
	 * 
	 * @param c Candidate to add
	 */
	public void addCandidate(Candidate c){
		candidates.add(new Candidate(c));
	}


	public ArrayList<Candidate> getCandidates() {
		return new ArrayList<Candidate>(this.candidates);
	}
	
	/**
	 * Select Candidate c as an answer
	 * @param c Candidate selected
	 */
	public void setAnswer(Candidate c){
		int cIndex = this.candidates.indexOf(c);
		super.setAnswer(cIndex);
	}


	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		bw.write(ElectionObject.class.getName());
		bw.newLine();
		bw.write("answer");
		bw.newLine();
		bw.write((new Integer(super.getAnswer())).toString());
		bw.newLine();
		bw.write("subject");
		bw.newLine();
		bw.write(this.getSubject());
		bw.newLine();
		bw.write("candidates");
		bw.newLine();
		bw.write(this.candidates.size());
		bw.newLine();
		for (int i = 0; i < candidates.size(); i++) {
			candidates.get(i).serialize(bw);
		}
	}
	
	/**
	 * Method used to deserialize an ElectionObject.
	 * @param br BufferedReader used to retrieve the ElectionObject
	 * @return Returns the ElectionObject retrieved from BufferedReader
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ElectionObject deserialize(BufferedReader br) throws IOException, ClassNotFoundException{
		if (!br.readLine().equals(ElectionObject.class.getName())){
			throw new ClassNotFoundException();
		}
		if (!br.readLine().equals("answer")){
			throw new ClassNotFoundException();
		}
		int answer = new Integer(br.readLine()).intValue();
		if (!br.readLine().equals("subject")){
			throw new ClassNotFoundException();
		}
		String subject = br.readLine();
		if (!br.readLine().equals("candidates")){
			throw new ClassNotFoundException();
		}
		int  nCandidates = br.read();
		br.readLine();
		Candidate [] candidates = new Candidate [nCandidates];
		for (int i = 0; i < candidates.length; i++) {
			candidates[i] = Candidate.deserialize(br);
		}
		ElectionObject eo = new ElectionObject(subject, candidates);
		eo.setAnswer(answer);
		return eo;
	}
	
}

/**
 * This class represent a candidate. The candidate is represented by his first name and 
 * his last name.
 * 
 * @author Sebastien Duc
 *
 */
class Candidate {
	
	/**
	 * First name of the candidate
	 */
	private String firstName;
	
	/**
	 * Last name of the candidate 
	 */
	private String lastName;
	
	/**
	 * Constructor for candidate.
	 * 
	 * @param firstname First name that the candidate will have
	 * @param lastname Last name that the candidate will have
	 */
	public Candidate(String firstname,String lastname){
		this.firstName = firstname;
		this.lastName = lastname;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param c Object to clone
	 */
	public Candidate(Candidate c){
		this.firstName = c.getFirstName();
		this.lastName = c.getLastName();
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		Candidate c = (Candidate) obj;
		return firstName.equals(c.firstName) && lastName.equals(c.lastName);
	}

	public void serialize(BufferedWriter bw) throws IOException{
		bw.write(Candidate.class.getName());
		bw.newLine();
		bw.write("firstName");
		bw.newLine();
		bw.write(firstName);
		bw.newLine();
		bw.write("lastName");
		bw.newLine();
		bw.write(lastName);
		bw.newLine();
	}
	
	public static Candidate deserialize(BufferedReader br) throws IOException, ClassNotFoundException{
		if (!br.readLine().equals(Candidate.class.getName())){
			throw new ClassNotFoundException();
		}
		if (!br.readLine().equals("firstName")){
			throw new ClassNotFoundException();
		}
		String firstName = br.readLine();
		if (!br.readLine().equals("lastName")){
			throw new ClassNotFoundException();
		}
		String lastName = br.readLine();
		return new Candidate(firstName,lastName);
	}
	
	public static void main(String[] args) {
		Candidate c1 = new Candidate("Toto","Poulet");
		Candidate c2 = new Candidate("Tutu", "Sandwich");
		ElectionObject eo = new ElectionObject("connerie", new Candidate[]{c1,c2});
		eo.setAnswer(c2);
		
		QuestionObject qo = new QuestionObject("salo", "aimes-tu la vie?");
		QuestionObject qo2 = new QuestionObject("Gogol", "est-tu un polio?");
		
		Ballot b = new Ballot(234561L, new VotingObject [] {eo,qo,qo2});
		
		FileOutputStream  fos = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream("toto.obj");
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			b.serialize(bw);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOHelper.closeQuietly(bw);
			IOHelper.closeQuietly(fos);
		}
		
		FileInputStream fis = null;
		BufferedReader br = null;
		Ballot tutu = null;
		try {
			fis = new FileInputStream("toto.obj");
			br = new BufferedReader(new InputStreamReader(fis));
			tutu = Ballot.deserialize(br);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(tutu.getBallotID());
		System.out.println(((ElectionObject) 
				tutu.getVotingObject("connerie")).getAnswer() + " " +
		((ElectionObject) tutu.getVotingObject("connerie")).getCandidates().get(0).getLastName());
		System.out.println(tutu.getVotingObject("salo").getSubject());
		System.out.println(tutu.getVotingObject("Gogol").getAnswer());
		
	}
	
}
