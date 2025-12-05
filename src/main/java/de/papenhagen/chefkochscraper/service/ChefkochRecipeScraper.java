package de.papenhagen.chefkochscraper.service;

import de.papenhagen.chefkochscraper.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Service is scraping the recipe from a given URL.
 */
@Service
public class ChefkochRecipeScraper {

    private static final Logger log = LoggerFactory.getLogger(ChefkochRecipeScraper.class);

    private final String BASE_URL = "https://www.chefkoch.de";

    private final RestClient restClient;

    public ChefkochRecipeScraper(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * scraping the recipe from the given URL.
     *
     * @param url of the recipe
     * @return the recipe
     */
    public Recipe scrapeRecipe(final String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("No URL provided");
        }
        final String checkedURL = url.startsWith("https") ? url : BASE_URL + url;
        log.debug("Scraping recipe from URL={}", url);

        final String html = restClient.get()
                .uri(checkedURL)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("HTTP fetch failed: {}", response.getStatusCode());
                })
                .body(String.class);

        if (html == null) {
            throw new IllegalStateException("No HTML returned from " + url);
        }

        final Document document = Jsoup.parse(html);
        log.debug("HTML parsed successfully");

        final String title = parseTitle(document);
        final List<String> ingredients = parseIngredients(document);
        final List<String> instructions = parseInstructions(document);
        int totalSeconds = parseTime(document);

        return new Recipe(title, ingredients, instructions, totalSeconds);
    }


    /**
     * parsing the Title the recipe
     *
     * @param document the downloaded HTML file
     * @return return the Title of the recipe
     */
    private static String parseTitle(Document document) {
        final Element h1 = document.selectFirst("h1");
        final String title = h1 != null ? h1.text().trim() : "Unknown";
        log.debug("Recipe title extracted: {}", title);

        return title;
    }

    /**
     * parsing the ingredients list of the given recipe.
     *
     * @param document the downloaded HTML file
     * @return the list of ingredients
     */
    private static List<String> parseIngredients(Document document) {
        final List<String> ingredients = new ArrayList<>();
        final Element ingredientsTable = document.selectFirst("table.ds-ingredients-table");

        if (ingredientsTable != null) {
            for (final Element row : ingredientsTable.select("tr")) {
                final Elements cols = row.select("td");
                if (cols.size() == 2) {
                    final String qty = Pattern.compile("\\s+")
                            .matcher(cols.get(0).text().trim())
                            .replaceAll(" ");
                    String name = cols.get(1).text().trim();
                    ingredients.add(qty + " " + name);
                }
            }
        }
        log.debug("Ingredient count: {}", ingredients.size() - 1);
        return ingredients;
    }

    /**
     * parse the instructions list of the given recipe.
     *
     * @param document the downloaded HTML file
     * @return the list of instructions
     */
    private static List<String> parseInstructions(Document document) {
        final List<String> instructions = new ArrayList<>();
        Elements steps = document.select("div.ds-flex.ds-items-start.instruction-row");

        if (!steps.isEmpty()) {
            for (Element step : steps) {
                String text = step.text().trim();
                String cleaned = text.substring(text.indexOf(" ") + 1);
                instructions.add(cleaned);
            }

        }
        log.debug("Instruction steps: {}", instructions.size() - 1);
        log.info("recipe done");
        return instructions;
    }

    /**
     * parsing the estimated time of the given recipe.
     *
     * @param document the downloaded HTML file
     * @return the time ins seconds
     */
    private static int parseTime(Document document) {
        int totalSeconds = 0;
        final Element timeEl = document.selectFirst("div.recipe-meta-property-group__value");

        if (timeEl != null) {
            final String cleaned = timeEl.text()
                    .replace("\uE192", "")
                    .replace("\n", "")
                    .replace("Min.", "")
                    .trim();

            try {
                totalSeconds = Integer.parseInt(cleaned) * 60;
                log.debug("Total time: {} seconds", totalSeconds);
            } catch (NumberFormatException e) {
                log.warn("Cannot parse time from: {}", cleaned);
            }
        }
        return totalSeconds;
    }


}
