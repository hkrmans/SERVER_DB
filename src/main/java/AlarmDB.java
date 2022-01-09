import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Alarm")
public class AlarmDB {

    @Id
    @Column(name = "alarmId", nullable = false)
    private int alarmId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "value", nullable = false)
    private int value;
    @Column(name = "deviceId", nullable = false)
    private Integer deviceId;
    @Column(name = "alarmCode", nullable = false)
    private String alarmCode;


    public AlarmDB() {

    }

    public AlarmDB(String name, int value, Integer deviceId, String alarmCode) {
        this.name = name;
        this.value = value;
        this.deviceId = deviceId;
        this.alarmCode = alarmCode;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }
}