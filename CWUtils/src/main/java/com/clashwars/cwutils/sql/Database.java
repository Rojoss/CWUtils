package com.clashwars.cwutils.sql;

import java.sql.Connection;

import com.clashwars.cwutils.CWUtils;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL, SQLite, etc.)
 * 
 * @author -_Husky_-
 * @author tips48
 */
public abstract class Database {

    /**
     * Plugin instance, use for plugin.getDataFolder() and plugin.getLogger()
     */
    protected CWUtils plugin;

    /**
     * Creates a new Database
     * 
     * @param plugin2
     *            Plugin instance
     */
    protected Database(CWUtils plugin2) {
        this.plugin = plugin2;
    }

    /**
     * Opens a connection with the database
     * 
     * @return Connection opened
     */
    public abstract Connection openConnection();

    /**
     * Checks if a connection is open with the database
     * 
     * @return true if a connection is open
     */
    public abstract boolean checkConnection();

    /**
     * Gets the connection with the database
     * 
     * @return Connection with the database, null if none
     */
    public abstract Connection getConnection();

    /**
     * Closes the connection with the database
     */
    public abstract void closeConnection();
}