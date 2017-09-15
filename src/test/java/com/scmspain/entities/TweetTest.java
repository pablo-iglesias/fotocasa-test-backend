package com.scmspain.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TweetTest {

    @Test
    public void shouldQualifyAsEqual(){

        Tweet tweetA = new Tweet();
        Tweet tweetB = new Tweet();

        assertThat(tweetA.equals(tweetB)).isTrue();

        tweetA.setId(1L);
        tweetA.setTweet("Uno");
        tweetA.setPublisher("Dos");
        tweetA.setDiscarded(false);

        tweetB.setId(1L);
        tweetB.setTweet("Uno");
        tweetB.setPublisher("Dos");
        tweetB.setDiscarded(false);

        assertThat(tweetA.equals(tweetB)).isTrue();

        tweetA.setPre2015MigrationStatus(2L);
        tweetB.setPre2015MigrationStatus(2L);

        assertThat(tweetA.equals(tweetB)).isTrue();
    }

    @Test
    public void shouldNotQualifyAsEqual(){

        Tweet tweetA = new Tweet(false);
        tweetA.setId(1L);
        tweetA.setTweet("Uno");
        tweetA.setPublisher("Dos");
        tweetA.setDiscarded(false);
        tweetA.setPre2015MigrationStatus(1L);

        assertThat(tweetA).isNotEqualTo(null);
        assertThat(tweetA).isNotEqualTo(new Object());

        Tweet tweetB = new Tweet();
        tweetB.setId(2L);

        assertThat(tweetA).isNotEqualTo(tweetB);

        tweetB.setId(1L);
        tweetB.setTweet("Uno A");

        assertThat(tweetA).isNotEqualTo(tweetB);

        tweetB.setTweet("Uno");
        tweetB.setPublisher("Dos A");

        assertThat(tweetA).isNotEqualTo(tweetB);

        tweetB.setPublisher("Dos");
        tweetB.setDiscarded(true);

        assertThat(tweetA).isNotEqualTo(tweetB);

        tweetB.setDiscarded(false);
        tweetB.setPre2015MigrationStatus(2L);

        assertThat(tweetA).isNotEqualTo(tweetB);

        tweetB.setPre2015MigrationStatus(1L);

        assertThat(tweetA).isEqualTo(tweetB);
    }
}
