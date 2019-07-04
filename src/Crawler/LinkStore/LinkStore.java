package Crawler.LinkStore;

import Crawler.Link;

import java.net.URI;

public interface LinkStore {
    boolean uriVisited(URI uri);

    void createParentChildLink(Link link);

    void addLink(Link link);

    void printLinks();

    void addURIToFailedList(URI uri, int httpStatusCode);

    boolean uriInFetchFailedList(URI uri);

    void printFailedLinks();
}
