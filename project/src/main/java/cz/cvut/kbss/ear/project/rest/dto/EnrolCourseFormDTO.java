package cz.cvut.kbss.ear.project.rest.dto;

public class EnrolCourseFormDTO {
    private String course;
    private String semester;

    public EnrolCourseFormDTO() {
    }

    public EnrolCourseFormDTO(String course, String semester) {
        this.course = course;
        this.semester = semester;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
