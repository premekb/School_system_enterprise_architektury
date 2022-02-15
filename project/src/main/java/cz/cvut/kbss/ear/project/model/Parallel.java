package cz.cvut.kbss.ear.project.model;

import com.sun.istack.NotNull;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;
import org.hibernate.annotations.Cascade;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.access.prepost.PreAuthorize;

@Entity
public class Parallel extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;

    @NotNull
    private DayOfWeek dayOfWeek;

    private String note;

    @NotNull
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private ParallelType parallelType;

    @ManyToOne
    @JoinColumn(name = "course_in_semester_id")
    private CourseInSemester course;

    @OrderBy("user")
    @ManyToMany(mappedBy = "parallels")
    private Collection<CourseStudent> courseStudents = new HashSet<>();

    @OrderBy("user")
    @ManyToMany(mappedBy = "parallels")
    private Collection<CourseTeacher> courseTeachers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

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

    public CourseInSemester getCourseInSemester() {
        return course;
    }

    public void setCourseInSemester(CourseInSemester courseInSemester) {
        this.course = courseInSemester;
    }

    public Collection<CourseStudent> getCourseStudents() {
        return courseStudents;
    }

    public void setCourseStudents(Collection<CourseStudent> courseStudents) {
        this.courseStudents = courseStudents;
    }

    public Collection<CourseTeacher> getCourseTeachers() {
        return courseTeachers;
    }

    public void setCourseTeachers(Collection<CourseTeacher> courseTeachers) {
        this.courseTeachers = courseTeachers;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<CourseParticipant> getAllParticipants() {
        List<CourseParticipant> participants = new ArrayList<>();
        participants.addAll(courseStudents);
        participants.addAll(courseTeachers);
        return participants;
    }

    @Override
    public String toString() {
        return "Parallel{" +
            "name='" + name + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", note='" + note + '\'' +
            ", capacity=" + capacity +
            ", parallelType=" + parallelType +
            ", courseInSemester=" + course +
            ", courseStudents=" + courseStudents +
            ", courseTeachers=" + courseTeachers +
            ", classroom=" + classroom +
            '}';
    }

    public void enrollParticipant(CourseStudent student) {
        courseStudents.add(student);
        student.getParallels().add(this);
    }

    public void enrollParticipant(CourseTeacher teacher) {
        courseTeachers.add(teacher);
        teacher.getParallels().add(this);
    }

    public void unenrollParticipant(CourseStudent student) {
        courseStudents.remove(student);
        student.getParallels().remove(this);
    }

    public void unenrollParticipant(CourseTeacher teacher) {
        courseTeachers.remove(teacher);
        teacher.getParallels().remove(this);
    }
}
