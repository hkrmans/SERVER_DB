package DB.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Household")
public class Household implements Serializable {

    @Id
    @Column(name = "householdId", nullable = false)
    private int householdId;
    @Column(name = "name", nullable = false)
    private String name;


    public Household(){

    }



    public Household(int householdId, String name) {
        this.householdId = householdId;
        this.name = name;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
