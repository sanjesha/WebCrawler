package Crawler;

import Crawler.RobotTextParser.SimpleRobotTextParser;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRobotTextParserTest {

    @Test
    void getDisallowedURIList() throws URISyntaxException {
        SimpleRobotTextParser robotTextParser = SimpleRobotTextParser.getInstance();
        String robotsText = "User-agent: *\n" +
                "Disallow: /docs/\n" +
                "Disallow: /referral/\n" +
                "Disallow: /-staging-referral/\n" +
                "Disallow: /install/";
        List<URI> disallowedURIList = robotTextParser.getDisallowedURIList(robotsText,"https://monzo.com");
        List<URI> expectedList = new ArrayList<>();
        expectedList.add(new URI("https://monzo.com/docs/"));
        expectedList.add(new URI("https://monzo.com/referral/"));
        expectedList.add(new URI("https://monzo.com/-staging-referral/"));
        expectedList.add(new URI("https://monzo.com/install/"));
        assertEquals(expectedList, disallowedURIList);
    }
}