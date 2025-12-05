# chefkoch‑scraper

A scraper for recipes from chefkoch.de, implemented in Java using Spring Boot 4.

## About the Project

This project provides a tool to scrape recipe data from the German cooking website chefkoch.de. It is built with Java and Spring Boot 4. The goal is to programmatically extract recipe information for further processing, analysis or integration.

## Features

- Scrape recipes from chefkoch.de
- Extract recipe metadata (title, ingredients, instructions, servings)
- Maven / Spring Boot build
- Dockerfile included

## Getting Started

### Prerequisites

- Java 25
- Maven
- Git
- Docker (optional)

### Installation / Build

```shell
git clone https://github.com/jenspapenhagen/chefkoch-scraper.git
cd chefkoch-scraper
./mvnw clean package
```

### Running

```shell
java -jar target/chefkoch-scraper-<version>.jar
```

## Docker

```shell
docker build -t chefkoch-scraper .
docker run --rm chefkoch-scraper
```


### Using

calling the scraper:

```shell
wget http://localhost:8080/api/scrape?url=https://www.chefkoch.de/rezepte/260491101742498/Toast-Hawaii.html
```

the Response:
```json
{
   "title":"Toast Hawaii",
   "ingredients":[
      "8 Scheibe/n Toastbrot",
      "4 TL Margarine",
      "8 Scheibe/n Ananaskleine Scheiben à 35 g",
      "125 g KochschinkenHinterschinken",
      "8 Scheibe/n Schmelzkäse"
   ],
   "instructions":[
      "Die Toastscheiben ...",
      "Ananasscheiben ...",
      "Die Brote in ..."
   ],
   "totalTimeSeconds":600
}
```


## Contributing

1. Fork the repo
2. Create a feature branch
3. Commit changes
4. Open a pull request

## License

See LICENSE file.
