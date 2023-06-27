package com.example.sweater.domain;

import com.example.sweater.util.MessHelp;

public class Mess {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String filename;

    public Mess(Message message, Long likes, Boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.author = message.getAuthor();
        this.filename = message.getFilename();
    }

    public String getAuthorName() {
        return MessHelp.getAuthorName(author);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public User getAuthor() {
        return author;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", author=" + author +
                '}';
    }
}
