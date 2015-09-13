package ch.epfl.lasec.evoting;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used for each new vote. It contains useful information such as the ballot,
 * the end date to send back the ballot, the date when it was created, 
 * the date when the results will be given. It is serializable so that 
 * we can send it from the sever to the client and vice-versa.
 * 
 * @author Sebastien Duc
 */
public class Vote implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6173952651384457574L;
	
	private Ballot ballot = null;
	
	private String title;
	
	private final static String dateFormat = "yyyy-MM-dd";
	
	private final DateFormat dfm = new SimpleDateFormat(Vote.dateFormat);
	
	private Date creationDate;
	private Date endDate;
	private Date resultDate;
	
	/**
	 * Constructor for a Vote. The dates are object java.util.Date.
	 * 
	 * @param b The ballot associated to the vote
	 * @param title The title of the vote
	 * @param creation The date when the vote was created
	 * @param end The date which corresponds to the deadline to send back the ballot
	 * @param result The date when the results of the vote will be given
	 */
	public Vote(Ballot b, String title, Date creation, Date end, Date result){
		this.ballot = b;
		this.title = title;
		this.creationDate = creation;
		this.endDate = end;
		this.resultDate = result;
	}
	
	/**
	 * Constructor for Vote. The dates are represented by strings using format yyy-MM-dd
	 * 
	 * @param b The ballot associated to the vote
	 * @param title The title of the vote
	 * @param creationDate the date when the vote was created
	 * @param endDate The date which corresponds to the deadline to send back the ballot
	 * @param resultDate The date when the results of the vote will be given
	 * @throws ParseException Throws this exception when the dates have the wrong format
	 */
	public Vote(Ballot b, String title, String creationDate, 
			String endDate, String resultDate) throws ParseException{
		this.ballot = b;
		this.title = title;
		this.creationDate = dfm.parse(creationDate);
		this.endDate = dfm.parse(endDate);
		this.resultDate = dfm.parse(resultDate);
	}

	public Ballot getBallot() {
		return ballot;
	}

	public String getTitle() {
		return title;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getCreationDateToString(){
		return dfm.format(creationDate);
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public String getEndDateToString(){
		return dfm.format(endDate);
	}

	public Date getResultDate() {
		return resultDate;
	}
	
	public String getResultDateToString(){
		return dfm.format(resultDate);
	}
	
	
	/**
	 * @return Returns the format (String) that is used by the dates
	 */
	public static String getDateFormat(){
		return Vote.dateFormat;
	}
	
}
