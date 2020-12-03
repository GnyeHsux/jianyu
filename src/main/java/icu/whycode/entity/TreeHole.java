package icu.whycode.entity;

public class TreeHole {

    private String author;

    private String text;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TreeHole{" +
                "author='" + author + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
