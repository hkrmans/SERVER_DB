package Hibernate.entity;

import javax.persistence.*;

@Entity
public class Device {
    @Id
    @Column(name = "device_Id", nullable = false)
    private Long device_Id;
    @Column(name = "name")
    String name;
    @Column(name = "status")
    boolean status;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "person")
    Person person;

    public Device(Long device_Id, String name, boolean status, Person person) {
        this.device_Id = device_Id;
        this.name = name;
        this.status = status;
        this.person = person;
    }

    public Device() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public Long getDevice_Id() {
        return device_Id;
    }

    public void setDevice_Id(Long cariId) {
        this.device_Id = cariId;
    }


}
