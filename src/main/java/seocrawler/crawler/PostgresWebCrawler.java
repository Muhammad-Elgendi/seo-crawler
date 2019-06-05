package seocrawler.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import seocrawler.SampleLauncher;
import seocrawler.db.PostgresDBService;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class PostgresWebCrawler extends WebCrawler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PostgresWebCrawler.class);

    private static Pattern FILE_ENDING_EXCLUSION_PATTERN = Pattern.compile(".*(\\.(" +
            "css|js" +
            "|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw" +
            "|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g" +
            "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
            "|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx" +
            "|swf" +
            "|zip|rar|gz|bz2|7z|bin" +
            "|xml|txt|java|c|cpp|exe" +
            "))$");


    private final PostgresDBService postgresDBService;

    public PostgresWebCrawler(PostgresDBService postgresDBService) {
        this.postgresDBService = postgresDBService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if(SampleLauncher.exactMatch) {
            return !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches() && href.startsWith(SampleLauncher.matchPattern);
        }else
            return !FILE_ENDING_EXCLUSION_PATTERN.matcher(href).matches() && url.getDomain().equalsIgnoreCase(SampleLauncher.matchPattern);
    }

    /**
     * This function is called if the crawler encounters a page with a 3xx
     * status code
     *
     * @param page Partial page object
     */
    @Override
    public void onRedirectedStatusCode(Page page) {
//        int statusCode = page.getStatusCode();

//        ArrayList<String> status3xx = new ArrayList<>();
//        status3xx.add(0, page.getWebURL().getURL());
//        status3xx.add(1, page.getRedirectedToUrl());
//        status3xx.add(2, String.valueOf(statusCode));
//        problems.put("status3xx", status3xx);

        // decode Url
        String url;

        try {
            url = URLDecoder.decode(page.getWebURL().getURL(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            url = page.getWebURL().getURL();
            logger.error("Decoding url in onRedirectedStatusCode() failed", e);
        }

        // remove trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // store url
        try {
            postgresDBService.storeUrl(url,page.getStatusCode(), SampleLauncher.siteId);
        } catch (RuntimeException e) {
            logger.error("Storing url in onRedirectedStatusCode() failed", e);
        }

        // store redirect
        try {
            postgresDBService.storeRedirect(url,URLDecoder.decode(page.getRedirectedToUrl(), "UTF-8"));
        } catch (RuntimeException e) {
            logger.error("Storing redirect in onRedirectedStatusCode() failed", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Decoding getRedirectedToUrl in onRedirectedStatusCode() failed", e);
        }

//        if (page.getStatusCode() == 302 || page.getStatusCode() == 303 || page.getStatusCode() == 307) {
//            /*
//            Temporary Redirect Problems
//            Using HTTP header refreshes, 302, 303 or 307 redirects will cause search engine crawlers
//            to treat the redirect as temporary and not pass any link juice (ranking power).
//            We highly recommend that you replace temporary redirects with 301 redirects.
//            handlePageStatusCode() and Search for HTTP header refreshes.
//             */
//            ArrayList<String> temporaryRedirect = new ArrayList<>();
//            temporaryRedirect.add(0, page.getWebURL().getURL());
//            temporaryRedirect.add(1, page.getRedirectedToUrl());
//            temporaryRedirect.add(2, String.valueOf(statusCode));
//            problems.put("headerTemporaryRedirect", temporaryRedirect);
//        }

//        // print problems map
//        JSONObject json = new JSONObject(problems);
//        System.out.printf("JSON: %s", json.toString());

    }

    /**
     * This function is called if the crawler encountered an unexpected http
     * status code ( a status code other than 3xx)
     *
     * @param urlStr URL in which an unexpected error was encountered while
     * crawling
     * @param statusCode Html StatusCode
     * @param contentType Type of Content
     * @param description Error Description
     */
    @Override
    public void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType,
                                          String description) {
//        if (statusCode > 399 && statusCode < 500) {
//            // 4xx problems
//            ArrayList<String> status4xx = new ArrayList<>();
//            status4xx.add(0, urlStr);
//            status4xx.add(1, String.valueOf(statusCode));
//            problems.put("status4xx", status4xx);
//        }
//        if (statusCode > 499 && statusCode < 600) {
//            // 5xx problems
//            ArrayList<String> status5xx = new ArrayList<>();
//            status5xx.add(0, urlStr);
//            status5xx.add(1, String.valueOf(statusCode));
//            problems.put("status5xx", status5xx);
//        }
//        // print problems map
//        JSONObject json = new JSONObject(problems);
//        System.out.printf("JSON: %s", json.toString());

        // decode url
        String url ;
        try {
            url = URLDecoder.decode(urlStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            url = urlStr;
            logger.error("Decoding url in onUnexpectedStatusCode() failed", e);
        }

        // remove trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // store url
        try {
            postgresDBService.storeUrl(url,statusCode, SampleLauncher.siteId);
        } catch (RuntimeException e) {
            logger.error("Storing url in onUnexpectedStatusCode() failed", e);
        }

    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Document doc = Jsoup.parse(htmlParseData.getHtml());
//                problems.put("pageUrl", page.getWebURL().getURL());

            // decode url
            String url;
            try {
                url = URLDecoder.decode(page.getWebURL().getURL(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                url = page.getWebURL().getURL();
                logger.error("Decoding url in visit() failed", e);
            }

            // remove trailing slash
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }

            // store url
            try {
                postgresDBService.storeUrl(url,page.getStatusCode(), SampleLauncher.siteId);
            } catch (RuntimeException e) {
                logger.error("Storing url in vist() failed", e);
            }


            Header[] headers = page.getFetchResponseHeaders();
            for (Header header : headers) {
                if (header.getName().equals("X-Robots-Tag")) {
                    if (header.getValue().contains("noindex")) {
                        // X-Robots-Tag: noindex problem
//                            ArrayList<String> xRobotsTagNoindex = new ArrayList<>();
//                            xRobotsTagNoindex.add(0, page.getWebURL().getURL());
//                            xRobotsTagNoindex.add(1, "noindex");
//                            problems.put("xRobotsTagNoindex", xRobotsTagNoindex);

                        // store xrobots
                        try {
                            postgresDBService.storeRobot(url,"xRobots","noindex");
                        } catch (RuntimeException e) {
                            logger.error("Storing xRobots noindex failed", e);
                        }

                    }
                    if (header.getValue().contains("nofollow")) {
                        // X-Robots-Tag: nofollow problem
//                            ArrayList<String> xRobotsTagNofollow = new ArrayList<>();
//                            xRobotsTagNofollow.add(0, page.getWebURL().getURL());
//                            xRobotsTagNofollow.add(1, "nofollow");
//                            problems.put("xRobotsTagNofollow", xRobotsTagNofollow);

                        // store xrobots
                        try {
                            postgresDBService.storeRobot(url,"xRobots","nofollow");
                        } catch (RuntimeException e) {
                            logger.error("Storing xRobots nofollow failed", e);
                        }


                    }
                    if (header.getValue().contains("none")) {
                        // X-Robots-Tag: noindex, nofollow problems
//                            ArrayList<String> xRobotsTagNone = new ArrayList<>();
//                            xRobotsTagNone.add(0, page.getWebURL().getURL());
//                            xRobotsTagNone.add(1, "none");
//                            problems.put("xRobotsTagNone", xRobotsTagNone);

                        // store xrobots
                        try {
                            postgresDBService.storeRobot(url,"xRobots","none");
                        } catch (RuntimeException e) {
                            logger.error("Storing xRobots none failed", e);
                        }


                    }
                }
                if (header.getName().equals("Refresh")) {
                    // HTTP header refreshes Temporary Redirect Problems
                        /*
                           will cause search engine crawlers
                        to treat the redirect as temporary and not pass any link juice (ranking power).
                        We highly recommend that you replace temporary redirects with 301 redirects.
                        handlePageStatusCode() and Search for HTTP header refreshes.
                         */
//                        ArrayList<String> headerRefreshes = new ArrayList<>();
//                        headerRefreshes.add(0, page.getWebURL().getURL());
//                        headerRefreshes.add(1, "Header Refresh");
//                        headerRefreshes.add(2, header.getValue());
//                        problems.put("headerRefreshes", headerRefreshes);

                    // store header refresh
                    try {
                        postgresDBService.storeRefresh(url,"headerRefresh",header.getValue());
                    } catch (RuntimeException e) {
                        logger.error("Storing header refresh failed", e);
                    }


                }
            }


            Elements titleTags = doc.selectFirst("head").select("title");
            if (titleTags.isEmpty()) {
                // Missing Title Problem
//                    ArrayList<String> titles = new ArrayList<>();
//                    titles.add(0, page.getWebURL().getURL());
//                    titles.add(1, "Missing Title");
//                    problems.put("MissingTitle", titles);

                // store title
                try {
                    postgresDBService.storeTitle(url,"");
                } catch (RuntimeException e) {
                    logger.error("Storing empty title failed", e);
                }

            } else {

                if (titleTags.size() > 1) {
                    // Multiple Titles Problem
//                        ArrayList<String> titles = new ArrayList<>();
//                        titles.add(0, page.getWebURL().getURL());
//                        titles.add(1, "Multiple Titles");
//                        problems.put("MultipleTitles", titles);
                    for (Element titleTag : titleTags) {

                        // store title
                        try {
                            postgresDBService.storeTitle(url,titleTag.text());
                        } catch (RuntimeException e) {
                            logger.error("Storing multiple title failed", e);
                        }


                    }
                }else {

                    String title = doc.selectFirst("head").select("title").first().text();


                    // store title
                    try {
                        postgresDBService.storeTitle(url,title);
                    } catch (RuntimeException e) {
                        logger.error("Storing title failed", e);
                    }


//                    int titleLength = title.length();
//                    problems.put("title", title);
//                    if (titleLength > 60) {
//                        // Title too long flag is set
//                        /*
//                        Make a method to add url and problem details e.g. title itself and so on
//                        to the lists that will be sent to database
//                         */
//                        ArrayList<String> titles = new ArrayList<>();
//                        titles.add(0, page.getWebURL().getURL());
//                        titles.add(1, "Title too long");
//                        titles.add(2, String.valueOf(titleLength));
//                        titles.add(3, title);
//                        problems.put("TitleTooLong", titles);
//                    } else if (titleLength < 25) {
//                        // Title too short flag is set
//                        ArrayList<String> titles = new ArrayList<>();
//                        titles.add(0, page.getWebURL().getURL());
//                        titles.add(1, "Title too short");
//                        titles.add(2, String.valueOf(titleLength));
//                        titles.add(3, title);
//                        problems.put("TitleTooShort", titles);
//                    }
                }
            }

            Elements descriptionTags = doc.selectFirst("head").select("meta[name=description]");
            if (descriptionTags.isEmpty()) {
                //Missing Description
//                    ArrayList<String> description = new ArrayList<>();
//                    description.add(0, page.getWebURL().getURL());
//                    description.add(1, "Missing Description");
//                    problems.put("MissingDescription", description);

                // store description
                try {
                    postgresDBService.storeDescription(url,"");
                } catch (RuntimeException e) {
                    logger.error("Storing empty description failed", e);
                }

            } else {

                String description = doc.selectFirst("head").selectFirst("meta[name=description]").attr("content");

                // store description
                try {
                    postgresDBService.storeDescription(url,description);
                } catch (RuntimeException e) {
                    logger.error("Storing description failed", e);
                }


//                    int descriptionLength = description.length();
//                    if (descriptionLength > 300) {
//                        // description too long flag is set
//                        ArrayList<String> descriptionLengthList = new ArrayList<>();
//                        descriptionLengthList.add(0, page.getWebURL().getURL());
//                        descriptionLengthList.add(1, "Description too long");
//                        descriptionLengthList.add(2, String.valueOf(descriptionLength));
//                        descriptionLengthList.add(3, description);
//                        problems.put("DescriptionTooLong", descriptionLengthList);
//                    } else if (descriptionLength < 50) {
//                        // description too short flag is set
//                        ArrayList<String> descriptionLengthList = new ArrayList<>();
//                        descriptionLengthList.add(0, page.getWebURL().getURL());
//                        descriptionLengthList.add(1, "Description too short");
//                        descriptionLengthList.add(2, String.valueOf(descriptionLength));
//                        descriptionLengthList.add(3, description);
//                        problems.put("DescriptionTooShort", descriptionLengthList);
//                    }

            }

//                int urlLength = page.getWebURL().getURL().length();
//                if (urlLength > 75) {
//                    // Url is too long
//                    ArrayList<String> urlList = new ArrayList<>();
//                    urlList.add(0, page.getWebURL().getURL());
//                    urlList.add(1, "URL too long");
//                    urlList.add(2, String.valueOf(urlLength));
//                    problems.put("URLTooLong", urlList);
//                }

            Elements robotsTags = doc.selectFirst("head").select("meta[name=robots]");
            if (!robotsTags.isEmpty()) {
                for (Element tag : robotsTags) {
                    if (tag.attr("content").contains("noindex")) {
                        //Meta noindex problem
//                            ArrayList<String> robotsTagNoindex = new ArrayList<>();
//                            robotsTagNoindex.add(0, page.getWebURL().getURL());
//                            robotsTagNoindex.add(1, "noindex");
//                            problems.put("metaTagNoindex", robotsTagNoindex);

                        // store robot
                        try {
                            postgresDBService.storeRobot(url,"metaTag","noindex");
                        } catch (RuntimeException e) {
                            logger.error("Storing meta tag noindex failed", e);
                        }


                    }
                    if (tag.attr("content").contains("nofollow")) {
                        //Meta Nofollow problem
//                            ArrayList<String> robotsTagNofollow = new ArrayList<>();
//                            robotsTagNofollow.add(0, page.getWebURL().getURL());
//                            robotsTagNofollow.add(1, "nofollow");
//                            problems.put("metaTagNofollow", robotsTagNofollow);

                        // store robot
                        try {
                            postgresDBService.storeRobot(url,"metaTag","nofollow");
                        } catch (RuntimeException e) {
                            logger.error("Storing meta tag nofollow failed", e);
                        }

                    }
                }
            }

            Elements metaRefresh = doc.selectFirst("head").select("meta[http-equiv=refresh]");
            if (!metaRefresh.isEmpty()) {
                // Meta Refresh Problem
//                    ArrayList<String> metaElement = new ArrayList<>();
//                    metaElement.add(0, page.getWebURL().getURL());
//                    metaElement.add(1, "Meta Refresh");
//                    metaElement.add(2, metaRefresh.first().attr("content"));
//                    problems.put("MetaRefresh", metaElement);

                // store refresh
                try {
                    postgresDBService.storeRefresh(url,"MetaRefresh",metaRefresh.first().attr("content"));
                } catch (RuntimeException e) {
                    logger.error("Storing meta refresh failed", e);
                }

            }

            boolean isH1Exit =true;

            Elements H1Tags = doc.selectFirst("body").select("h1");
            if (H1Tags.isEmpty()) {
                // Missing H1 Problem
//                    ArrayList<String> h1 = new ArrayList<>();
//                    h1.add(0, page.getWebURL().getURL());
//                    h1.add(1, "Missing H1");
//                    problems.put("MissingH1", h1);
                isH1Exit =false;
            }

            String bodyText = doc.selectFirst("body").text();
            String contentWithoutSpaces = bodyText.replaceAll("\\s+", "");

            Integer contentLength = contentWithoutSpaces.length();

//                if (contentWithoutSpaces.length() < 300) {
//                    // Thin Content Problem
//                    ArrayList<String> content = new ArrayList<>();
//                    content.add(0, page.getWebURL().getURL());
//                    content.add(1, "Thin Content");
//                    content.add(2, String.valueOf(contentWithoutSpaces.length()));
//                    problems.put("Thin Content", content);
//                }

            String urlQuery = "";

            try {
                URL pageUrl = new URL(url);
                if(pageUrl.getQuery() != null) {
                    urlQuery = pageUrl.getQuery();
                }
            } catch (MalformedURLException ex) {
                logger.error("Parsing URL failed", ex);
            }




//                if (url.getQuery() != null) {
//                    //Dynamic URL problem
//                    ArrayList<String> dynamicUrl = new ArrayList<>();
//                    dynamicUrl.add(0, page.getWebURL().getURL());
//                    dynamicUrl.add(1, "Dynamic URL");
//                    dynamicUrl.add(2, url.getQuery());
//                    problems.put("DynamicURL", dynamicUrl);
//                }

            Elements canonicalTags = doc.selectFirst("head").select("link[rel=canonical]");

            boolean isCanonicalExist = true;

            if (canonicalTags.isEmpty()) {
                // Missing canonical Url Problem
//                    ArrayList<String> canonical = new ArrayList<>();
//                    canonical.add(0, page.getWebURL().getURL());
//                    canonical.add(1, "Missing canonical Url");
//                    problems.put("MissingCanonicalUrl", canonical);
                isCanonicalExist =false;
            }

            // calculate hash of the page
            String hash = Similarities.calculateHash(htmlParseData.getHtml());

            // store content
            try {
                postgresDBService.storeContent(url,isH1Exit,isCanonicalExist,urlQuery,contentLength,hash);
            } catch (RuntimeException e) {
                logger.error("Storing content failed", e);
            }


            // remove old backlinks
            try {
                postgresDBService.removeBacklink(url);
            } catch (RuntimeException e) {
                logger.error("Removing backlinks failed", e);
            }

            // Get Outbound links
            Elements links = doc.select("a[href]");
            // is this link external
            boolean isExternal;

            for (Element link : links) {
                isExternal = !link.attr("abs:href").isEmpty() && !link.attr("abs:href").contains(page.getWebURL().getSubDomain()+page.getWebURL().getDomain());

//                if(SampleLauncher.exactMatch) {
//                    isExternal = !link.attr("abs:href").toLowerCase().startsWith(SampleLauncher.matchPattern);
//                }else {
//                    URL myUrl = null;
//                    try {
//                        myUrl = new URL(link.attr("abs:href").toLowerCase());
//                    } catch (MalformedURLException e) {
//                        logger.error("Wrong backlink format", e);
//                    }
//                    isExternal = myUrl != null ? !myUrl.getHost().equalsIgnoreCase(SampleLauncher.matchPattern) : false;
//                }
                if (isExternal){
                    // decode url
                    String backlink;
                    try {
                        backlink = URLDecoder.decode(link.attr("abs:href").toLowerCase(), "UTF-8");
                        if (backlink.endsWith("/")) {
                            backlink = backlink.substring(0, backlink.length() - 1);
                        }
                    } catch (UnsupportedEncodingException e) {
                        backlink = link.attr("abs:href").toLowerCase();
                        if (backlink.endsWith("/")) {
                            backlink = backlink.substring(0, backlink.length() - 1);
                        }
                        logger.error("Decoding backlink in visit() failed", e);
                    }



                    boolean isDoFollow = !link.attr("rel").contains("nofollow");

                    // store new backlinks
                    try {
                        postgresDBService.storeBacklink(url,backlink,link.html(),isDoFollow);
                    } catch (RuntimeException e) {
                        logger.error("Storing backlinks failed", e);
                    }
                }
            }

            // Slow Load Time Problem
            // Search how to get load time of the page into visit()


//                try {
//                    postgresDBService.store(page);
//                } catch (RuntimeException e) {
//                    logger.error("Storing failed", e);
//                }


//                // print problems map
//                JSONObject json = new JSONObject(problems);
//                System.out.printf("JSON: %s", json.toString());

        }
    }

    /**
     * This function is called just before the termination of the current
     * crawler instance. It can be used for persisting in-memory data or other
     * finalization tasks.
     */
//    public void onBeforeExit() {
//        if (postgresDBService != null) {
//            postgresDBService.close();
//        }
//    }

}
