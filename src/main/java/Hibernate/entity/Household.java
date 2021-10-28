package Hibernate.entity;

import javax.persistence.*;

@Table(name = "household")
@Entity
public class Household {
    @Id
    @Column(name = "household_id", nullable = false)
    private Long household_id;
    String name;

    @ManyToOne
    @JoinColumn(name = "household_person_id")
    private Person person;


    public Household(Long household_id, String name, Person person) {
        this.household_id = household_id;
        this.name = name;
        this.person = person;
    }

    public Household() {

    }

    public Long getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(Long household_id) {
        this.household_id = household_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }




}