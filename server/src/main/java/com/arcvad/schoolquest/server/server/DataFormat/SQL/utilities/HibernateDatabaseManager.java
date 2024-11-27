package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateDatabaseManager {

    // Save an entity
    public <T> void saveEntity(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Get an entity by ID
    public <T> T getEntityById(Class<T> clazz, Object id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(clazz, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update an entity
    public <T> void updateEntity(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Check if an entity exists by ID
    public <T> boolean entityExists(Class<T> clazz, Object id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String entityName = clazz.getSimpleName();
            String queryString = "SELECT COUNT(e) FROM " + entityName + " e WHERE e.id = :id";
            Long count = session.createQuery(queryString, Long.class)
                .setParameter("id", id)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

            // Delete an entity
    public <T> void deleteEntity(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
