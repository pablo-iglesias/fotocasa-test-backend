package com.scmspain.configuration;

import com.scmspain.controller.TweetController;
import com.scmspain.repository.TweetRepository;
import com.scmspain.repository.TweetRepositoryRelational;
import com.scmspain.services.TweetService;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class TweetConfiguration {

    @Bean
    public TweetRepository getTweetRepository(EntityManager entityManager) {
        return new TweetRepositoryRelational(entityManager);
    }

    @Bean
    public TweetService getTweetService(TweetRepository tweetRepository, MetricWriter metricWriter) {
        return new TweetService(tweetRepository, metricWriter);
    }

    @Bean
    public TweetController getTweetConfiguration(TweetService tweetService) {
        return new TweetController(tweetService);
    }
}
