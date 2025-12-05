package de.papenhagen.chefkochscraper.model;

import java.util.List;

public record Recipe(
    String title,
    List<String> ingredients,
    List<String> instructions,
    int totalTimeSeconds
) {}
