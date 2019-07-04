package Crawler;

import Crawler.LinkStore.LinkStore;
import Crawler.PageFetcher.PageFetchFailedException;
import Crawler.PageFetcher.PageFetcher;
import Crawler.PageParser.PageParser;
import Crawler.RobotTextParser.RobotTextParser;
import Crawler.URIQueue.URIQueue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {

    //To prevent getting into an infinite loop due to a "Spider Trap",
    // and upper limit for number of crawled pages
    public static final int MAX_ALLOWED_ITERATIONS = 100000;

    public static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    private LinkStore linkStore;
    private URIQueue uriQueue;
    private PageFetcher pageFetcher;
    private PageParser pageParser;
    private RobotTextParser robotTextParser;

    private Link seedLink;
    private int politenessCrawlDelayInSeconds = 0;

    public WebCrawler(LinkStore linkStore, URIQueue uriQueue, PageFetcher pageFetcher,
                      PageParser pageParser, RobotTextParser robotTextParser) {
        this.linkStore = linkStore;
        this.uriQueue = uriQueue;
        this.pageFetcher = pageFetcher;
        this.pageParser = pageParser;
        this.robotTextParser = robotTextParser;
    }


    public void addSeedLink(Link seedLink) {
        this.seedLink = seedLink;
        uriQueue.addLink(seedLink);
    }

    public void addSeedLink(String seedURL) {
        try {
            Link seedLink = SimpleLink.newInstance(new URI(seedURL), null);
            addSeedLink(seedLink);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            logger.error("Adding Seed link to URI Queue failed due to URISyntaxException.");
        }
    }

    public void setPolitenessCrawlDelayInSeconds(int politenessCrawlDelayInSeconds) {
        this.politenessCrawlDelayInSeconds = politenessCrawlDelayInSeconds;
    }

    public void start() {
        List<URI> robotDisallowedURIList = getRobotDisallowedURIList();


        for(int i = 0; !uriQueue.isEmpty() && i < MAX_ALLOWED_ITERATIONS; i++){

            Link link = uriQueue.getNextLink();

            if(disallowedURI(link.getURI(), robotDisallowedURIList)){
                continue;
            }

            if(linkStore.uriVisited(link.getURI())){
                linkStore.createParentChildLink(link);
                continue;
            }

            waitBetweenSuccessiveRequests();

            try {
                String htmlPage = pageFetcher.getPage(link.getURI());

                linkStore.addLink(link);

                List<URI> urlList = pageParser.getURLsFromSameDomain(htmlPage, link.getURI());

                URI parentURI = link.getURI();
                for (URI linkURI : urlList) {
                    if(uriShouldBeCrawled(robotDisallowedURIList, linkURI)){
                        uriQueue.addLink(SimpleLink.newInstance(linkURI, parentURI));
                    }
                }

            } catch (PageFetchFailedException e) {
                e.printStackTrace();
                logger.info("PageFetchFailedException. Failed to fetch page " + link.getURI().toString() +
                        " , Parent: " + link.getParentURI().toString());
                linkStore.addURIToFailedList(e.getUri(), e.getHTTPStatusCode());
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("IOException. Failed to fetch page " + link.getURI().toString());
                //TBD: Add back the link to the uriQueue. Add max retry logic.
            } catch (Exception e){
                e.printStackTrace();
                logger.info("Unexpected Exception while processing uri: " + link.getURI().toString());
            }

            if(i == MAX_ALLOWED_ITERATIONS){
                logger.info("Max allowed iterations, {}, reached.",i);
            }

        }

    }

    private void waitBetweenSuccessiveRequests() {
        try {
            if(politenessCrawlDelayInSeconds > 0) {
                TimeUnit.SECONDS.sleep(politenessCrawlDelayInSeconds);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean uriShouldBeCrawled(List<URI> robotDisallowedURIList, URI linkURI) {
        return !disallowedURI(linkURI, robotDisallowedURIList)
                && !linkStore.uriVisited(linkURI)
                && !linkStore.uriInFetchFailedList(linkURI);
    }

    private List<URI> getRobotDisallowedURIList()  {

        try {
            String baseURI = seedLink.getURI().toString();
            URI robotsTextURI = null;
            robotsTextURI = new URI(baseURI + "/robots.txt");

            String robotsText = null;
            robotsText = pageFetcher.getPage(robotsTextURI);

            return robotTextParser.getDisallowedURIList(robotsText, baseURI);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (PageFetchFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  Collections.emptyList();
    }

    public boolean disallowedURI(URI uri, List<URI> robotDisallowedURIList) {
        for (URI disallowedURI : robotDisallowedURIList) {
            if(uri.toString().startsWith(disallowedURI.toString())){
                logger.info("Disallowed uri: " + uri.toString());
                return true;
            }
        }
        return false;
    }

    public void printCrawledLinks(){
        linkStore.printLinks();
    }

    public void printFailedLinks() {
        linkStore.printFailedLinks();
    }




    public static final class Builder {
        private LinkStore linkStore;
        private URIQueue uriQueue;
        private PageFetcher pageFetcher;
        private PageParser pageParser;
        private RobotTextParser robotTextParser;

        private Builder() {
        }

        public static Builder aWebCrawler() {
            return new Builder();
        }

        public Builder withLinkStore(LinkStore linkStore) {
            this.linkStore = linkStore;
            return this;
        }

        public Builder withUriQueue(URIQueue uriQueue) {
            this.uriQueue = uriQueue;
            return this;
        }

        public Builder withPageFetcher(PageFetcher pageFetcher) {
            this.pageFetcher = pageFetcher;
            return this;
        }

        public Builder withPageParser(PageParser pageParser) {
            this.pageParser = pageParser;
            return this;
        }

        public Builder withRobotTextParser(RobotTextParser robotTextParser) {
            this.robotTextParser = robotTextParser;
            return this;
        }

        public WebCrawler build() {
            return new WebCrawler(linkStore, uriQueue, pageFetcher, pageParser, robotTextParser);
        }
    }
}
