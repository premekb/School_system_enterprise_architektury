package cz.cvut.kbss.ear.project.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    REGULAR_USER("ROLE_REGULAR_USER"),
    STUDY_DEPARTMENT_EMPLOYEE("ROLE_STUDY_DEPARTMENT_EMPLOYEE");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
