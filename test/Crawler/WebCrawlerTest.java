package Crawler;

import Crawler.LinkStore.SiteMap;
import Crawler.PageFetcher.PageFetchFailedException;
import Crawler.PageFetcher.PageFetcher;
import Crawler.PageParser.SimplePageParser;
import Crawler.RobotTextParser.SimpleRobotTextParser;
import Crawler.URIQueue.SimpleURIQueue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebCrawlerTest {
    static WebCrawler webCrawler;
    static String mockHTML = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>First Heading</h1>\n" +
            "<p>My first paragraph." +
            "<a href=\"https://testuri.com/home/\">Home</a>" +
            "<a href=\"/about\">About</a>" +
            "</p>\n" +
            "<a href=\"https://testurl3/\">Test Link 3</a>" +
            "<a href=\"https://testuri.com/contact\">Contact</a>" +
            "</body>\n" +
            "</html>";

    static String mockHTML1 = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>First Heading</h1>\n" +
            "<p>My first paragraph." +
            "<a href=\"https://testuri.com/home/\">Home</a>" +
            "<a href=\"../about\">About</a>" +

            "</p>\n" +
            "<a href=\"https://testurl3/\">Test Link 3</a>" +
            "<a href=\"https://testuri.com/contact\">Contact</a>" +
            "</body>\n" +
            "</html>";

    static String mockHTML2 = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>First Heading</h1>\n" +
            "<p>My first paragraph." +
            "</p>\n" +
            "<a href=\"https://testurl3/\">Test Link 3</a>" +
            "<a href=\"https://testuri.com/home/\">Contact</a>" +
            "<a href=\"https://testuri.com/blog/\">Contact</a>" +

            "</body>\n" +
            "</html>";

    static String mockHTML3 = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>First Heading</h1>\n" +
            "<p>My first paragraph." +
            "</p>\n" +
            "</body>\n" +
            "</html>";

    static String mockRobotsText = "User-agent: *\n" +
            "Disallow: /docs/\n" +
            "Disallow: /referral/\n" +
            "Disallow: /-staging-referral/\n" +
            "Disallow: /install/";

    @BeforeAll
    static void init() throws URISyntaxException, PageFetchFailedException, IOException {
        SiteMap.getInstance().clear();
        PageFetcher pageFetcher = mock(PageFetcher.class);
        webCrawler = WebCrawler.Builder.aWebCrawler()
                .withUriQueue(SimpleURIQueue.newInstance())
                .withPageFetcher(pageFetcher)
                .withPageParser(SimplePageParser.newInstance())
                .withLinkStore(SiteMap.getInstance())
                .withRobotTextParser(SimpleRobotTextParser.getInstance())
                .build();

        when(pageFetcher.getPage(new URI("https://testuri.com"))).thenReturn(mockHTML);
        when(pageFetcher.getPage(new URI("https://testuri.com/home/"))).thenReturn(mockHTML1);
        when(pageFetcher.getPage(new URI("https://testuri.com/about"))).thenReturn(mockHTML2);
        when(pageFetcher.getPage(new URI("https://testuri.com/contact"))).thenReturn(mockHTML3);
        when(pageFetcher.getPage(new URI("https://testuri.com/robots.txt"))).thenReturn(mockRobotsText);
        URI uri = new URI("https://testuri.com/blog/");
        when(pageFetcher.getPage(uri))
                .thenThrow(new PageFetchFailedException(uri, 404));



    }

    @Test
    void testCrawler() throws URISyntaxException {
        Link seedLink = SimpleLink.newInstance(new URI("https://testuri.com"), null);
        webCrawler.addSeedLink(seedLink);
        webCrawler.start();
        webCrawler.printCrawledLinks();
        webCrawler.printFailedLinks();

    }

    @Test
    void start() {
    }

    @Test
    void printCrawledLinks() {
    }

    @Test
    void disallowedURIShouldReturnTrue() throws URISyntaxException {
        List<URI> disallowedURIList = new ArrayList<>();
        disallowedURIList.add(new URI("https://monzo.com/docs/"));
        disallowedURIList.add(new URI("https://monzo.com/referral/"));
        disallowedURIList.add(new URI("https://monzo.com/-staging-referral/"));
        disallowedURIList.add(new URI("https://monzo.com/install/"));
        assertTrue(webCrawler.disallowedURI(new URI("https://monzo.com/install/"), disallowedURIList));

    }

    @Test
    void disallowedURIShouldReturnFalse() throws URISyntaxException {
        List<URI> disallowedURIList = new ArrayList<>();
        disallowedURIList.add(new URI("https://monzo.com/docs/"));
        disallowedURIList.add(new URI("https://monzo.com/referral/"));
        disallowedURIList.add(new URI("https://monzo.com/-staging-referral/"));
        disallowedURIList.add(new URI("https://monzo.com/install/"));
        assertFalse(webCrawler.disallowedURI(new URI("https://monzo.com/"), disallowedURIList));
    }

}