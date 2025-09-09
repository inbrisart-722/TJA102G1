package com.eventra.member.controller;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // 預設會讀取 classpath 下的 hibernate.cfg.xml
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");

            ServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder()
                            .applySettings(cfg.getProperties())
                            .build();

            return cfg.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(
                    "Initial SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    private HibernateUtil() {
        // 工具類，不允許實例化
    }
}
