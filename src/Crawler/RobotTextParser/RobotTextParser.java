package Crawler.RobotTextParser;

import java.net.URI;
import java.util.List;

public interface RobotTextParser {

    List<URI> getDisallowedURIList(String robotsText, String baseURI);
}
