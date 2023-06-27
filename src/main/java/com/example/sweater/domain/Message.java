package com.example.sweater.domain;

import com.example.sweater.util.MessHelp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Пожалуйста, заполните сообщение")
    private String text;
    private String tag;
    private String nam;
    private String status;
    private String university;
    private String application;
    private String endingGroup;
    private String currentGroup;
    private String trainingLevel;
    private String courseSemester;
    private String profil;
    private String whichDirection;
    private String eachingForm;
    private String trainingFunding;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private String filename;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = { @JoinColumn(name = "message_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id")}
    )
    private Set<User> likes = new HashSet<>();

    public Message(String text, String tag, String nam, String status, String university, String application,
                   String endingGroup, String currentGroup, String trainingLevel, String courseSemester, String profil,
                   String whichDirection, String eachingForm, String trainingFunding, User user) {
        this.nam = nam;
        this.status = status;
        this.university = university;
        this.application = application;
        this.endingGroup = endingGroup;
        this.currentGroup = currentGroup;
        this.trainingLevel = trainingLevel;
        this.courseSemester = courseSemester;
        this.profil = profil;
        this.whichDirection = whichDirection;
        this.eachingForm = eachingForm;
        this.trainingFunding = trainingFunding;
        this.author = user;
        this.text = text;
        this.tag = tag;
    }

    public String getAuthorName() {
        return MessHelp.getAuthorName(author);
    }

    public String getStatus(User user) { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getUniversity() { return university; }

    public void setUniversity(String university) { this.university = university; }

    public String getApplication() { return application; }

    public void setApplication(String application) { this.application = application; }

    public String getEndingGroup() { return endingGroup; }

    public void setEndingGroup(String endingGroup) { this.endingGroup = endingGroup; }

    public String getCurrentGroup() { return currentGroup; }

    public void setCurrentGroup(String currentGroup) { this.currentGroup = currentGroup; }

    public String getTrainingLevel() { return trainingLevel; }

    public void setTrainingLevel(String trainingLevel) { this.trainingLevel = trainingLevel; }

    public String getCourseSemester() { return courseSemester; }

    public void setCourseSemester(String courseSemester) { this.courseSemester = courseSemester; }

    public String getProfil() { return profil; }

    public void setProfil(String profil) { this.profil = profil; }

    public String getWhichDirection() { return whichDirection; }

    public void setWhichDirection(String whichDirection) { this.whichDirection = whichDirection; }

    public String getEachingForm() { return eachingForm; }

    public void setEachingForm(String eachingForm) { this.eachingForm = eachingForm; }

    public String getTrainingFunding() { return trainingFunding; }

    public void setTrainingFunding(String trainingFunding) { this.trainingFunding = trainingFunding; }

    public void setNam(String text) {
        this.nam = nam;
    }

    public String getNam() {
        return nam;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFilename() {
        return filename;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
