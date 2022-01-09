import org.hibernate.sql.Update;

import javax.persistence.*;
import java.util.ArrayList;

public class DBHandler {


    public DeviceDB getDeviceDB(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            entityManager.getTransaction().begin();
            DeviceDB device = entityManager.find(DeviceDB.class, id);
            entityManager.getTransaction().commit();
            return device;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public ArrayList<Device> householdDevices(int householdId) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        ArrayList<Device> householdDevices = new ArrayList<>();

        try {
            String strQuery = "SELECT c FROM DeviceDB c WHERE c.householdId = " + householdId;
            TypedQuery<DeviceDB> typedQuery = entityManager.createQuery(strQuery, DeviceDB.class);
            ArrayList<DeviceDB> deviceDBS;
            deviceDBS = (ArrayList<DeviceDB>) typedQuery.getResultList();

            for (DeviceDB d : deviceDBS) {
                householdDevices.add(new Device(d.getDeviceId(), d.getName(), d.getType(), d.getValue(), d.getHouseholdId(),d.getTimer()));
            }

            return householdDevices;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public DeviceDB getDeviceDBFromAlarm(String command){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        command = "'" + command + "'";
        try {
            String strQuery = "SELECT c FROM AlarmDB c WHERE c.alarmCode = " + command;
            TypedQuery<AlarmDB> typedQuery = entityManager.createQuery(strQuery, AlarmDB.class);
            ArrayList<AlarmDB> AlarmDBS;

            AlarmDBS = (ArrayList<AlarmDB>) typedQuery.getResultList();
            DeviceDB deviceDB = getDeviceDB(AlarmDBS.get(0).getDeviceId());

            return deviceDB;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public void updateUserHouseholdValue(User user) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getTransaction().begin();
        UserDB user2 = getUserUsingUsername(user.getUsername());
        user2.setHouseholdId(user.getHouseholdId());
        entityManager.merge(user2);
        entityManager.getTransaction().commit();

        if (transaction.isActive()) {
            transaction.rollback();
        }
        entityManager.clear();
        entityManagerFactory.close();
    }

    public void updateDeviceValue(DeviceDB device) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getTransaction().begin();
        entityManager.merge(device);
        entityManager.getTransaction().commit();

        if (transaction.isActive()) {
            transaction.rollback();
        }
        entityManager.clear();
        entityManagerFactory.close();
    }

    public void updateUser(UserDB userDB) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        entityManager.getTransaction().begin();
        entityManager.merge(userDB);
        entityManager.getTransaction().commit();

        if (transaction.isActive()) {
            transaction.rollback();
        }
        entityManager.clear();
        entityManagerFactory.close();
    }


    public DeviceDB getDeviceUsingCommands(String command) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        command = "'" + command + "'";
        try {
            String strQuery = "SELECT c FROM DeviceDB c WHERE c.commandOn = " + command;
            TypedQuery<DeviceDB> typedQuery = entityManager.createQuery(strQuery, DeviceDB.class);
            ArrayList<DeviceDB> deviceDBS;

            deviceDBS = (ArrayList<DeviceDB>) typedQuery.getResultList();
            if(deviceDBS.size() == 0 ){
                strQuery = "SELECT c FROM DeviceDB c WHERE c.commandOff = " + command;
                typedQuery = entityManager.createQuery(strQuery, DeviceDB.class);
                deviceDBS = (ArrayList<DeviceDB>) typedQuery.getResultList();
                return deviceDBS.get(0);
            }
            return deviceDBS.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public UserDB getUserUsingUsername(String username) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        username = "'" + username + "'";

        try {
            String strQuery = "SELECT c FROM UserDB c WHERE c.username = " + username;
            TypedQuery<UserDB> typedQuery = entityManager.createQuery(strQuery, UserDB.class);
            ArrayList<UserDB> userDBS;
            userDBS = (ArrayList<UserDB>) typedQuery.getResultList();

            if(userDBS.size() ==  0){
                return null;
            }
            return userDBS.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public HouseholdDB getHousehold(int householdID) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            String strQuery = "SELECT c FROM HouseholdDB c WHERE c.householdId = " + householdID;
            TypedQuery<HouseholdDB> typedQuery = entityManager.createQuery(strQuery, HouseholdDB.class);
            ArrayList<HouseholdDB> householdDBS;
            householdDBS = (ArrayList<HouseholdDB>) typedQuery.getResultList();

            if(householdDBS.size() ==  0){
                return null;
            }
            return householdDBS.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public void createUser(User user){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        // set up the connection throw
        UserDB userDB = new UserDB(0,user.getUsername(),user.getPassword(), user.getName(),user.getHouseholdId());
//        Starting the transaction to the DB
        try {
            transaction.begin();
            entityManager.persist(userDB);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public HouseholdDB createHousehold (Household household){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        // set up the connection throw
        HouseholdDB householdDB = new HouseholdDB(0,household.getName());
//        Starting the transaction to the DB
        try {
            transaction.begin();
            entityManager.persist(householdDB);
            entityManager.flush();
            transaction.commit();
            return householdDB;
        } finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

    public ArrayList<DeviceDB> getAllDevices(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            String strQuery = "SELECT c FROM DeviceDB c";
            TypedQuery<DeviceDB> typedQuery = entityManager.createQuery(strQuery, DeviceDB.class);
            ArrayList<DeviceDB> deviceDBS;
            deviceDBS = (ArrayList<DeviceDB>) typedQuery.getResultList();
            return deviceDBS;

        } catch (Exception e) {
            return null;
        }
        finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }
            entityManager.clear();
            entityManagerFactory.close();
        }
    }

}
