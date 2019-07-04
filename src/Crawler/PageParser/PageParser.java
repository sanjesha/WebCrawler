package Crawler.PageParser;

import java.net.URI;
import java.util.List;

public interface PageParser {
    List<URI> getURLsFromSameDomain(String htmlPage, URI uri);
}
