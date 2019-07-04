package Crawler;

import Crawler.URIQueue.SimpleURIQueue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

class SimpleURIQueueTest {

    @Test
    void testAddLinkSucceedsWhenQueueIsEmpty() throws URISyntaxException {
        SimpleURIQueue uriQueue = SimpleURIQueue.newInstance();
        Link actualLink = SimpleLink.newInstance(new URI("http://testuri.com"), null);
        uriQueue.addLink(actualLink);
        Link expectedLink = uriQueue.getNextLink();
        assertEquals(expectedLink.getURI(), actualLink.getURI());
    }

    @Test
    void testAddLinkSucceedsWhenQueueIsNotEmpty() throws URISyntaxException {
        SimpleURIQueue uriQueue = SimpleURIQueue.newInstance();
        Link firstLink = SimpleLink.newInstance(new URI("http://firstTesturi.com"), null);
        uriQueue.addLink(firstLink);
        Link secondLink = SimpleLink.newInstance(new URI("http://secondTesturi.com"), null);
        uriQueue.addLink(secondLink);
        Link expectedLink = uriQueue.getNextLink();
        assertEquals(firstLink.getURI(), firstLink.getURI());
    }

    @Test
    void testIsEmpty() throws URISyntaxException {
        SimpleURIQueue uriQueue = SimpleURIQueue.newInstance();
        assertTrue(uriQueue.isEmpty());
        Link actualLink = SimpleLink.newInstance(new URI("http://invaliduri.com"), null);
        uriQueue.addLink(actualLink);
        assertFalse(uriQueue.isEmpty());
        Link expectedLink = uriQueue.getNextLink();
        assertTrue(uriQueue.isEmpty());
    }

    @Test
    void testGetNextLink() throws URISyntaxException {
        SimpleURIQueue uriQueue = SimpleURIQueue.newInstance();
        Link actualLink = SimpleLink.newInstance(new URI("http://invaliduri.com"), null);
        uriQueue.addLink(actualLink);
        Link expectedLink = uriQueue.getNextLink();
        assertEquals(expectedLink.getURI(), actualLink.getURI());
    }
}