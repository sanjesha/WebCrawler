package Crawler.RobotTextParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimpleRobotTextParser implements RobotTextParser {
    public static final Logger logger = LoggerFactory.getLogger(SimpleRobotTextParser.class);

    private static final SimpleRobotTextParser INSTANCE = new SimpleRobotTextParser();

    private SimpleRobotTextParser() {

    }

    public static SimpleRobotTextParser getInstance() {
        return INSTANCE;
    }

    @Override
    public List<URI> getDisallowedURIList(String robotsText, String baseURI) {
        StringTokenizer stringTokenizer = new StringTokenizer(robotsText, ": \n", false);
        String token1 = "";
        String token2 = "";
        while(stringTokenizer.countTokens() > 1 && !(token1.equals("User-agent") && token2.equals("*"))){
            token1 = stringTokenizer.nextToken();
            token2 = stringTokenizer.nextToken();
        }
        List<URI> disallowedURIList = new ArrayList<>();
        while(stringTokenizer.hasMoreTokens()) {
            token1 = stringTokenizer.nextToken();
            if(token1.equals("Disallow") && stringTokenizer.hasMoreTokens()) {
                String disallowedURI = baseURI + stringTokenizer.nextToken();
                try {
                    disallowedURIList.add(new URI(disallowedURI));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        return disallowedURIList;
    }
}
