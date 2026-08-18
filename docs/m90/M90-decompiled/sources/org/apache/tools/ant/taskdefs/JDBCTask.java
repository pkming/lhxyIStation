package org.apache.tools.ant.taskdefs;

import android.accounts.AccountManager;
import android.content.Context;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public abstract class JDBCTask extends Task {
    private static final int HASH_TABLE_SIZE = 3;
    private static Hashtable<String, AntClassLoader> LOADER_MAP = new Hashtable<>(3);
    private Path classpath;
    private AntClassLoader loader;
    private boolean caching = true;
    private boolean autocommit = false;
    private String driver = null;
    private String url = null;
    private String userId = null;
    private String password = null;
    private String rdbms = null;
    private String version = null;
    private boolean failOnConnectionError = true;
    private List<Property> connectionProperties = new ArrayList();

    public void setClasspath(Path path) {
        this.classpath = path;
    }

    public void setCaching(boolean z) {
        this.caching = z;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public void setDriver(String str) {
        this.driver = str.trim();
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setAutocommit(boolean z) {
        this.autocommit = z;
    }

    public void setRdbms(String str) {
        this.rdbms = str;
    }

    public void setVersion(String str) {
        this.version = str;
    }

    public void setFailOnConnectionError(boolean z) {
        this.failOnConnectionError = z;
    }

    protected boolean isValidRdbms(Connection connection) {
        if (this.rdbms == null && this.version == null) {
            return true;
        }
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (this.rdbms != null) {
                String lowerCase = metaData.getDatabaseProductName().toLowerCase();
                log("RDBMS = " + lowerCase, 3);
                if (lowerCase == null || lowerCase.indexOf(this.rdbms) < 0) {
                    log("Not the required RDBMS: " + this.rdbms, 3);
                    return false;
                }
            }
            if (this.version != null) {
                String lowerCase2 = metaData.getDatabaseProductVersion().toLowerCase(Locale.ENGLISH);
                log("Version = " + lowerCase2, 3);
                if (lowerCase2 == null || (!lowerCase2.startsWith(this.version) && lowerCase2.indexOf(" " + this.version) < 0)) {
                    log("Not the required version: \"" + this.version + "\"", 3);
                    return false;
                }
            }
            return true;
        } catch (SQLException unused) {
            log("Failed to obtain required RDBMS information", 0);
            return false;
        }
    }

    protected static Hashtable<String, AntClassLoader> getLoaderMap() {
        return LOADER_MAP;
    }

    protected AntClassLoader getLoader() {
        return this.loader;
    }

    public void addConnectionProperty(Property property) {
        this.connectionProperties.add(property);
    }

    protected Connection getConnection() throws BuildException {
        if (this.userId == null) {
            throw new BuildException("UserId attribute must be set!", getLocation());
        }
        if (this.password == null) {
            throw new BuildException("Password attribute must be set!", getLocation());
        }
        if (this.url == null) {
            throw new BuildException("Url attribute must be set!", getLocation());
        }
        try {
            log("connecting to " + getUrl(), 3);
            Properties properties = new Properties();
            properties.put(Context.USER_SERVICE, getUserId());
            properties.put(AccountManager.KEY_PASSWORD, getPassword());
            for (Property property : this.connectionProperties) {
                String name = property.getName();
                String value = property.getValue();
                if (name == null || value == null) {
                    log("Only name/value pairs are supported as connection properties.", 1);
                } else {
                    log("Setting connection property " + name + " to " + value, 3);
                    properties.put(name, value);
                }
            }
            Connection connectionConnect = getDriver().connect(getUrl(), properties);
            if (connectionConnect == null) {
                throw new SQLException("No suitable Driver for " + this.url);
            }
            connectionConnect.setAutoCommit(this.autocommit);
            return connectionConnect;
        } catch (SQLException e) {
            if (!this.failOnConnectionError) {
                log("Failed to connect: " + e.getMessage(), 1);
                return null;
            }
            throw new BuildException(e, getLocation());
        }
    }

    private Driver getDriver() throws BuildException {
        Class<?> cls;
        if (this.driver == null) {
            throw new BuildException("Driver attribute must be set!", getLocation());
        }
        try {
            if (this.classpath != null) {
                synchronized (LOADER_MAP) {
                    if (this.caching) {
                        this.loader = LOADER_MAP.get(this.driver);
                    }
                    if (this.loader == null) {
                        log("Loading " + this.driver + " using AntClassLoader with classpath " + this.classpath, 3);
                        AntClassLoader antClassLoaderCreateClassLoader = getProject().createClassLoader(this.classpath);
                        this.loader = antClassLoaderCreateClassLoader;
                        if (this.caching) {
                            LOADER_MAP.put(this.driver, antClassLoaderCreateClassLoader);
                        }
                    } else {
                        log("Loading " + this.driver + " using a cached AntClassLoader.", 3);
                    }
                }
                cls = this.loader.loadClass(this.driver);
            } else {
                log("Loading " + this.driver + " using system loader.", 3);
                cls = Class.forName(this.driver);
            }
            return (Driver) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new BuildException("Class Not Found: JDBC driver " + this.driver + " could not be loaded", e, getLocation());
        } catch (IllegalAccessException e2) {
            throw new BuildException("Illegal Access: JDBC driver " + this.driver + " could not be loaded", e2, getLocation());
        } catch (InstantiationException e3) {
            throw new BuildException("Instantiation Exception: JDBC driver " + this.driver + " could not be loaded", e3, getLocation());
        }
    }

    public void isCaching(boolean z) {
        this.caching = z;
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public boolean isAutocommit() {
        return this.autocommit;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserid(String str) {
        this.userId = str;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRdbms() {
        return this.rdbms;
    }

    public String getVersion() {
        return this.version;
    }
}
