package cz.cvut.kbss.ear.project.model.enums;

public enum CourseCompletionType {
    CLFD_CREDIT("Klasifikovaný zápočet"),
    CREDIT("Zápočet"),
    EXAM("Zkouška"),
    NOTHING("Nic"),
    DEFENCE("Obhajoba"),
    UNDEFINED("Nedefinovaný"),
    CREDIT_EXAM("Zápočet a zkouška");

    private final String description;

    CourseCompletionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
