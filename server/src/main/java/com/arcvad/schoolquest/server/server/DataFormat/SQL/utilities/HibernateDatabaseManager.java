package com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.MaterialRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.FamilyRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.Player;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.PlayerRegistrar;
import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities.User;
import com.arcvad.schoolquest.server.server.GlobalUtils.Entiies.TransactionUser;
import com.arcvad.schoolquest.server.server.GlobalUtils.EnumRandomizer;
import com.arcvad.schoolquest.server.server.Playerutils.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

import static com.arcvad.schoolquest.server.server.DataFormat.SQL.utilities.HibernateUtil.sessionFactory;
import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class HibernateDatabaseManager {

    // Save an entity
    public <T> void saveEntity(T entity) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during entity saving \{e}");
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException(STR."Error during entity saving \{e}");
        }finally {
            session.clear();
            session.close();
        }
    }

    // Get an entity by ID
    public <T> T getEntityById(Class<T> clazz, Object id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(clazz, id);
        } catch (Exception e) {
            throw new RuntimeException(STR."Error during entity saving \{e}");
        }finally {
            session.close();
        }
    }

    public <T> T getEntityByNaturalId(Class<T> clazz, Object id) {
        Session session = sessionFactory.openSession();
        try {
            return session.byNaturalId(clazz)
                .using("key", id)
                .load();
        } catch (Exception e) {
            throw new RuntimeException(STR."Error during entity saving \{e}");
        }finally {
            session.close();
        }
    }

    // Update an entity
    public <T> void updateEntity(T entity) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during entity updating \{e}");
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException(STR."Error during entity saving \{e}");
        } finally {
            session.close();
        }
    }

    public <T> void saveOrUpdate(T entity) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during entity updating \{e}");
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException(STR."Error during entity saving \{e}");
        } finally {
            session.close();
        }
    }

    // Check if an entity exists by ID
    public <T> boolean entityExists(Class<T> clazz, Object id) {
        Session session = sessionFactory.openSession();
        try {
            String entityName = clazz.getSimpleName();
            String queryString = "SELECT COUNT(e) FROM " + entityName + " e WHERE e.id = :id";
            Long count = session.createQuery(queryString, Long.class)
                .setParameter("id", id)
                .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during entity query \{e}");
            e.printStackTrace();
            return false;
        }finally {
            session.close();
        }
    }

    public <T> boolean entityExistsByNaturalId(Class<T> clazz, String naturalIdProperty, Object naturalIdValue) {
        Session session = sessionFactory.openSession();
        try {
            // Start a transaction to use NaturalId API
            session.beginTransaction();

            // Use Hibernate's natural ID query
            T result = session.byNaturalId(clazz)
                .using(naturalIdProperty, naturalIdValue)
                .load();

            // Commit transaction
            session.getTransaction().commit();

            // Check if entity exists
            return result != null;

        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during natural ID query: \{e}");
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }


    // Delete an entity
    public <T> void deleteEntity(T entity) {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            logger.error("ARC-SQL", STR."Error during entity deletion \{e}");
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    public boolean createUser(TransactionUser user) {
        try {
            PlayerRegistrar registrar = getEntityById(PlayerRegistrar.class, 1L);

            logger.info("ARC-XML", "Making the default user...");
            User sqlUser = createUserObject(user);
            sqlUser.setPlayerRegistrar(registrar);

            Player player = createPlayerObject(user.getGender(), sqlUser);
            saveEntity(player);
            updateEntity(registrar);

            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    private User createUserObject(TransactionUser user) {
        User sqlUser = new User();
        sqlUser.setUsername(user.getUsername());
        sqlUser.setEmail(user.getEmail());
        sqlUser.setPassword(user.getPassword());
        sqlUser.setLastname(user.getLastname());
        sqlUser.setFirstname(user.getFirstname());
        sqlUser.setGender(user.getGender());
        return sqlUser;
    }
    private Player createPlayerObject(Genders gender, User user) throws InterruptedException {
        Player player = new Player();

        MaterialRegistrar materialRegistrar = getEntityById(MaterialRegistrar.class, 1L);

        // Assign default clothing to the player
        player.setCurrentTopCloth(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );
        player.setCurrentBottomCloth(
                materialRegistrar.getBottomClothList().stream()
                    .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                    .findFirst()
                    .get()
        );
        player.setCurrentShoe(
                materialRegistrar.getShoesList().stream()
                    .filter(shoe -> shoe.getKey().equals("s_def_u"))
                    .findFirst()
                    .get()
        );

        // Populate the collections with the shared instances
        player.setCollectedTopCloth(new ArrayList<>());
        player.setCollectedBottomCloth(new ArrayList<>());
        player.setCollectedShoes(new ArrayList<>());

        player.getCollectedTopCloth().add(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );
        player.getCollectedTopCloth().add(
            materialRegistrar.getTopClothList().stream()
                .filter(topCloth -> topCloth.getKey().equals(STR."t_alt_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                .findFirst()
                .get()
        );

        player.getCollectedBottomCloth().add(
                materialRegistrar.getBottomClothList().stream()
                    .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_def_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                    .findFirst()
                    .get()
        );
        player.getCollectedBottomCloth().add(
                materialRegistrar.getBottomClothList().stream()
                    .filter(bottomCloth -> bottomCloth.getKey().equals(STR."c_alt_\{gender.equals(Genders.MALE) ? "m" : "f"}"))
                    .findFirst()
                    .get()
        );

        player.getCollectedShoes().add(
                materialRegistrar.getShoesList().stream()
                    .filter(shoe -> shoe.getKey().equals("s_def_u"))
                    .findFirst()
                    .get()
        );
        player.getCollectedShoes().add(
                materialRegistrar.getShoesList().stream()
                    .filter(shoe -> shoe.getKey().equals("s_alt_u"))
                    .findFirst()
                    .get()
        );

        player.setIrisHue(new Color(0, 0, 0, 100));
        player.setHairHue(new Color(0, 0, 0, 100));
        player.setEyeLashHue(new Color(0, 0, 0, 100));
        player.setSkinHue(new Color(0, 0, 0, 100));
        player.setEyeLashType(Styles.EyelashStyles.DEFAULT);
        player.setHairType(Styles.HairStyles.DEFAULT);

        player.setCurrentAccessories(new ArrayList<>());
        player.setCollectedAccessories(new ArrayList<>());

        FamilyNames familyName = EnumRandomizer.getRandomEnum(FamilyNames.class);

        FamilyRegistrar familyRegistrar = getEntityById(FamilyRegistrar.class, 1L);
        Family family = getFamily(familyRegistrar, familyName);
        family.getFamilyMembers().add(user);

        player.setUser(user);
        player.setId(UUIDGenerator.generateUUIDFromString(user.getUsername()).toString());

        user.setFamily(family);
        user.setFamilyPosition(family.getFamilySize());

        return player;
    }
    private static Family getFamily(FamilyRegistrar familyRegistrar, FamilyNames familyName) {
        List<Family> families = familyRegistrar.getFamilies();

        return families.stream()
                .filter(family -> family.getFamilyNames().equals(familyName))
                .findAny()
                .orElseGet(() ->
                    {
                        com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family newFamily =
                            new com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family(
                                familyName
                            );
                        newFamily.setFamilyRegistrar(familyRegistrar);
                        newFamily.setFamilySize();
                        newFamily.setFamilyWealth(Wealth.getRandomWealthByWeight());
                        newFamily.setFamilyMembers(new ArrayList<>());
                        return newFamily;
                    }
                );
    }

}
