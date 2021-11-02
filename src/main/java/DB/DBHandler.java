package DB;

import DB.Entity.Household;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class DBHandler {

    private static DBHandler dbHandler;

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Household car = new Household(1,"SimpizHus");

        try {
            transaction.begin();


            entityManager.persist(car);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }

            entityManager.clear();
            entityManagerFactory.close();
        }
    }


    private DBHandler() {

    }

    public static DBHandler getInstance() {
        if (dbHandler == null) {
            dbHandler = new DBHandler();
        }
        return dbHandler;
    }

}
