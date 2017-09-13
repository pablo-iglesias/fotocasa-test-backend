package com.scmspain.services;

import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TweetServiceTest {
    private EntityManager entityManager;
    private MetricWriter metricWriter;
    private TweetService tweetService;

    @Before
    public void setUp() throws Exception {
        this.entityManager = mock(EntityManager.class);
        this.metricWriter = mock(MetricWriter.class);

        this.tweetService = new TweetService(entityManager, metricWriter);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

        verify(entityManager).persist(any(Tweet.class));
    }

    // Ignore links embedded in the tweet content when validating against length restriction
    @Test
    public void shouldInsertANewTweetIgnoringEmbeddedLinks() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush http://www.vidaextra.com/aventura-plataformas/el-demake-de-the-curse-of-monkey-island-esta-cada-vez-mas-cerca-de-ver-la-luz Threepwood, mighty pirate https://www.vidaextra.com/aventura-plataformas/el-demake-de-the-curse-of-monkey-island-esta-cada-vez-mas-cerca-de-ver-la-luz");

        verify(entityManager).persist(any(Tweet.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetIsEmpty() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
        tweetService.publishTweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetPublisherNameIsEmpty() throws Exception {
        tweetService.publishTweet("", "I am Guybrush Threepwood, mighty pirate.");
    }
}
