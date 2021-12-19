import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "User")
public class UserDB implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private int userId;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "householdId", nullable = true)
    private int householdId;

    public UserDB(){

    }

    public UserDB(int userId, String username, String password, String name, int householdId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.householdId = householdId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }
}
