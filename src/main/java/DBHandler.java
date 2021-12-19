import javax.persistence.*;
import java.util.ArrayList;

public class DBHandler {

    ArrayList<DeviceDB> list = new ArrayList<>();
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    private java.util.Map<String, Object> Map;

    public DeviceDB getDeviceDB(int id) {
        try {
            entityManager.getTransaction().begin();
            DeviceDB device = entityManager.find(DeviceDB.class, id);
            entityManager.getTransaction().commit();
            return device;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Device> householdDevices(int householdId) {
        ArrayList<Device> householdDevices = new ArrayList<>();

        try {
            String strQuery = "SELECT c FROM DeviceDB c WHERE c.householdId = " + householdId;
            TypedQuery<DeviceDB> typedQuery = entityManager.createQuery(strQuery, DeviceDB.class);
            ArrayList<DeviceDB> deviceDBS;
            deviceDBS = (ArrayList<DeviceDB>) typedQuery.getResultList();

            for (DeviceDB d : deviceDBS) {
                householdDevices.add(new Device(d.getDeviceId(), d.getName(), d.getType(), d.getValue(), d.getHouseholdId()));
            }

            return householdDevices;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateDeviceValue(DeviceDB device) {
        entityManager.getTransaction().begin();
        entityManager.merge(device);
        entityManager.getTransaction().commit();
    }


    public DeviceDB getDeviceUsingCommands(String command) {
        ArrayList<Device> householdDevices = new ArrayList<>();
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
    }

}
