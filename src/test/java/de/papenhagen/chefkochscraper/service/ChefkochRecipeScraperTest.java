//package de.papenhagen.chefkochscraper.service;
//
//import de.papenhagen.chefkochscraper.controller.RecipeScraperController;
//import de.papenhagen.chefkochscraper.model.Recipe;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.web.servlet.client.RestTestClient;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class ChefkochRecipeScraperTest {
//
//    @LocalServerPort
//    private int port;
//
//    private ChefkochRecipeScraper scraper;
//
//    private RestTestClient client;
//
//    @BeforeEach
//    public void setup() {
//        client = RestTestClient.bindToServer()
//                .baseUrl("http://localhost:" + port)
//                .build();
//    }
//
//    @Test
//    void testScrapeRecipe_missingTitle() {
//        // Given
//        String html = """
//                <html><body>
//                  <span class="recipe-preptime">10 Min.</span>
//                </body></html>
//                """;
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//
//        // When
//        Recipe recipe = scraper.scrapeRecipe("/x");
//
//        // Then
//        assertEquals("Unknown", recipe.title());
//    }
//
//    @Test
//    void testScrapeRecipe_missingTime_defaultsToZero() {
//        // Given
//        String html = """
//                <html><body>
//                  <h1>Recipe</h1>
//                </body></html>
//                """;
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//
//        // When
//        Recipe recipe = scraper.scrapeRecipe("/x");
//
//        // Then
//        assertEquals(0, recipe.totalTimeSeconds());
//    }
//
//    @Test
//    void testScrapeRecipe_invalidTimeFormat() {
//        String html = """
//                <html><body>
//                  <h1>Recipe</h1>
//                  <span class="recipe-preptime">abc Min.</span>
//                </body></html>
//                """;
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//
//        Recipe recipe = scraper.scrapeRecipe("/x");
//
//        assertEquals(0, recipe.totalTimeSeconds());
//    }
//
//    @Test
//    void testScrapeRecipe_noIngredients() {
//        String html = """
//                <html><body>
//                  <h1>Recipe</h1>
//                  <span class="recipe-preptime">5 Min.</span>
//                </body></html>
//                """;
//
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//
//        Recipe recipe = scraper.scrapeRecipe("/x");
//
//        assertTrue(recipe.ingredients().isEmpty());
//    }
//
//    @Test
//    void testScrapeRecipe_instructionFilterRejectsInvalidDivs() {
//        String html = """
//                <html>
//                  <body>
//                    <h1>R</h1>
//                    <span class="recipe-preptime">5 Min.</span>
//
//                    <!-- valid -->
//                    <div class="ds-box"><br>Mix stuff<br></div>
//
//                    <!-- invalid because of <p> -->
//                    <div class="ds-box"><p>Invalid</p></div>
//                  </body>
//                </html>
//                """;
//
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//
//        Recipe recipe = scraper.scrapeRecipe("/x");
//
//        assertEquals(1, recipe.instructions().size());
//        assertEquals("Mix stuff", recipe.instructions().get(0));
//    }
//
//    @Test
//    void testScrapeRecipe() {
//        String html = """
//                <html>
//                  <body>
//                    <h1>Test Recipe</h1>
//                    <span class="recipe-preptime">15 Min.</span>
//                    <table class="ingredients">
//                      <tr><td>200 g</td><td>Flour</td></tr>
//                      <tr><td>1</td><td>Egg</td></tr>
//                    </table>
//                    <div class="ds-box"><br>Mix ingredients<br></div>
//                  </body>
//                </html>
//                """;
//
//        when(client.get().uri(anyString()).retrieve().body(String.class)).thenReturn(html);
//
//        Recipe recipe = scraper.scrapeRecipe("/test");
//
//        assertEquals("Test Recipe", recipe.title());
//        assertEquals(900, recipe.totalTimeSeconds());
//        assertEquals(2, recipe.ingredients().size());
//        assertEquals(1, recipe.instructions().size());
//        Assertions.assertTrue(recipe.instructions().get(0).contains("Mix ingredients"));
//    }
//}