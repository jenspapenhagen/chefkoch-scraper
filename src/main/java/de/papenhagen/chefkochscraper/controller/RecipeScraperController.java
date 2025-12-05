package de.papenhagen.chefkochscraper.controller;

import de.papenhagen.chefkochscraper.model.Recipe;
import de.papenhagen.chefkochscraper.service.ChefkochRecipeScraper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.giovannicaggianella.toon.annotation.ToonResponse;

@RestController
@RequestMapping("/api")
public class RecipeScraperController {

    private final ChefkochRecipeScraper scraper;

    public RecipeScraperController(ChefkochRecipeScraper scraper) {
        this.scraper = scraper;
    }

    @GetMapping("/scrape")
    @ToonResponse
    public Recipe scrape(@RequestParam(name = "url") String url) {
        return scraper.scrapeRecipe(url);
    }
}
