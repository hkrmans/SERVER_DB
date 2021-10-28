package Hibernate.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
public class DatabaseHandler { private static final SessionFactory sessionFactory;
    private static final Session session;

    static {
        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure("./hibernate.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(Device.class)
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Household.class)
                
                .getMetadataBuilder()
                .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                .build();
        sessionFactory = metadata.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    private DatabaseHandler() {
    }

    public static <T> T load(final Class<T> tClass, Serializable key) {
        session.beginTransaction();
        T t = session.get(tClass, key);
        session.getTransaction().commit();
        return t;
    }

    /**
     * This method will get all object in table <the command to run it is
     * List<Device> users = DatabaseHandler.loadAll(Device.class);
     *
     * @param tClass A Hibernate annotated class type
     * @return A list containing objects of type T
     */
    public static <T> List<T> loadAll(Class<T> tClass) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(tClass);
        criteria.from(tClass);
        return session.createQuery(criteria).getResultList();
    }

    public static void save(Object object) {
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
    }

    public static void delete(Object object) {
        session.beginTransaction();
        session.delete(object);
        session.getTransaction().commit();
    }

    

    

    public static List<Object[]> query(String query) {
        return session.createQuery(query, Object[].class).getResultList();
    }

    public static List sqlQuery(String query) {
        return session.createSQLQuery(query).getResultList();

    }


//    public static List<Household> getDevices(Household Household) {
//        return session.createQuery("SELECT T FROM Device T JOIN Person P ON T LIKE P JOIN Result R ON T " +
//                        "LIKE R.patient JOIN Household E ON E LIKE R.examiner WHERE E.id LIKE :ssn GROUP BY T", Household.class)
//                .setParameter("ssn", Household.getHousehold_id())
//                .getResultList();
//    }

    public static List<Household> getHousehold(Person person) {
        return null;
    }
}