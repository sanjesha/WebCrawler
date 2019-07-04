package Crawler.URIQueue;

import Crawler.Link;

public interface URIQueue {

    void addLink(Link link);

    boolean isEmpty();

    Link getNextLink();
}
