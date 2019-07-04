package Crawler.PageFetcher;

import java.net.URI;

public class PageFetchFailedException extends Throwable {

    private final URI uri;
    private final int HTTPStatusCode;

    public PageFetchFailedException(URI uri, int HTTPStatusCode) {
        this.uri = uri;
        this.HTTPStatusCode = HTTPStatusCode;
    }

    public URI getUri() {
        return uri;
    }

    public int getHTTPStatusCode() {
        return HTTPStatusCode;
    }
}
