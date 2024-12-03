package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class HibernateUtil {
    public static SessionFactory sessionFactory = null;

    public static void initialise(File configFile) {
        try {
            sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
            sessionFactory.openSession();
            logger.success("ARC-SQL", "Session started successfully");
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void initialise(SessionFactory sessionFactory, boolean isSqlite, File file) {
        try {
            if (isSqlite){
                if (!file.exists()){
                    File parentFolder = file.getParentFile();
                    parentFolder.mkdirs();
                }
                file.mkdir();
            }
            sessionFactory.openSession();
            logger.success("ARC-SQL", "Session started successfully");
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}

