package Crawler;

import Crawler.PageParser.PageParser;
import Crawler.PageParser.SimplePageParser;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimplePageParserTest {

    String mockHTML = "<!DOCTYPE html>\n" +
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
            "<a href=\"https://testuri.com/contact#123\">Contact</a>" +
            "</body>\n" +
            "</html>";

    @Test
    void testGetURLsFromSameDomain() throws MalformedURLException, URISyntaxException {
        URI uri = new URI("https://testuri.com/home/");
        //String htmlPage = PageFetcher.getPage(uri);
        //List<URI> urlList = SimplePageParser.getURLsFromSameDomain(htmlPage, uri);
        PageParser pageParser = SimplePageParser.newInstance();
        List<URI> uriList = pageParser.getURLsFromSameDomain(mockHTML, uri);
        List<URI> expectedURIList = new ArrayList<>();
        expectedURIList.add(new URI("https://testuri.com/home/"));
        expectedURIList.add(new URI("https://testuri.com/about"));
        expectedURIList.add(new URI("https://testuri.com/contact"));

        assertEquals(expectedURIList, uriList);

    }

    @Test
    void testRelativeURLToAbsoluteURL() throws URISyntaxException, MalformedURLException {
        String uriString = "https://testuri.com/home/test/";
        URI uri = new URI(uriString);
        String relativeUrl = "../contact";
        SimplePageParser pageParser = SimplePageParser.newInstance();
        String absoluteURL = pageParser.relativeURLToAbsoluteURL(uri, relativeUrl);
        assertEquals("https://testuri.com/home/contact", absoluteURL);

    }

    @Test
    void testCreateURI() throws MalformedURLException, URISyntaxException {
        String uriString = "https://testuri.com/ home/test#abc";
        URI uri = SimplePageParser.createURI(uriString);
        URI expectedURI = new URI("https://testuri.com/%20home/test");
        assertEquals(expectedURI, uri);

    }
}