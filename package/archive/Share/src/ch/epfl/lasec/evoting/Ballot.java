package ch.epfl.lasec.evoting;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;


/**
 * class representing a ballot. A ballot is a list of objects to be voted, it is also represented
 * by a unique identifier.
 * 
 * @author Sebastien Duc
 */
public class Ballot {
	
	/**
	 * The list of objects to be voted
	 */
	//private ArrayList <VotingObject> objects = new ArrayList<VotingObject>();
	private HashMap<String, VotingObject> objects = new HashMap<String, VotingObject>();
	
	/**
	 * The id of the ballot. It is a unique identifier.
	 */
	private Long ballotID;
	
	/**
	 * Construct an empty ballot with ID equal to id.
	 * 
	 * @param id The id of the ballot. It must be a unique identifier.
	 */
	public Ballot(Long id){
		this.ballotID = id;
	}
	
	public Ballot(Long id, VotingObject [] objects){
		this.ballotID = id;
		for (VotingObject votingObject : objects) {
			//this.objects.add(votingObject);
			this.objects.put(votingObject.getSubject(), votingObject);
		}
	}

	public Long getBallotID() {
		return ballotID;
	}

	public void setBallotID(Long ballotID) {
		this.ballotID = ballotID;
	}
	
	
	
	public HashMap<String, VotingObject> getObjects() {
		return new HashMap<String, VotingObject>(this.objects);
	}

	/**
	 * This method adds a voting object in the ballot. The object must be a question
	 * to be answered by yes/no.
	 * 
	 * @param subject The subject of the vote
	 * @param question The question that is submitted on vote
	 */
	public void addVotingQuestion(String subject, String question){
		this.objects.put(subject,new QuestionObject(subject, question));
	}

	
	/**
	 * This method adds an election submitted on vote. The ith candidate has its first
	 * name in the i_th element of candidatesFirstName and its last name in the i_th element of
	 * candidatesLastName. Both String arrays must have the same size.
	 * 
	 * @param subject The subject of election
	 * @param candidatesFirstName The list of candidates first name
	 * @param candidatesLastName The list of candidates last name
	 */
	public void addVotingElection(String subject, 
			String [] candidatesFirstName, String [] candidatesLastName){
		
		//The length of the arrays should be the same.
		assert candidatesFirstName.length == candidatesLastName.length:
			"First name: " + candidatesFirstName.length + ", Last name: " 
				+ candidatesLastName.length;
		
		//Create the candidate list
		Candidate [] candidates =  new Candidate[candidatesFirstName.length];
		for(int candidInd = 0; candidInd < candidatesFirstName.length; candidInd++){
			candidates[candidInd] = new Candidate(candidatesFirstName[candidInd],
					candidatesLastName[candidInd]);
		}
		
		this.objects.put(subject,new ElectionObject(subject, candidates));
	}
	
	public VotingObject getVotingObject(String subject){
		return this.objects.get(subject);
	}
	
	/**
	 * Set the answer of the voting object to answer. See {@link VotingObject} for the 
	 * answer.
	 * @param subject Subject of the voting object we want to set an answer
	 * @param answer Answer we set to the voting object. 
	 */
	public void setAnswerVotingObject(String subject, int answer){
		this.objects.get(subject).setAnswer(answer);
	}
	
	/**
	 * Set the answer of an election object to the candidate with name 
	 * candidateFirstName, candidateLastName.
	 * @param subject
	 * @param candidateFirstName
	 * @param candidateLastName
	 */
	public void setAnswerElection(String subject, String candidateFirstName, 
			String candidateLastName) {
		VotingObject vo = this.objects.get(subject);
		if(vo.getClass() !=  ElectionObject.class){
			throw new ClassCastException();
		}
		((ElectionObject) vo).setAnswer(new Candidate(candidateFirstName, candidateLastName));
	}
	
	/**
	 * Method used to serialize the ballot and save it using a BufferedWriter
	 * @param bw BufferedWriter used to store the ballot
	 * @throws IOException
	 */
	public void serialize(BufferedWriter bw) throws IOException{
		bw.write(Ballot.class.getName());
		bw.newLine();
		bw.write("id");
		bw.newLine();
		bw.write(this.ballotID.toString());
		bw.newLine();
		// serialize the ArrayList
		bw.write("objects");
		bw.newLine();
		bw.write(objects.size());
		bw.newLine();
		for (String key : this.objects.keySet()) {
			bw.write(objects.get(key).getType());
			bw.newLine();
			objects.get(key).serialize(bw);
		}
	}
	
	/**
	 * Method used to deserialize a ballot previously saved using serialize.
	 * @param br BufferedReader used to retrieve the serialized Ballot
	 * @return Return the deserialized ballot retrieved from the BufferReader
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static Ballot deserialize(BufferedReader br) throws IOException, ClassNotFoundException{
		if (!br.readLine().equals(Ballot.class.getName())){
			throw new ClassNotFoundException();
		}
		if (!br.readLine().equals("id")){
			throw new ClassNotFoundException();
		}
		Long id = new Long(br.readLine());
		if (!br.readLine().equals("objects")){
			throw new ClassNotFoundException();
		}
		int nObjects = br.read();
		br.readLine();
		VotingObject [] objects = new VotingObject [nObjects];
		for (int i = 0; i < objects.length; i++) {
			int oType = br.read();
			br.readLine();
			switch (oType) {
			case VotingObject.TYPE_ELECTION:
				objects[i] = ElectionObject.deserialize(br);
				break;
				
			case VotingObject.TYPE_QUESTION:
				objects[i] = QuestionObject.deserialize(br);
				break;
			}
		}
		return new Ballot(id,objects);
	}
}


