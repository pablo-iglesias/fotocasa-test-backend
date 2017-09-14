package com.scmspain.repository;

import com.scmspain.entities.Tweet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class TweetRepositoryRelational implements TweetRepository{

    private String entityName;
    private EntityManager entityManager;

    public TweetRepositoryRelational(EntityManager entityManager) {
        entityName = Tweet.class.getName();
        this.entityManager = entityManager;
    }

    /**
     * Persists a tweet in the repository
     *
     * @param tweet - Tweet object
     */
    public void persist(Tweet tweet){
        entityManager.persist(tweet);
    }

    /**
     * Recover tweet from repository
     *
     * @param id - id of the Tweet to retrieve
     * @return retrieved Tweet
     */
    public Tweet get(Long id) {
        return entityManager.find(Tweet.class, id);
    }

    /**
     * Retrieve a list with all the non discarded Tweets
     *
     * @return list of Tweet objects
     */
    public List<Tweet> getAllNonDiscarded() {

        String sql = "SELECT e FROM " + entityName + " e";
        sql += " WHERE pre2015MigrationStatus<>99";
        sql += " AND discarded = FALSE";
        sql += " ORDER BY id DESC";

        TypedQuery<Tweet> query = entityManager.createQuery(sql, Tweet.class);
        return query.getResultList();
    }

    /**
     * Retrieve a list with all the discarded Tweets
     *
     * @return list of Tweet objects
     */
    public List<Tweet> getAllDiscarded() {

        String sql = "SELECT e FROM " + entityName + " e";
        sql += " WHERE pre2015MigrationStatus<>99";
        sql += " AND discarded = TRUE";
        sql += " ORDER BY id DESC";

        TypedQuery<Tweet> query = entityManager.createQuery(sql, Tweet.class);
        return query.getResultList();
    }
}
