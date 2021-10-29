package Hibernate.entity;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "household")
@Entity
public class Household implements Serializable {
    @Id
    @Column(name = "household_id", nullable = false)
    private Long household_id;
    String name;
     @ManyToOne(
             cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
             fetch = FetchType.LAZY
     )
     @JoinColumn(name = "person_person_id")
     Person person;


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