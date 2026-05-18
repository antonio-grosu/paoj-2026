package com.pao.laboratory12.model;

public class Loan {
    private long id;
    private long bookId;
    private long readerId;
    private String loanDate;
    private String returnDate;

    public Loan() {}

    public Loan(long bookId, long readerId, String loanDate) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.loanDate = loanDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBookId() { return bookId; }
    public void setBookId(long bookId) { this.bookId = bookId; }
    public long getReaderId() { return readerId; }
    public void setReaderId(long readerId) { this.readerId = readerId; }
    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }
    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    @Override
    public String toString() {
        return "Loan{id=" + id + ", bookId=" + bookId + ", readerId=" + readerId
                + ", loanDate='" + loanDate + "', returnDate='" + returnDate + "'}";
    }
}
