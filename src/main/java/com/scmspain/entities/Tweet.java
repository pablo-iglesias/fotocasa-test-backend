package com.scmspain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String publisher;
    @Column(nullable = false, columnDefinition="CLOB")
    private String tweet;
    @Column (nullable=true)
    private Long pre2015MigrationStatus = 0L;

    @JsonIgnore // To prevent from changing the contract
    @Column (nullable=false)
    private Boolean discarded;

    public Boolean getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Boolean discarded) {
        this.discarded = discarded;
    }

    public Tweet() {
    }

    // Constructor for testing purposes
    public Tweet(Boolean discarded) {
        this.discarded = discarded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public Long getPre2015MigrationStatus() {
        return pre2015MigrationStatus;
    }

    public void setPre2015MigrationStatus(Long pre2015MigrationStatus) {
        this.pre2015MigrationStatus = pre2015MigrationStatus;
    }

    @Override
    public boolean equals(Object o){

        if(o == null || o.getClass() != this.getClass()){
            return false;
        }

        Tweet tweet = (Tweet)o;

        return tweet.getId().equals(getId())
                && tweet.getPublisher().equals(getPublisher())
                && tweet.getTweet().equals(getTweet())
                && tweet.getDiscarded() == getDiscarded()
                && tweet.getPre2015MigrationStatus().equals(getPre2015MigrationStatus());
    }

}
