package ch.epfl.lasec.universitycontest;

import java.io.IOException;

import ch.epfl.lasec.IOHelper;
import ch.epfl.lasec.mova.CommunicationProtocol;
import ch.epfl.lasec.mova.Message;
import ch.epfl.lasec.mova.MovaSignature;
import ch.epfl.lasec.tuple.Tuple;

/**
 * 
 * interface used to handle the state of the server in the protocol 
 * of the university contest application.
 * 
 * @author Sebastien Duc
 *
 */
public interface ServerProtocolState {
	
	/**
	 * Used to handle the protocol state
	 * @throws IOException 
	 */
	abstract public void handle(ServerContext c) throws IOException;

}

/**
 * This is the default/initial state of the server
 * 
 * @author Sebastien Duc
 *
 */
class InitState implements ServerProtocolState {

	@Override
	public void handle(ServerContext c) throws IOException {
		// get the query and handle it
		int rcv = c.getClientService().getIn().read();
		boolean leave = false;
		switch (rcv) {
		case UniversityContestProtocol.ASK_NEW_CHALLENGE:
			
			// retrieve the quiz from the database
			Quiz q = null;
			
			DataBaseHandler dbh = new DataBaseHandler();
			try {
				dbh.connect();
				q = (Quiz) dbh.getCurrentChallenge(c.getClientService().getTeamID());
				dbh.disconnect();
			} catch (NotConnectDBException e1) {
				e1.printStackTrace();
			}
			
			// sign the challenge
			MovaSignature sQ = null;
			
			if (q != null) {
				sQ = c.getClientService().getMovaInstance().sign(
						new Message(q.getEncoded()), 
						c.getClientService().getMovaKeys().getSk());
			}
			
			// send the challenge
			UniversityContestProtocol.handleAskNewChallenge(c.getClientService().getIn(),
					c.getClientService().getOut(),q,sQ);
			break;

		case UniversityContestProtocol.ASK_TEAM_SCORE:
			
			DataBaseHandler dbh4 = new DataBaseHandler();
			Score [] teamScores = {};
			try {
				dbh4.connect();
				teamScores = dbh4.getTeamScores(c.getClientService().getTeamID());
				dbh4.disconnect();
			} catch (NotConnectDBException e1) {
				e1.printStackTrace();
			}
			
			UniversityContestProtocol.handleAskTeamScore(c.getClientService().getIn(),
					c.getClientService().getOut(),teamScores);
			break;
		
		case UniversityContestProtocol.ASK_UNIVERSITY_SCORE:
			
			DataBaseHandler dbh1 = new DataBaseHandler();
			
			Score[] s = null;
			try {
				dbh1.connect();
				s = dbh1.getUniversityScores();
				dbh1.disconnect();
			} catch (NotConnectDBException e) {
				e.printStackTrace();
			}
			
			
			UniversityContestProtocol.handleAskUniversityScore(c.getClientService().getIn(),
					c.getClientService().getOut(),s);
			break;
			
		case UniversityContestProtocol.ASK_CHALLENGE_SOLUTION:
			
			String askedChallengeName = new String(IOHelper.readEncodedObject(c
					.getClientService().getIn()),UniversityContestProtocol.CHARSET);
			
			ServerManager sm = new ServerManager();
			
			ChallengeScore challengeScore = 
					sm.loadChallengeScore(askedChallengeName.hashCode());
			
			UniversityContestProtocol.handleAskChallengeSolution(c
					.getClientService().getOut(), challengeScore);
			break;

		case UniversityContestProtocol.SEND_CHALLENGE:
			// receive the challenge from the client and store in a database
			Tuple challengeSig = UniversityContestProtocol.receiveFilledSignedChallenge(
					c.getClientService().getIn(),
					c.getClientService().getOut());
			
			System.out.println("Message : " + ((Quiz) challengeSig.getNthValue(0)));
			System.out.println("Signature : " + challengeSig.getNthValue(1));
			
			DataBaseHandler dbh3 = new DataBaseHandler();
			try {
				dbh3.connect();
				dbh3.addReceivedChallenge((Quiz) challengeSig.getNthValue(0), 
						c.getClientService().getTeamID());
				dbh3.disconnect();
			} catch (NotConnectDBException e) {
				e.printStackTrace();
			}
			
			break;
			
		case UniversityContestProtocol.STATE_MOVA:
			// go to state mova.
			c.setCurrentState(new MovaState());
			break;
			
		case UniversityContestProtocol.END:
			// end the communiction with the client
			leave = true;
			break;
			
		default:
			System.out.println("Unknown query");
			break;
		}
		
		if (!leave) {
			c.getCurrentState().handle(c);
		}
	}
	
}

/**
 * This is the state of the server when the protocol deals with MOVA stuffs
 * such as verifying a signature, retrieving the public key ,retrieving the mova instance.
 * 
 * @author Sebastien Duc
 *
 */
class MovaState implements ServerProtocolState {

	@Override
	public void handle(ServerContext c) throws IOException {
		System.out.println("- MOVA state -");
		// TODO Auto-generated method stub
		int rcv = c.getClientService().getIn().read();
		switch (rcv) {
		case CommunicationProtocol.ASK_CONFIRMATION:
			System.out.println("- - Asking confirmation - -");
			CommunicationProtocol.acceptConfirmation(c.getClientService()
					.getIn(), c.getClientService().getOut(), c
					.getClientService().getMovaInstance(), c.getClientService()
					.getMovaKeys());
			break;

		case CommunicationProtocol.ASK_DENIAL:
			System.out.println("- - Asking denial - -");
			CommunicationProtocol.acceptDenial(c.getClientService().getIn(), c
					.getClientService().getOut(), c.getClientService()
					.getMovaInstance(), c.getClientService().getMovaKeys());
			break;

		case CommunicationProtocol.ASK_MOVA:
			System.out.println("- - Asking mova instance - -");
			CommunicationProtocol.sendMovaInstance(
					c.getClientService().getIn(),
					c.getClientService().getOut(), c.getClientService()
							.getMovaInstance());
			break;

		case CommunicationProtocol.ASK_PUBLIC_KEY:
			System.out.println("- - Asking mova public key - -");
			CommunicationProtocol.sendPublicKey(c.getClientService().getOut(), 
					c.getClientService().getMovaKeys().getPk());
			break;
			
		case CommunicationProtocol.ASK_TO_SIGN:
			System.out.println("- - Asking to sign message - -");
			CommunicationProtocol.handleAskToSign(c.getClientService().getIn(),
					c.getClientService().getOut(), c.getClientService().getMovaInstance(),
					c.getClientService().getMovaKeys().getSk());
			break;
			
		default:
			System.out.println("Unknown mova query " + rcv);
			break;
		}

		c.setCurrentState(new InitState());
		c.getCurrentState().handle(c);
	}

}
