# crawler4j
[![Build Status](https://travis-ci.org/yasserg/crawler4j.svg?branch=master)](https://travis-ci.org/yasserg/crawler4j)
![Maven Central](https://img.shields.io/maven-central/v/edu.uci.ics/crawler4j.svg?style=flat-square)

crawler4j is an open source web crawler for Java which provides a simple interface for
crawling the Web. Using it, you can setup a multi-threaded web crawler in few minutes.

## Installation

### Using Maven

To use the latest release of crawler4j, please use the following snippet in your pom.xml

```xml
    <dependency>
        <groupId>edu.uci.ics</groupId>
        <artifactId>crawler4j</artifactId>
        <version>4.2</version>
    </dependency>
```

### Without Maven

crawler4j JARs are available on the [releases page](https://github.com/yasserg/crawler4j/releases)
and at [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22edu.uci.ics%22%20AND%20a%3A%22crawler4j%22).

If you use crawler4j without Maven, be aware that crawler4j jar file has a couple of
external dependencies. In [releases page](https://github.com/yasserg/crawler4j/releases), you can find a file named crawler4j-X.Y-with-dependencies.jar that includes crawler4j and all of its dependencies as a bundle.
You can add download it and add it to your classpath to get all the dependencies covered.

## Quickstart
You need to create a crawler class that extends WebCrawler. This class decides which URLs
should be crawled and handles the downloaded page. The following is a sample
implementation:
```java
public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp3|zip|gz))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches()
                && href.startsWith("http://www.ics.uci.edu/");
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();

             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             System.out.println("Number of outgoing links: " + links.size());
         }
    }
}
```
As can be seen in the above code, there are two main functions that should be overridden:

- shouldVisit: This function decides whether the given URL should be crawled or not. In
the above example, this example is not allowing .css, .js and media files and only allows
 pages within 'www.ics.uci.edu' domain.
- visit: This function is called after the content of a URL is downloaded successfully.
 You can easily get the url, text, links, html, and unique id of the downloaded page.

You should also implement a controller class which specifies the seeds of the crawl,
the folder in which intermediate crawl data should be stored and the number of concurrent
 threads:

```java
public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://www.ics.uci.edu/~lopes/");
        controller.addSeed("http://www.ics.uci.edu/~welling/");
    	controller.addSeed("http://www.ics.uci.edu/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
```
