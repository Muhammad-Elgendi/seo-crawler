package seocrawler.db.impl;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.slf4j.Logger;
import seocrawler.SampleLauncher;
import seocrawler.db.PostgresDBService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


public class PostgresDBServiceImpl implements PostgresDBService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PostgresDBServiceImpl.class);

    private ComboPooledDataSource comboPooledDataSource;

    private PreparedStatement insertKeyStatement,insertUrlStatement,insertTitleStatement,insertRedirectStatement,
            insertRobotStatement,insertRefreshStatement,insertDescriptionStatement,insertContentStatement,
            insertSimilarityStatement,getHashesStatement,removeSiteStatement,updateJobStatement,
            removeBacklinkStatement,insertBacklinkStatement;

    public PostgresDBServiceImpl(ComboPooledDataSource comboPooledDataSource) throws SQLException {
        this.comboPooledDataSource = comboPooledDataSource;
//        init();
    }

    private void init() throws SQLException {

        ///XXX should be done via DDL script
//        comboPooledDataSource.getConnection().createStatement().executeUpdate(
//                "CREATE SEQUENCE id_master_seq" +
//                        "  INCREMENT 1" +
//                        "  MINVALUE 1 " +
//                        "  MAXVALUE 9223372036854775807" +
//                        "  START 6 " +
//                        "  CACHE 1;")
//        ;
//        comboPooledDataSource.getConnection().createStatement().executeUpdate(
//                "CREATE TABLE webpage" +
//                        " ( " +
//                        "  id bigint NOT NULL," +
//                        "  html TEXT," +
//                        "  text TEXT," +
//                        "  url varchar(4096)," +
//                        "  seen timestamp without time zone NOT NULL," +
//                        "  primary key (id)" +
//                        ")");
    }

    @Override
    public void store(Page page) {

        if (page.getParseData() instanceof HtmlParseData) {
            try {
                insertKeyStatement = comboPooledDataSource.getConnection().prepareStatement("insert into webpage values " +
                        "(nextval('id_master_seq'),?,?,?,?)");

                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

                insertKeyStatement.setString(1, htmlParseData.getHtml());
                insertKeyStatement.setString(2, htmlParseData.getText());
                insertKeyStatement.setString(3, page.getWebURL().getURL());
                insertKeyStatement.setTimestamp(4, SampleLauncher.getCurrentTimeStamp());
                insertKeyStatement.executeUpdate();
            } catch (SQLException e) {
                logger.error("SQL Exception while storing webpage for url'{}'", page.getWebURL().getURL(), e);
                throw new RuntimeException(e);
            }finally {
//                if (insertKeyStatement != null) {
//                    try {
//                        insertKeyStatement.close();
//                    } catch (SQLException e) {
//                        logger.error("SQL Exception while closing insertKeyStatement'{}'", page.getWebURL().getURL(), e);
//                    }
//                }
                try {
                    if (insertKeyStatement.getConnection() != null)
                        insertKeyStatement.getConnection().close();
                } catch (SQLException e) {
                    logger.error("SQL Exception while closing connection insertKeyStatement'{}'",e);
                }
            }

        }
    }

//    @Override
//    public void close() {
//        if (comboPooledDataSource != null) {
//            comboPooledDataSource.close();
//        }
//    }

    @Override
    public void storeUrl(String url,Integer status, Integer siteId) {
        try {
            insertUrlStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into urls values " +
                    "(?,?,?,?)");
            insertUrlStatement.setString(1,url);
            insertUrlStatement.setInt(2,status);
            insertUrlStatement.setInt(3,siteId);
            insertUrlStatement.setTimestamp(4, SampleLauncher.getCurrentTimeStamp());
            insertUrlStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing url", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertUrlStatement != null) {
//                try {
//                    insertUrlStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertUrlStatement'{}'", e);
//                }
//            }
            try {
                if (insertUrlStatement.getConnection() != null)
                    insertUrlStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertUrlStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeTitle(String url,String title) {
        try {
            insertTitleStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into titles values " +
                    "(?,?,?)");
            insertTitleStatement.setString(1,url);
            insertTitleStatement.setString(2,title);
            insertTitleStatement.setTimestamp(3,SampleLauncher.getCurrentTimeStamp());
            insertTitleStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing title", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertTitleStatement != null) {
//                try {
//                    insertTitleStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertTitleStatement'{}'", e);
//                }
//            }
            try {
                if (insertTitleStatement.getConnection() != null)
                    insertTitleStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertTitleStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeRedirect(String url,String redirectTo) {
        try {

            insertRedirectStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into redirects values " +
                    "(?,?,?)");
            insertRedirectStatement.setString(1,url);
            insertRedirectStatement.setString(2,redirectTo);
            insertRedirectStatement.setTimestamp(3,SampleLauncher.getCurrentTimeStamp());
            insertRedirectStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing redirect", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertRedirectStatement != null) {
//                try {
//                    insertRedirectStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertRedirectStatement'{}'", e);
//                }
//            }
            try {
                if (insertRedirectStatement.getConnection() != null)
                    insertRedirectStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertRedirectStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeRobot(String url,String type,String content) {
        try {
            insertRobotStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into robots values " +
                    "(?,?,?,?)");
            insertRobotStatement.setString(1,url);
            insertRobotStatement.setString(2,type);
            insertRobotStatement.setString(3,content);
            insertRobotStatement.setTimestamp(4,SampleLauncher.getCurrentTimeStamp());
            insertRobotStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing robot", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertRobotStatement != null) {
//                try {
//                    insertRobotStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertRobotStatement'{}'", e);
//                }
//            }
            try {
                if (insertRobotStatement.getConnection() != null)
                    insertRobotStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertRobotStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeRefresh(String url,String type,String content) {
        try {
            insertRefreshStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into refreshes values " +
                    "(?,?,?,?)");
            insertRefreshStatement.setString(1,url);
            insertRefreshStatement.setString(2,type);
            insertRefreshStatement.setString(3,content);
            insertRefreshStatement.setTimestamp(4,SampleLauncher.getCurrentTimeStamp());
            insertRefreshStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing refresh", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertRefreshStatement != null) {
//                try {
//                    insertRefreshStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertRefreshStatement'{}'", e);
//                }
//            }
            try {
                if (insertRefreshStatement.getConnection() != null)
                    insertRefreshStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertRefreshStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeDescription(String url,String description) {
        try {
            insertDescriptionStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into descriptions values " +
                    "(?,?,?)");
            insertDescriptionStatement.setString(1,url);
            insertDescriptionStatement.setString(2,description);
            insertDescriptionStatement.setTimestamp(3,SampleLauncher.getCurrentTimeStamp());
            insertDescriptionStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing description", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertDescriptionStatement != null) {
//                try {
//                    insertDescriptionStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertDescriptionStatement'{}'", e);
//                }
//            }
            try {
                if (insertDescriptionStatement.getConnection() != null)
                    insertDescriptionStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertDescriptionStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeContent(String url,Boolean isH1Exist,Boolean isCanonicalExist,String urlQuery,Integer contentLength ,String contentHash) {
        try {
            insertContentStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into contents values " +
                    "(?,?,?,?,?,?,?)");
            insertContentStatement.setString(1,url);
            insertContentStatement.setBoolean(2,isH1Exist);
            insertContentStatement.setBoolean(3,isCanonicalExist);
            insertContentStatement.setString(4,urlQuery);
            insertContentStatement.setInt(5,contentLength);
            insertContentStatement.setString(6,contentHash);
            insertContentStatement.setTimestamp(7,SampleLauncher.getCurrentTimeStamp());
            insertContentStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing content", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertContentStatement != null) {
//                try {
//                    insertContentStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertContentStatement'{}'", e);
//                }
//            }
            try {
                if (insertContentStatement.getConnection() != null)
                    insertContentStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertContentStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeSimilarity(String srcUrl,String destUrl,Float percent) {
        try {
            insertSimilarityStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into similarities values " +
                    "(?,?,?,?)");
            insertSimilarityStatement.setString(1,srcUrl);
            insertSimilarityStatement.setString(2,destUrl);
            insertSimilarityStatement.setFloat(3,percent);
            insertSimilarityStatement.setTimestamp(4,SampleLauncher.getCurrentTimeStamp());
            insertSimilarityStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing similarity", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertSimilarityStatement != null) {
//                try {
//                    insertSimilarityStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertSimilarityStatement'{}'", e);
//                }
//            }
            try {
                if (insertSimilarityStatement.getConnection() != null)
                    insertSimilarityStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertSimilarityStatement'{}'",e);
            }
        }
    }

    @Override
    public void storeBacklink(String srcUrl,String targetUrl,String anchor,Boolean isDofollow) {
        try {
            insertBacklinkStatement =  comboPooledDataSource.getConnection().prepareStatement("insert into backlinks values " +
                    "(?,?,?,?,?)");
            insertBacklinkStatement.setString(1,srcUrl);
            insertBacklinkStatement.setString(2,targetUrl);
            insertBacklinkStatement.setString(3,anchor);
            insertBacklinkStatement.setBoolean(4,isDofollow);
            insertBacklinkStatement.setTimestamp(5,SampleLauncher.getCurrentTimeStamp());
            insertBacklinkStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while storing backlink", e);
            throw new RuntimeException(e);
        }finally {
//            if (insertBacklinkStatement != null) {
//                try {
//                    insertBacklinkStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing insertBacklinkStatement'{}'", e);
//                }
//            }
            try {
                if (insertBacklinkStatement.getConnection() != null)
                    insertBacklinkStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection insertBacklinkStatement'{}'",e);
            }
        }
    }

    @Override
    public void removeBacklink(String url) {
        try {
            removeBacklinkStatement =  comboPooledDataSource.getConnection().prepareStatement("delete from backlinks where source_url = ?");
            removeBacklinkStatement.setString(1,url);
            removeBacklinkStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while removing old backlinks", e);
            throw new RuntimeException(e);
        }finally {
//            if (removeBacklinkStatement != null) {
//                try {
//                    removeBacklinkStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing removeBacklinkStatement'{}'", e);
//                }
//            }
            try {
                if (removeBacklinkStatement.getConnection() != null)
                    removeBacklinkStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection removeBacklinkStatement'{}'",e);
            }
        }
    }

    @Override
    public Map<String,String> getHashes(String host) {
        Map<String,String> hashes = new HashMap<String, String>();

        try {
            getHashesStatement =  comboPooledDataSource.getConnection().prepareStatement("select url,content_hash from contents where url like ?");
            getHashesStatement.setString(1,"%"+host+"%");
            ResultSet rs = getHashesStatement.executeQuery();
            while (rs.next()) {
                hashes.put(rs.getString("url"),rs.getString("content_hash"));
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while getting hashes", e);
            throw new RuntimeException(e);
        }finally {
//            if (getHashesStatement != null) {
//                try {
//                    getHashesStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing getHashesStatement'{}'", e);
//                }
//            }
            try {
                if (getHashesStatement.getConnection() != null)
                    getHashesStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection getHashesStatement'{}'",e);
            }
        }
        return hashes;
    }

    @Override
    public void removeSite(String url) {
        try {
            removeSiteStatement =  comboPooledDataSource.getConnection().prepareStatement("delete from urls where url like ?");
            removeSiteStatement.setString(1,"%"+url+"%");
            removeSiteStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while removing old urls of the site", e);
            throw new RuntimeException(e);
        }finally {
//            if (removeSiteStatement != null) {
//                try {
//                    removeSiteStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing removeSiteStatement'{}'", e);
//                }
//            }
            try {
                if (removeSiteStatement.getConnection() != null)
                    removeSiteStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection removeSiteStatement'{}'",e);
            }
        }
    }

    @Override
    public void updateJob(String status,Timestamp finishTime,Integer siteId) {
        try {
            updateJobStatement = comboPooledDataSource.getConnection().prepareStatement("update crawling_jobs set status = ? , finished_at = ? where site_id = ?");
            updateJobStatement.setString(1,status);
            updateJobStatement.setTimestamp(2,finishTime);
            updateJobStatement.setInt(3,siteId);
            updateJobStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception while update job", e);
            throw new RuntimeException(e);
        }finally {
//            if (updateJobStatement != null) {
//                try {
//                    updateJobStatement.close();
//                } catch (SQLException e) {
//                    logger.error("SQL Exception while closing updateJobStatement'{}'", e);
//                }
//            }
            try {
                if (updateJobStatement.getConnection() != null)
                    updateJobStatement.getConnection().close();
            } catch (SQLException e) {
                logger.error("SQL Exception while closing connection updateJobStatement'{}'",e);
            }
        }
    }
}
