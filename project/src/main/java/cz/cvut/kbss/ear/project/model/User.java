package cz.cvut.kbss.ear.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import cz.cvut.kbss.ear.project.model.enums.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Entity
@Table(name = "SYSTEM_USER")
@NamedQueries({
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User extends AbstractEntity {

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @JsonIgnore
    private String password;

    private String permanentResidence;

    private String mailingAddress;

    @NotNull
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void setRole(Role role) {
        this.role = role;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        setPassword(passwordEncoder.encode(password));
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", password='" + password + '\'' +
            ", permanentResidence='" + permanentResidence + '\'' +
            ", mailingAddress='" + mailingAddress + '\'' +
            ", email='" + email + '\'' +
            ", role=" + role +
            '}';
    }

    @JsonIgnore
    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
