package com.scmspain.repository;

import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetRepositoryRelationalTest {

    @Autowired
    private TweetRepository repository;

    @Test
    public void shouldReturnNullWhenGettingInexistentTweet() {

        assertThat(repository.get(Long.MAX_VALUE)).isNull();
    }

    @Test
    public void shouldPersistTweet() {

        Tweet tweet = new Tweet();
        tweet.setPublisher("Yo");
        tweet.setTweet("Text");
        tweet.setDiscarded(Boolean.FALSE);

        repository.persist(tweet);

        assertThat(tweet.getId()).isNotNull();
    }

    @Test
    public void shouldRetrieveTweetById() {

        Tweet tweetA = new Tweet();
        tweetA.setPublisher("Yo");
        tweetA.setTweet("Text");
        tweetA.setDiscarded(Boolean.FALSE);

        repository.persist(tweetA);

        Tweet tweetB = repository.get(tweetA.getId());

        assertThat(tweetA).isEqualTo(tweetB);
    }

    @Test
    public void shouldReturnTweets() {

        Tweet tweet = new Tweet();
        tweet.setPublisher("Yo");
        tweet.setTweet("Text");
        tweet.setDiscarded(Boolean.FALSE);

        repository.persist(tweet);

        List<Tweet> tweets = repository.getAllNonDiscarded();
        assertThat(tweets).isInstanceOf(List.class);
        assertThat(tweets).contains(tweet);
    }

    @Test
    public void shouldReturnDiscardedTweets() {

        Tweet tweet = new Tweet();
        tweet.setPublisher("Yo");
        tweet.setTweet("Text");
        tweet.setDiscarded(Boolean.TRUE);

        repository.persist(tweet);

        List<Tweet> tweets = repository.getAllDiscarded();
        assertThat(tweets).isInstanceOf(List.class);
        assertThat(tweets).contains(tweet);
    }
}
