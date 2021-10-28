package Hibernate.demo1;

import Hibernate.entity.Device;
import Hibernate.entity.Household;
import Hibernate.entity.Person;
import com.github.javafaker.Faker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Random;

public class main {
    public static void main(String[] args) {
//if the code doesn't work fist time go to Persistence.xml and un comment line 17 & 18.
        // adding 10 demo
        for (int i = 0; i < 2; i++) {
            demo();
        }


    }

    public static void demo() {
        // set up the connection throw
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

//       Random template
        Faker faker = new Faker();
        Random random = new Random();
//        Person person = new Person(random.nextLong(),
//                faker.name().firstName(), random.nextInt(100));
//        Device device = new Device(random.nextLong(), faker.company().name()
//                , random.nextBoolean(), person);
        Person person = new Person(1221l, faker.name().firstName(),
                faker.internet().emailAddress(), faker.internet().password());

        Household household = new Household(random.nextLong(), faker.address().city(),person);


        Device device = new Device(random.nextLong(), faker.company().name(),
                "lamp", 1, random.nextBoolean(), household);
        Device device1 = new Device(random.nextLong(), faker.company().name(),
                "lamp", 1, random.nextBoolean(), household);
        Device device2 = new Device(random.nextLong(), faker.company().name(),
                "lamp", 1, random.nextBoolean(), household);
//        Starting the transaction to the DB
        try {
            transaction.begin();
//            entityManager.persist(device);
//            entityManager.persist(device1);
//            entityManager.persist(device2);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {

                transaction.rollback();
            }

            entityManager.clear();
            entityManagerFactory.close();
        }

    }
}