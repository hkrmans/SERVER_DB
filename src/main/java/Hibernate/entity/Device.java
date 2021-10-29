package Hibernate.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Device implements Serializable {
    @Id
    @Column(name = "device_Id", nullable = false)
    private Long device_Id;
    @Column(name = "name", nullable = false)
    String name;
    @Column(name = "type", nullable = false)
    String type;
    @Column(name = "value", nullable = false)
    int value;
    String commandOn;
    String commandOff;


    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.LAZY
    )
    Household household;

    public Device() {

    }

    public Device(Long device_Id, String name, String type, int value, String commandOn, String commandOff, Household household) {
        this.device_Id = device_Id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.commandOn = commandOn;
        this.commandOff = commandOff;

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

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

}
