import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Device")
public class DeviceDB {

    @Id
    @Column(name = "deviceId" , nullable = false)
    private int deviceId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "value", nullable = false)
    private int value;
    @Column(name = "householdId", nullable = false)
    private int householdId;
    @Column(name = "commandOn", nullable = false)
    private String commandOn;
    @Column(name = "commandOff", nullable = false)
    private String commandOff;

   public DeviceDB() {

   }

    public DeviceDB(int deviceId, String name, String type, int value, int householdId, String commandOn, String commandOff) {
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.householdId = householdId;
        this.commandOn = commandOn;
        this.commandOff = commandOff;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public String getCommandOn() {
        return commandOn;
    }

    public void setCommandOn(String commandOn) {
        this.commandOn = commandOn;
    }

    public String getCommandOff() {
        return commandOff;
    }

    public void setCommandOff(String commandOff) {
        this.commandOff = commandOff;
    }
}
