package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.exceptions.ResourceNotFoundException;

import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// TODO: Refactor comments
// TODO: Create a tweet repository
@Service
@Transactional
public class TweetService {
    private EntityManager entityManager;
    private MetricWriter metricWriter;

    public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
        this.entityManager = entityManager;
        this.metricWriter = metricWriter;
    }

    /**
      Push tweet to repository
      Parameter - publisher - creator of the Tweet
      Parameter - text - Content of the Tweet
      Result - recovered Tweet
    */
    public void publishTweet(String publisher, String text) {

        if (publisher == null || publisher.length() == 0){
            throw new IllegalArgumentException("A Tweet's Publisher name can't be empty");
        }

        if (text == null || text.length() == 0){
            throw new IllegalArgumentException("A Tweet can't be empty");
        }

        String delinked = text.replaceAll("[\\s]*(http[s]*)://[^\\s]+","");
        if(delinked.length() > 140){
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }

        Tweet tweet = new Tweet();
        tweet.setTweet(text);
        tweet.setPublisher(publisher);
        tweet.setDiscarded(Boolean.FALSE);

        this.entityManager.persist(tweet);
        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
    }

    /**
      Set a tweet to discarded status
      Parameter - id - id of the Tweet to discard
      Result - discarded Tweet
     */
    public void discardTweet(Long id){

        if(id == null){
            throw new IllegalArgumentException("To discard a tweet, you must provide an ID");
        }

        Tweet tweet = getTweet(id);

        if(tweet == null){
            throw new ResourceNotFoundException("Specified ID does not correspond to an existing tweet");
        }

        // The API is idempotent about discarding a tweet
        // The response is always 201, persisting the object and incrementing the metric is done once
        if(!tweet.getDiscarded()){
            tweet.setDiscarded(Boolean.TRUE);
            this.entityManager.persist(tweet);
            this.metricWriter.increment(new Delta<Number>("discarded-tweets", 1));
        }
    }

    /**
      Recover tweet from repository
      Parameter - id - id of the Tweet to retrieve
      Result - retrieved Tweet
    */
    public Tweet getTweet(Long id) {
      return this.entityManager.find(Tweet.class, id);
    }

    /**
      Recover tweet from repository
      Result - retrieved Tweet
    */
    public List<Tweet> listAllTweets() {
        List<Tweet> result = new ArrayList<Tweet>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        TypedQuery<Long> query = this.entityManager.createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 AND discarded = FALSE ORDER BY id DESC", Long.class);
        List<Long> ids = query.getResultList();
        for (Long id : ids) {
            result.add(getTweet(id));
        }
        return result;
    }
}
