package beans;

public class ReturnBean extends BookBean{

	String returnDate,issueDate;
	int fine, previousFine;
	boolean fineRecieved;

	public boolean isFineRecieved() {
		return fineRecieved;
	}

	public void setFineRecieved(boolean fineRecieved) {
		this.fineRecieved = fineRecieved;
	}

	public int getPreviousFine() {
		return previousFine;
	}

	public void setPreviousFine(int previousFine) {
		this.previousFine = previousFine;
	}

	public int getFine() {
		return fine;
	}

	public void setFine(int fine) {
		this.fine = fine;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
}
