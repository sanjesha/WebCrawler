package Crawler;

import java.net.URI;

public class SimpleLink implements Link {
    private final URI uri;
    private final URI parentURI;

    private SimpleLink(URI uri, URI parentURI) {
        this.uri = uri;
        this.parentURI = parentURI;
    }

    public static Link newInstance(URI uri, URI parentURI) {
        return new SimpleLink(uri, parentURI);
    }


    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public URI getParentURI() {
        return parentURI;
    }
}
