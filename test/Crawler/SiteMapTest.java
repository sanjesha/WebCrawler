package Crawler;

import Crawler.LinkStore.SiteMap;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SiteMapTest {

    private static URI seedURI;

    @BeforeAll
    static void init() throws URISyntaxException {
        SiteMap.getInstance().clear();
        seedURI = new URI("http://seeduri.com");
    }



    @Test
    @Order(1)
    void uriVisitedShouldReturnFalse() throws URISyntaxException {
        SiteMap siteMap = SiteMap.getInstance();
        URI uri = new URI("http://testuri1.com");
        assertFalse(siteMap.uriVisited(uri));
    }

    @Test
    @Order(2)
    void testAddLink() throws URISyntaxException {
        SiteMap siteMap = SiteMap.getInstance();
        siteMap.addLink(SimpleLink.newInstance(seedURI, null));
    }

    @Test
    @Order(3)
    void uriVisitedShouldReturnTrue() throws URISyntaxException {
        SiteMap siteMap = SiteMap.getInstance();
        URI uri = seedURI;
        assertTrue(siteMap.uriVisited(uri));
    }


    @Test
    @Order(4)
    void createParentChildLinkShouldSucceedWithoutException() throws URISyntaxException {
        SiteMap siteMap = SiteMap.getInstance();
        URI parentURI = new URI("http://testuriParent.com");
        siteMap.addLink(SimpleLink.newInstance(parentURI, seedURI));
        URI childURI = new URI("http://testuriChild.com");
        siteMap.addLink(SimpleLink.newInstance(childURI, parentURI));
        siteMap.createParentChildLink(SimpleLink.newInstance(parentURI, childURI));
    }



    @Test
    @Order(5)
    void testTraverseDFSShouldSucceedWithoutException() throws URISyntaxException {
        SiteMap siteMap = SiteMap.getInstance();
        URI firstURI = new URI("http://testuri1.com");
        siteMap.addLink(SimpleLink.newInstance(firstURI, seedURI));
        URI secondURI = new URI("http://testuri2.com");
        siteMap.addLink(SimpleLink.newInstance(secondURI, firstURI));
        siteMap.createParentChildLink(SimpleLink.newInstance(firstURI, secondURI));
        URI thirdURI = new URI("http://testuriGrandchild1.com");
        siteMap.addLink(SimpleLink.newInstance(thirdURI,secondURI));
        siteMap.addLink(SimpleLink.newInstance(new URI("http://testuriGrandchild2.com"),secondURI));
        siteMap.addLink(SimpleLink.newInstance(new URI("http://testuriChild2.com"),firstURI));
        siteMap.printLinks();
    }
}