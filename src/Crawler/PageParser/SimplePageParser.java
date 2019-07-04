package Crawler.PageParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class SimplePageParser implements PageParser {
    public static final Logger logger = LoggerFactory.getLogger(SimplePageParser.class);

    private SimplePageParser(){

    }

    public static SimplePageParser newInstance() {
        return  new SimplePageParser();
    }

    @Override
    public  List<URI> getURLsFromSameDomain(String htmlPage, URI uri) {
        List<URI> uriList = new ArrayList<>();
        if(htmlPage == null){
            logger.error("Null html page encountered");
            return uriList;
        }
        try {
            String baseUri = uri.toString();

            Document doc = Jsoup.parse(htmlPage, baseUri);
            Elements links = doc.select("a[href]");


            for (Element link : links) {
                String httpLink = link.attr("abs:href");
                if (httpLink.startsWith("#")) {
                    continue;
                }
                if (httpLink.startsWith("//")) {
                    try {
                        httpLink = uri.toURL().getProtocol() + ":" + httpLink;
                    } catch (MalformedURLException e) {
                       // e.printStackTrace();
                        System.out.println("MalformedURLException: " + httpLink);
                        continue;
                    }
                }

                if (httpLink.startsWith("./") || httpLink.startsWith("../")) {
                    httpLink = relativeURLToAbsoluteURL(uri, httpLink);
                }


                try {
                    if(httpLink.contains(uri.getHost())) {
                            URI linkURI = createURI(httpLink);
                            if (linkURI.getHost().equals(uri.getHost())) {
                                //System.out.println(httpLink);
                                uriList.add(linkURI);
                            }
                    }
                } catch (URISyntaxException e) {
                   // e.printStackTrace();
                    System.out.println("URISyntaxException: " + httpLink);

                } catch (MalformedURLException e) {
                    // e.printStackTrace();
                    System.out.println("MalformedURLException: " + httpLink);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception : " + httpLink);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return uriList;
    }

    public static URI createURI(String httpLink) throws MalformedURLException, URISyntaxException {
        URL url = new URL(httpLink);
        URI uri = new URI(url.getProtocol(),url.getAuthority(), url.getPath(), url.getQuery(), null);
        return uri;
    }

    public String relativeURLToAbsoluteURL(URI uri, String relativeUrl) throws MalformedURLException {
        String[] path = uri.getRawPath().split("/");
        String[] rpath = relativeUrl.split("/");
        int pathIndex = path.length - 1;
        int rpathIndex = 0;

        while((rpath[rpathIndex].equals("..") || rpath[rpathIndex].equals("."))){
            if(rpath[rpathIndex].equals(".")){
                rpathIndex++;
            } else {
                rpathIndex++;
                pathIndex--;
            }
        }
        String finalpath = "";
        for(int i=1; i<=pathIndex; i++){
            finalpath += "/" + path[i];
        }

        for(int i=rpathIndex; i<rpath.length; i++){
            finalpath += "/" + rpath[i];
        }
        System.out.println(finalpath);
        return uri.toURL().getProtocol() + "://" + uri.getHost() + finalpath;
    }
}
