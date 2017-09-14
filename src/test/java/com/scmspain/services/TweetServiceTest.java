package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.exceptions.ResourceNotFoundException;
import com.scmspain.repository.TweetRepository;
import com.scmspain.repository.TweetRepositoryRelational;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.actuate.metrics.writer.Delta;

import javax.persistence.EntityManager;

import org.mockito.Mockito;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

public class TweetServiceTest {

    // Sample tweet IDs
    private final static Long NON_DISCARDED_TWEET_ID = 1L;
    private final static Long ALREADY_DISCARDED_TWEET_ID = 2L;
    private final static Long INEXISTANT_TWEET_ID = 3L;

    private TweetRepository tweetRepository;
    private MetricWriter metricWriter;
    private TweetService tweetService;

    @Before
    public void setUp() throws Exception {
        this.tweetRepository = mock(TweetRepository.class);
        this.metricWriter = mock(MetricWriter.class);

        // Return a non-discarded tweet when asked for the tweet with id 1
        Mockito.when(tweetRepository.get(NON_DISCARDED_TWEET_ID))
                .thenReturn(new Tweet(false));

        // Return a discarded tweet when asked for the tweet with id 2
        Mockito.when(tweetRepository.get(ALREADY_DISCARDED_TWEET_ID))
                .thenReturn(new Tweet(true));

        // Return null when asked for the tweet with id 3
        Mockito.when(tweetRepository.get(INEXISTANT_TWEET_ID))
                .thenReturn(null);

        this.tweetService = new TweetService(tweetRepository, metricWriter);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");

        verify(tweetRepository).persist(any(Tweet.class));
        verify(metricWriter).increment(any(Delta.class));
    }

    // Ignore links embedded in the tweet content when validating against length restriction
    @Test
    public void shouldInsertANewTweetIgnoringEmbeddedLinks() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush http://www.vidaextra.com/aventura-plataformas/el-demake-de-the-curse-of-monkey-island-esta-cada-vez-mas-cerca-de-ver-la-luz Threepwood, mighty pirate https://www.vidaextra.com/aventura-plataformas/el-demake-de-the-curse-of-monkey-island-esta-cada-vez-mas-cerca-de-ver-la-luz");

        verify(tweetRepository).persist(any(Tweet.class));
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldDiscardATweet() throws Exception {
        tweetService.discardTweet(NON_DISCARDED_TWEET_ID);

        verify(tweetRepository).persist(any(Tweet.class));
        verify(metricWriter).increment(any(Delta.class));
    }

    // Do nothing if the requested tweet was already discarded
    @Test
    public void discardTweetShouldNotPerformIfAlreadyDiscarded() throws Exception {
        tweetService.discardTweet(ALREADY_DISCARDED_TWEET_ID);

        verify(tweetRepository, never()).persist(any(Tweet.class));
        verify(metricWriter, never()).increment(any(Delta.class));
    }

    @Test
    public void shouldListNonDiscardedTweets() throws Exception {
        tweetService.listAllTweets();

        verify(tweetRepository).getAllNonDiscarded();
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldListDiscardedTweets() throws Exception {
        tweetService.listDiscardedTweets();

        verify(tweetRepository).getAllDiscarded();
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishTweetShouldThrowAnExceptionWhenTweetIsEmpty() throws Exception {
        tweetService.publishTweet("Guybrush Threepwood", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishTweetShouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
        tweetService.publishTweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void publishTweetShouldThrowAnExceptionWhenTweetPublisherNameIsEmpty() throws Exception {
        tweetService.publishTweet("", "I am Guybrush Threepwood, mighty pirate.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void discardTweetShouldThrowAnExceptionWhenTweetIDNotProvided() throws Exception {
        tweetService.discardTweet(null);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void discardTweetShouldThrowAnExceptionWhenTweetNotFound() throws Exception {
        tweetService.discardTweet(INEXISTANT_TWEET_ID);
    }
}
