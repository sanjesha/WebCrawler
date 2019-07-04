package Crawler.PageFetcher;

import java.io.IOException;
import java.net.URI;

public interface PageFetcher {
    String getPage(URI uri) throws PageFetchFailedException, IOException;
}
