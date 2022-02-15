package cz.cvut.kbss.ear.project.rest.dto;

import java.util.Objects;

public class UsernameDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameDTO that = (UsernameDTO) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UsernameDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}

