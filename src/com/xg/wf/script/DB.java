package com.xg.wf.script;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;

public class DB {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DB.class);

    private String url;
    private String user;
    private String pwd;
    private String driver;
    private String sourcedb;
    public DB() {
        Properties prop = new Properties();
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("db.properties"));
            user = prop.getProperty("jdbc.username");
            pwd = prop.getProperty("jdbc.password");
            url = prop.getProperty("jdbc.url");
            driver = prop.getProperty("jdbc.driver");
            sourcedb="["+prop.getProperty("sourcedb")+"]";
            LOGGER.info("==============读取db配置文件成功==============");
        } catch (Exception e) {
            LOGGER.error("==============读取db配置文件失败==============");
            LOGGER.error(e.getMessage());
        }
    }


    public Sql SQL() {
        try {
            return Sql.newInstance(url, user, pwd, driver);
        } catch (Exception e) {
            LOGGER.error("==============数据连接JDBC失败==============");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public String Config(Sql sql,int key){
        try {
            GroovyRowResult row= sql.firstRow("SELECT Value FROM "+sourcedb+".dbo.Setting WHERE KeyID="+key);
            return row.getProperty("Value").toString();
        } catch (SQLException e) {
            LOGGER.error("==============查询设置表失败==============");
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public String getUrl() {
        return url;
    }


    public String getUser() {
        return user;
    }


    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDriver() {
        return driver;
    }
}
