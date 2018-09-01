package main.Metadata;

public class Publisher {
    private String paper = "";

    private String category = "";
    private String volume = "", no = "";
    private String year = "";

    private String page_start = "", page_end = "";

    private Date received = new Date(), revised = new Date(), accepted = new Date();

    // Default constructor
    public Publisher() {
        
    }
    
    // Getters and Setters
    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPageStart() {
        return page_start;
    }

    public void setPageStart(String page_start) {
        this.page_start = page_start;
    }

    public String getPageEnd() {
        return page_end;
    }

    public void setPageEnd(String page_end) {
        this.page_end = page_end;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public Date getRevised() {
        return revised;
    }

    public void setRevised(Date revised) {
        this.revised = revised;
    }

    public Date getAccepted() {
        return accepted;
    }

    public void setAccepted(Date accepted) {
        this.accepted = accepted;
    }
}
