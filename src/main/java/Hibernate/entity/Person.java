package Hibernate.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Person implements Serializable {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)//strategy for generating auto generated number
    private Long person_id;
    String name;
    String email;
    String password;
    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH,CascadeType.ALL},
            fetch = FetchType.LAZY,
            mappedBy = "person"
    )
     List<Household> households;
    public Person() {
    }

    public Person(Long person_id, String name, String email, String password) {
        this.person_id = person_id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Transient
    public List<Household> getHouseholds() {
        return DatabaseHandler.getHousehold(this);
    }

    public void setHouseholds(List<Household> households) {
        this.households = households;
    }
}