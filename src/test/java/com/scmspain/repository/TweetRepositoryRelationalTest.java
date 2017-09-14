package com.scmspain.repository;

import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetRepositoryRelationalTest {

    @Autowired
    private EntityManagerFactory factory;

    private TweetRepositoryRelational repository;

    @Before
    public void setUp() {

        repository = new TweetRepositoryRelational(factory.createEntityManager());
    }

    @Test
    public void shouldReturnNullWhenGettingInexistentTweet() {

        assertThat(repository.get(Long.MAX_VALUE)).isNull();
    }

    @Test
    public void shouldNotThrowAnExceptionOnPersistValidTweet() {

        try {
            repository.persist(new Tweet());
        }catch(Exception e){
            assert(false);
            throw e;
        }
    }

    @Test
    public void shouldReturnListWhenAskingForTweetCollection() {

        List<Tweet> tweets = repository.getAllNonDiscarded();
        assertThat(tweets).isInstanceOf(List.class);

        tweets = repository.getAllDiscarded();
        assertThat(tweets).isInstanceOf(List.class);
    }
}
