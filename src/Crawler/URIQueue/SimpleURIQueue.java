package Crawler.URIQueue;

import Crawler.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.LinkedList;

public class SimpleURIQueue implements URIQueue {
    public static final Logger logger = LoggerFactory.getLogger(SimpleURIQueue.class);

    private final LinkedList<Link> linkedList = new LinkedList<>();

    private SimpleURIQueue(){
    }

    public static SimpleURIQueue newInstance() {
        return new SimpleURIQueue();
    }

    @Override
    public void addLink(Link link) {
        linkedList.addFirst(link);
    }

    @Override
    public boolean isEmpty() {
        return linkedList.isEmpty();
    }

    @Override
    public Link getNextLink() {
        return  linkedList.removeLast();
    }

}
