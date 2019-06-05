package seocrawler.db;

import edu.uci.ics.crawler4j.crawler.Page;

import java.sql.Timestamp;
import java.util.Map;

public interface PostgresDBService {
    void store(Page webPage);
//    void close();

    void storeUrl(String url,Integer status, Integer siteId);
    void storeTitle(String url,String title);
    void storeRedirect(String url,String redirectTo);
    void storeRobot(String url,String type,String content);
    void storeRefresh(String url,String type,String content);
    void storeDescription(String url,String description);
    void storeContent(String url,Boolean isH1Exist,Boolean isCanonicalExist,String urlQuery,Integer contentLength,String contentHash);
    void storeSimilarity(String srcUrl,String destUrl,Float percent);
    void storeBacklink(String srcUrl,String targetUrl,String anchor_text,Boolean is_dofollow);
    void removeBacklink(String url);
    Map<String,String> getHashes(String host);
    void updateJob(String status, Timestamp finishTime, Integer siteId);
    void removeSite(String url);
}
