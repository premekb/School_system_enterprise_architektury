package cz.cvut.kbss.ear.project.rest.dto;

import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.Role;
import java.util.Collections;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class MeDTO {
    String username;
    String firstName;
    String lastName;
    String email;
    List<String> scope;

    public MeDTO() {

    }

    public MeDTO(User user) {
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        scope = Collections.singletonList(user.getRole().name());
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getScope() {
        return scope;
    }
}
