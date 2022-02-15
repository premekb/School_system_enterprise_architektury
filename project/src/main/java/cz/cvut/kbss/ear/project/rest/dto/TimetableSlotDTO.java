package cz.cvut.kbss.ear.project.rest.dto;

import cz.cvut.kbss.ear.project.model.Classroom;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Objects;

public class TimetableSlotDTO {
    private String name;

    private Time startTime;

    private Time endTime;

    private DayOfWeek dayOfWeek;

    private String courseCode;

    private Classroom classroom;

    @Enumerated(EnumType.STRING)
    private ParallelType parallelType;

    public TimetableSlotDTO() {
    }

    public TimetableSlotDTO(Parallel parallel){
        this.name = parallel.getName();
        this.startTime = parallel.getStartTime();
        this.endTime = parallel.getEndTime();
        this.dayOfWeek = parallel.getDayOfWeek();
        this.courseCode = parallel.getCourseInSemester().getCourse().getCode();
        this.classroom = parallel.getClassroom();
        this. parallelType = parallel.getParallelType();
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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public ParallelType getParallelType() {
        return parallelType;
    }

    public void setParallelType(ParallelType parallelType) {
        this.parallelType = parallelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimetableSlotDTO that = (TimetableSlotDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && dayOfWeek == that.dayOfWeek && Objects.equals(courseCode, that.courseCode) && Objects.equals(classroom, that.classroom) && parallelType == that.parallelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startTime, endTime, dayOfWeek, courseCode, classroom, parallelType);
    }

    @Override
    public String toString() {
        return "TimetableSlotDTO{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dayOfWeek=" + dayOfWeek +
                ", courseCode='" + courseCode + '\'' +
                ", classroom=" + classroom +
                ", parallelType=" + parallelType +
                '}';
    }
}
