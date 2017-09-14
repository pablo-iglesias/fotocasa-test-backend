package com.scmspain.entities;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TweetTest {

    @Test
    public void shouldQualifyAsEqual(){

        Tweet tweetA = new Tweet();
        tweetA.setTweet("Uno");
        tweetA.setPublisher("Dos");
        tweetA.setDiscarded(false);

        Tweet tweetB = new Tweet();
        tweetB.setTweet("Uno");
        tweetB.setPublisher("Dos");
        tweetB.setDiscarded(false);

        assertThat(tweetA).isEqualTo(tweetB);
        assertThat(tweetA.equals(tweetB)).isTrue();

        tweetA.setId(1L);
        tweetB.setId(1L);

        tweetA.setDiscarded(true);
        tweetB.setDiscarded(true);

        tweetA.setPre2015MigrationStatus(1L);
        tweetB.setPre2015MigrationStatus(1L);

        assertThat(tweetA).isEqualTo(tweetB);
        assertThat(tweetA.equals(tweetB)).isTrue();
    }

    @Test
    public void shouldNotQualifyAsEqual(){

        Tweet tweetA = new Tweet(false);
        tweetA.setId(1L);
        tweetA.setTweet("Uno");
        tweetA.setPublisher("Dos");

        assertThat(tweetA).isNotEqualTo(null);
        assertThat(tweetA).isNotEqualTo(new Object());

        Tweet tweetB = new Tweet();
        tweetB.setId(2L);
        tweetB.setTweet("Uno");
        tweetB.setPublisher("Dos");

        assertThat(tweetA).isNotEqualTo(tweetB);
        assertThat(tweetA.equals(tweetB)).isFalse();

        tweetA.setId(1L);
        tweetB.setId(1L);

        tweetA.setDiscarded(true);
        tweetB.setDiscarded(false);

        assertThat(tweetA).isNotEqualTo(tweetB);
        assertThat(tweetA.equals(tweetB)).isFalse();

        tweetA.setDiscarded(true);
        tweetB.setDiscarded(true);

        tweetA.setPre2015MigrationStatus(0L);
        tweetB.setPre2015MigrationStatus(1L);

        assertThat(tweetA).isNotEqualTo(tweetB);
        assertThat(tweetA.equals(tweetB)).isFalse();
    }
}
