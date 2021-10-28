package Hibernate.entity;

import javax.persistence.*;

@Entity
public class Device {
    @Id
    private Long device_Id;
    String name;
    String type;
    int value;
    boolean status;

    @ManyToOne(
            cascade = CascadeType.ALL,
            optional = false
    )
    Household household;

    public Device() {

    }

    public Device(Long device_Id, String name, String type, int value, boolean status, Household household) {
        this.device_Id = device_Id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.status = status;
        this.household = household;
    }

    public Long getDevice_Id() {
        return device_Id;
    }

    public void setDevice_Id(Long device_Id) {
        this.device_Id = device_Id;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

}
