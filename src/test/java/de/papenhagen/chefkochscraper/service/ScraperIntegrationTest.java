package de.papenhagen.chefkochscraper.service;

import de.papenhagen.chefkochscraper.model.Recipe;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@SpringBootTest
class ScraperIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ChefkochRecipeScraper scraper;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setup() {
        // Override the WebClient base URL inside the bean for test environment
        RestClient client = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
    }

    @Test
    void testFullScrapeFlow() {
        String html = """
                <html><body>
                  <h1>Integration Recipe</h1>
                  <span class="recipe-preptime">12 Min.</span>
                  <table class="ingredients">
                    <tr><td>1</td><td>Sugar</td></tr>
                  </table>
                  <div class="ds-box"><br>Stir<br></div>
                </body></html>
                """;

        mockWebServer.enqueue(new MockResponse().setBody(html));

        Recipe recipe = scraper.scrapeRecipe("/abc");

        assertEquals("Integration Recipe", recipe.title());
        assertEquals(720, recipe.totalTimeSeconds());
        assertEquals(1, recipe.ingredients().size());
        assertEquals("1 Sugar", recipe.ingredients().get(0));
        assertEquals(1, recipe.instructions().size());
    }
}