package cz.cvut.kbss.ear.project.rest.dto;

import cz.cvut.kbss.ear.project.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CourseInSemesterDTO {
    private Course course;

    private Semester semester;

    private List<User> teachers;

    public CourseInSemesterDTO() {
    }

    public CourseInSemesterDTO(CourseInSemester courseInSemester) {
        course = courseInSemester.getCourse();
        semester = courseInSemester.getSemester();
        teachers = courseInSemester.getTeachers().stream().map(CourseParticipant::getUser).collect(Collectors.toList());
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public List<User> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseInSemesterDTO that = (CourseInSemesterDTO) o;
        return Objects.equals(course, that.course) && Objects.equals(semester, that.semester) && Objects.equals(teachers, that.teachers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, semester, teachers);
    }

    @Override
    public String toString() {
        return "CourseInSemesterDTO{" +
                "course=" + course +
                ", semester=" + semester +
                ", teachers=" + teachers +
                '}';
    }
}
