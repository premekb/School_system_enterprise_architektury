package cz.cvut.kbss.ear.project.model.enums;

public enum ParallelType {
    LECTURE("přednáška"),
    TUTORIAL("cvičení"),
    LABORATORY("laboratoř"),
    UNDEFINED("nedefinovaný");

    private final String name;

    ParallelType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
