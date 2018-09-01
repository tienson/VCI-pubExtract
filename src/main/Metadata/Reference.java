package main.Metadata;

public class Reference {
    private String[] authors = new String[0];
    private String title = "";
    private Publisher publisher = new Publisher();
    private String link = "";
    private String raw = "";

    // Default constructor
    public Reference() {

    }

    // Getters and Setters
    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        if (raw.length() == 0) {
            StringBuilder builder = new StringBuilder("");

            int i = 0;
            while (i < authors.length) {
                builder.append(authors[i]);
                if (++i == authors.length) {
                    builder.append(" ");
                    break;
                } else {
                    builder.append(", ");
                }
            }

            if (publisher.getYear().length() > 0) {
                builder.append("(").append(publisher.getYear()).append("), ");
            }

            builder.append(title).append(", ");
            builder.append(publisher.getVolume()).append(", ");
            builder.append(publisher.getPageStart()).append("-").append(publisher.getPageEnd());

            return builder.toString();
        } else {
            return raw;
        }
    }
}
