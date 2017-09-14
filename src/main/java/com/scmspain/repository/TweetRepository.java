package com.scmspain.repository;

import com.scmspain.entities.Tweet;

import java.util.List;

public interface TweetRepository {

    void persist(Tweet tweet);
    Tweet get(Long id);
    List<Tweet> getAllNonDiscarded();
    List<Tweet> getAllDiscarded();
}
