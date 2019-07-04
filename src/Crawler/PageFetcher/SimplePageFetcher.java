package Crawler.PageFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SimplePageFetcher implements PageFetcher {
    private static final Logger logger = LoggerFactory.getLogger(SimplePageFetcher.class);
    private static final String EMPTY_PAGE = "<html><head></head><body></body></html>";
    private static final int REQUEST_TIMEOUT_IN_SECONDS = 60;

    private HttpClient httpClient;

    public SimplePageFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;

    }

    private SimplePageFetcher() {
        httpClient = HttpClient.newBuilder().build();
    }

    public static SimplePageFetcher newInstance() {
        return new SimplePageFetcher();
    }

    @Override
    public String getPage(URI uri) throws PageFetchFailedException, IOException {
        logger.info("Fetching page : " + uri.toString());

        if(uriIsANonHTMLFile(uri)){
            return EMPTY_PAGE;
        }

        try {
            HttpRequest request = buildHttpRequest(uri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();

            if( statusCode == 200) {
                return response.body();
            } else if(statusCode >= 300 && statusCode < 400) {
                String redirectedLocation = getRedirectedLocation(uri, response);
                //Hack to avoid handling infinite redirects.
                return "<html><head></head><body><a href=\"" + redirectedLocation + "\"></a></body></html>";
            } else {
                logger.info("Status code: " + response.statusCode() + "  " + uri.toString());
                throw new PageFetchFailedException(uri, response.statusCode());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("InterruptedException: " + uri.toString());
            //TBD: Add retry logic
        }

        logger.info("HTML page could not be fetched for uri: " + uri.toString());
        return EMPTY_PAGE;

    }

    private HttpRequest buildHttpRequest(URI uri) {
        HttpRequest request;
        request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_IN_SECONDS))
                .GET()
                .build();
        return request;
    }

    private String getRedirectedLocation(URI uri, HttpResponse<String> response) {
        String redirectedLocation =  response.headers().map().get("Location").get(0);
        logger.info("Status code: " + response.statusCode() + "  " + uri.toString());
        logger.info("Redirected to : " + redirectedLocation);
        if(redirectedLocation.startsWith("/")){
            redirectedLocation = uri.getScheme() + "://" + uri.getAuthority() + redirectedLocation;
            logger.info("Redirected to : " + redirectedLocation);
        }
        return redirectedLocation;
    }

    private boolean uriIsANonHTMLFile(URI uri) {
        return uri.toString().endsWith(".pdf")
                || uri.toString().endsWith(".jpg")
                || uri.toString().endsWith(".png");
    }
}
