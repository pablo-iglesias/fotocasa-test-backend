package com.scmspain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    public TweetControllerTest(){
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        mockMvc.perform(newTweet("Prospect", "Breaking the law"))
            .andExpect(status().is(201));
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        mockMvc.perform(newTweet("Schibsted Spain", "We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, coches.net and milanuncios. Say welcome to Guybrush Threepwood!"))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturn404WhenDiscardingTweetWithInvalidID() throws Exception {
        mockMvc.perform(discardTweet(123L))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldReturn400WhenDiscardingTweetWithoutID() throws Exception {
        mockMvc.perform(discardTweet(null))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {
        mockMvc.perform(newTweet("Yo", "How are you?"))
                .andExpect(status().is(201));

        MvcResult getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(1);
    }

    @Test
    public void shouldDiscardATweet() throws Exception {

        // Create a new tweet
        mockMvc.perform(newTweet("Yo", "Tweet to be discarded"));

        // Retrieve the list of tweets, identify the newly created tweet from the list
        MvcResult getResult = mockMvc.perform(get("/tweet")).andReturn();
        String content = getResult.getResponse().getContentAsString();
        Tweet tweet = parseJsonTweetAtPosition(content, 0);

        // Proceed to discard the tweet
        mockMvc.perform(discardTweet(tweet.getId()))
                .andExpect(status().is(201));

        // Respond indempotently on repeated request
        mockMvc.perform(discardTweet(tweet.getId()))
                .andExpect(status().is(201));

        // Retrieve the list of tweets again
        getResult = mockMvc.perform(get("/tweet"))
                .andExpect(status().is(200))
                .andReturn();

        // Check that the new tweet is not in the list anymore
        content = getResult.getResponse().getContentAsString();
        List<Tweet> tweetList = parseJsonTweetList(content);
        assertThat(tweetList.contains(tweet)).isFalse();
    }

    @Test
    public void shouldReturnAllDiscardedTweets() throws Exception {

        // Create three tweets
        mockMvc.perform(newTweet("Yo", "Tweet to be discarded 1"));
        mockMvc.perform(newTweet("Yo", "Tweet to be discarded 2"));
        mockMvc.perform(newTweet("Yo", "Tweet to be discarded 3"));

        // Retrieve the list of non-discarded tweets
        MvcResult getResult = mockMvc.perform(get("/tweet")).andReturn();
        String content = getResult.getResponse().getContentAsString();
        List<Tweet> liveTweetList = parseJsonTweetList(content);

        // Proceed to discard all the tweets from that list
        for(Tweet tweet : liveTweetList){
            mockMvc.perform(discardTweet(tweet.getId()));
        }

        // Retrieve list of discarded tweets
        getResult = mockMvc.perform(get("/discarded"))
                .andExpect(status().is(200))
                .andReturn();

        content = getResult.getResponse().getContentAsString();
        List<Tweet> discardedTweetList = parseJsonTweetList(content);

        // Assert that all the former tweets are now discarded
        for(Tweet tweet : liveTweetList){
            assertThat(discardedTweetList.contains(tweet)).isTrue();
        }
    }

    private MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        return post("/tweet")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private MockHttpServletRequestBuilder discardTweet(Long id) {
        MockHttpServletRequestBuilder request = post("/discarded")
                .contentType(MediaType.APPLICATION_JSON_UTF8);
        if(id != null){
            request.content(format("{\"tweet\": \"%d\"}", id));
        }
        return request;
    }

    /**
     * Helper method. Takes the json format for a tweet and returns a POJO
     *
     * @param json - Json that represents a tweet
     * @return Tweet
     * @throws Exception
     */
    private Tweet parseJsonTweet(String json) throws Exception{
        return objectMapper.readValue(json, Tweet.class);
    }

    /**
     * Helper method. Takes the json format for an array of tweets and returns a POJO
     *
     * @param json - Json that represents an array of tweets
     * @param pos - Position of the object that will be parsed to POJO
     * @return Tweet
     * @throws Exception
     */
    private Tweet parseJsonTweetAtPosition(String json, int pos) throws Exception{
        return parseJsonTweet(new JSONArray(json).getJSONObject(pos).toString());
    }

    /**
     * Helper method. Takes a JSONArray full of JSONObjects representing tweets and returns a POJO
     *
     * @param jsonArray - JSONArray full of JSONObjects representing tweets
     * @param pos - Position of the JSONObject that will be parsed to POJO
     * @return Tweet
     * @throws Exception
     */
    private Tweet parseJsonTweetAtPosition(JSONArray jsonArray, int pos) throws Exception{
        return parseJsonTweet(jsonArray.getJSONObject(pos).toString());
    }

    /**
     * Helper method. Takes the json format for an array of tweets and returns a list of POJOs
     *
     * @param json - Json that represents an array of tweets
     * @return List<Tweet>
     * @throws Exception
     */
    private List<Tweet> parseJsonTweetList(String json) throws Exception{

        JSONArray array = new JSONArray(json);
        List<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < array.length(); i++){
            tweets.add(parseJsonTweetAtPosition(array, i));
        }

        return tweets;
    }
}
