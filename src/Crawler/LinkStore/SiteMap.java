package Crawler.LinkStore;

import Crawler.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.*;

public class SiteMap implements LinkStore {
    public static final Logger logger = LoggerFactory.getLogger(SiteMap.class);

    private final Map<URI, LinkNode> visitedURIMap = new HashMap<>();
    private LinkNode root;
    private Map<URI, Integer> uriFetchFailedList = new HashMap<>();

    private static final SiteMap INSTANCE = new SiteMap();

    private SiteMap(){

    }

    public static SiteMap getInstance(){
        return INSTANCE;
    }

    @Override
    public boolean uriVisited(URI uri) {
        return visitedURIMap.containsKey(uri);

    }

    @Override
    public void createParentChildLink(Link link) {
        LinkNode linkNode = visitedURIMap.get(link.getURI());
        LinkNode parentNode = visitedURIMap.get(link.getParentURI());
        parentNode.childrenURI.add(linkNode);
    }

    @Override
    public void addLink(Link link) {
        URI uri = link.getURI();
        LinkNode linkNode = new LinkNode(uri);
        visitedURIMap.put(uri, linkNode);
        if(root == null){
            root = linkNode;
        } else {
            LinkNode parentNode = visitedURIMap.get(link.getParentURI());
            parentNode.childrenURI.add(linkNode);
        }
    }

    @Override
    public void printLinks() {
        String prefixToPrettify = "";
        Set<URI> visited = new HashSet<>();
        traverseDFS(root, prefixToPrettify, visited);
    }

    @Override
    public void addURIToFailedList(URI uri, int httpStatusCode) {
        uriFetchFailedList.put(uri, httpStatusCode);
    }

    @Override
    public boolean uriInFetchFailedList(URI uri) {
        return uriFetchFailedList.containsKey(uri);
    }

    @Override
    public void printFailedLinks() {
        for (URI uri : uriFetchFailedList.keySet()) {
            logger.info(uriFetchFailedList.get(uri) + ": " + uri.toString());
        }
    }

    private void traverseDFS(LinkNode rootNode, String prefixToPrettify, Set<URI> visited) {
        if(!visited.contains(rootNode.uri)) {
            print(rootNode, prefixToPrettify);
            visited.add(rootNode.uri);
            prefixToPrettify = "    " + prefixToPrettify;
            for (LinkNode linkNode : rootNode.childrenURI) {
                traverseDFS(linkNode, prefixToPrettify, visited);
            }
        }

    }

    private void print(LinkNode root, String prefixToPrettify){
        System.out.println(prefixToPrettify + root.uri.toString());
    }

    public void clear(){
        visitedURIMap.clear();
        root = null;
        uriFetchFailedList.clear();
    }

    private class LinkNode {
        URI uri;
        List<LinkNode> childrenURI = new ArrayList<>();

        public LinkNode(URI uri) {
            this.uri = uri;
        }
    }
}
