package cz.cvut.kbss.ear.project.rest.dto;

import com.sun.istack.NotNull;
import cz.cvut.kbss.ear.project.model.*;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParallelDTO {
    private Integer id;

    private String name;

    private Time startTime;

    private Time endTime;

    private DayOfWeek dayOfWeek;

    private String note;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private ParallelType parallelType;

    private List<User> students;

    private List<User> teachers;

    private Classroom classroom;

    private String courseCode;

    private String semesterCode;

    public ParallelDTO() {
    }

    public ParallelDTO(Parallel parallel) {
        id = parallel.getId();
        name = parallel.getName();
        startTime = parallel.getStartTime();
        endTime = parallel.getEndTime();
        dayOfWeek = parallel.getDayOfWeek();
        note = parallel.getNote();
        capacity = parallel.getCapacity();
        parallelType = parallel.getParallelType();
        students = parallel.getCourseStudents()
                .stream()
                .map(CourseParticipant::getUser)
                .collect(Collectors.toList());
        teachers = parallel.getCourseTeachers()
                .stream()
                .map(CourseParticipant::getUser)
                .collect(Collectors.toList());
        classroom = parallel.getClassroom();
        courseCode = parallel.getCourseInSemester().getCourse().getCode();
        semesterCode = parallel.getCourseInSemester().getSemester().getCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ParallelType getParallelType() {
        return parallelType;
    }

    public void setParallelType(ParallelType parallelType) {
        this.parallelType = parallelType;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<User> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParallelDTO that = (ParallelDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && dayOfWeek == that.dayOfWeek && Objects.equals(note, that.note) && Objects.equals(capacity, that.capacity) && parallelType == that.parallelType && Objects.equals(students, that.students) && Objects.equals(teachers, that.teachers) && Objects.equals(classroom, that.classroom) && Objects.equals(courseCode, that.courseCode) && Objects.equals(semesterCode, that.semesterCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startTime, endTime, dayOfWeek, note, capacity, parallelType, students, teachers, classroom, courseCode, semesterCode);
    }

    @Override
    public String toString() {
        return "ParallelDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dayOfWeek=" + dayOfWeek +
                ", note='" + note + '\'' +
                ", capacity=" + capacity +
                ", parallelType=" + parallelType +
                ", students=" + students +
                ", teachers=" + teachers +
                ", classroom=" + classroom +
                ", courseCode='" + courseCode + '\'' +
                ", semesterCode='" + semesterCode + '\'' +
                '}';
    }
}
