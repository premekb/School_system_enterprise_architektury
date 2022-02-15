package cz.cvut.kbss.ear.project.model.enums;

public enum SemesterType {
    WINTER("Zimní"),
    SUMMER("Letní");

    private final String name;

    SemesterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
