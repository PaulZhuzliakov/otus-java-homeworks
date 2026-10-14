package hw14;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long id;

    private String login;
    private String email;
    private int age;

    public User() {
    }

    public User(String login, String email, int age) {
        this.login = login;
        this.email = email;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
