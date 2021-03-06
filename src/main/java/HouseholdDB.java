import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Household")
public class HouseholdDB implements Serializable {

    @Id
    @Column(name = "householdId", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int householdId;
    @Column(name = "name", nullable = false)
    private String name;
    public HouseholdDB(){
    }

    public HouseholdDB(int householdId, String name) {
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
