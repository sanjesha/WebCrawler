package Crawler;

import Crawler.PageFetcher.PageFetchFailedException;
import Crawler.PageFetcher.PageFetcher;
import Crawler.PageFetcher.SimplePageFetcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class SimplePageFetcherTest {
    private static HttpClient httpClient = mock(HttpClient.class);
    private static HttpResponse response = mock(HttpResponse.class);
    private static String mockHTML;

    @BeforeAll
    static void init() throws IOException, InterruptedException {

        when(httpClient.send(any(), any())).thenReturn(response);
        mockHTML = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>First Heading</h1>\n" +
                "<p>My first paragraph." +
                "<a href=\"https://testurl1/\">Test Link 1</a>" +
                "<a href=\"https://testurl2/\">Test Link 2</a>" +
                "</p>\n" +
                "<a href=\"https://testurl3/\">Test Link 3</a>" +
                "</body>\n" +
                "</html>";
        when(response.body()).thenReturn(mockHTML);
        when(response.statusCode()).thenReturn(200);


    }

    @Test
    void getPage() throws URISyntaxException, PageFetchFailedException, IOException {
        PageFetcher pageFetcher = new SimplePageFetcher(httpClient);
        String htmlPage = pageFetcher.getPage(new URI("https://testuri.com"));
        assertEquals(mockHTML, htmlPage);
    }

//    @Test
//    void testRedirection() throws URISyntaxException, PageFetchFailedException {
//        PageFetcher pageFetcher = SimplePageFetcher.newInstance();
//        String htmlPage = pageFetcher.getPage(new URI("http://monzo.com/"));
//        String expectedString = "<html><head></head><body><a href=\"https://monzo.com/\"></a></body></html>";
//        assertEquals(expectedString, htmlPage);
//    }
}