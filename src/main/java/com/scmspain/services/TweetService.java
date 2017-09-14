package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.exceptions.ResourceNotFoundException;

import com.scmspain.repository.TweetRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TweetService {
    private TweetRepository tweetRepository;
    private MetricWriter metricWriter;

    public TweetService(TweetRepository tweetRepository, MetricWriter metricWriter) {
        this.tweetRepository = tweetRepository;
        this.metricWriter = metricWriter;
    }

    /**
     * Push tweet to repository
     *
     * @param publisher - creator of the Tweet
     * @param text - Content of the Tweet
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

        this.tweetRepository.persist(tweet);
        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
    }

    /**
     * Set a tweet to discarded state
     *
     * @param id - id of the Tweet to discard
     */
    public void discardTweet(Long id){

        if(id == null){
            throw new IllegalArgumentException("To discard a tweet, you must provide an ID");
        }

        Tweet tweet = this.tweetRepository.get(id);

        if(tweet == null){
            throw new ResourceNotFoundException("Specified ID does not correspond to an existing tweet");
        }

        // The API is idempotent about discarding a tweet
        // The response is always 201, the business is done once
        if(!tweet.getDiscarded()){
            tweet.setDiscarded(Boolean.TRUE);
            this.tweetRepository.persist(tweet);
            this.metricWriter.increment(new Delta<Number>("discarded-tweets", 1));
        }
    }

    /**
     * Retrieve a list with all the non discarded Tweets
     *
     * @return list of Tweet objects
     */
    public List<Tweet> listAllTweets() {

        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        List<Tweet> result = this.tweetRepository.getAllNonDiscarded();
        return result;
    }
}
