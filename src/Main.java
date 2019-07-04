import Crawler.*;
import Crawler.LinkStore.SiteMap;
import Crawler.PageFetcher.SimplePageFetcher;
import Crawler.PageParser.SimplePageParser;
import Crawler.RobotTextParser.SimpleRobotTextParser;
import Crawler.URIQueue.SimpleURIQueue;

public class Main {

    public static void main(String[] args) {
        final String SEED_URL = "https://monzo.com";
        System.out.println("Starting Web Crawler..");
        WebCrawler webCrawler = WebCrawler.Builder.aWebCrawler()
                .withUriQueue(SimpleURIQueue.newInstance())
                .withPageFetcher(SimplePageFetcher.newInstance())
                .withPageParser(SimplePageParser.newInstance())
                .withLinkStore(SiteMap.getInstance())
                .withRobotTextParser(SimpleRobotTextParser.getInstance())
                .build();
        webCrawler.addSeedLink(SEED_URL);
        webCrawler.setPolitenessCrawlDelayInSeconds(0);
        long startTime = System.currentTimeMillis();
        webCrawler.start();
        long endTime = System.currentTimeMillis();
        webCrawler.printCrawledLinks();  //Sitemap
        webCrawler.printFailedLinks();
        System.out.println("Time Taken in seconds: " + (endTime-startTime)/1000);
    }
}
