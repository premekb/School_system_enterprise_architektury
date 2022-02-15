package cz.cvut.kbss.ear.project.kosapi.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import cz.cvut.kbss.ear.project.kosapi.links.AtomLink;
import cz.cvut.kbss.ear.project.kosapi.links.ParallelLink;
import cz.cvut.kbss.ear.project.kosapi.links.TeacherLink;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class KosParallel implements Serializable {
    private ParallelLink link;

    @JacksonXmlProperty(localName = "capacity")
    private String capacity;

    @JacksonXmlProperty(localName = "code")
    private String code;

    @JacksonXmlProperty(localName = "parallelType")
    private String parallelType;

    @JacksonXmlProperty(localName = "timetableSlot")
    private KosTimetableSlot timetableSlot;

    @JacksonXmlElementWrapper(localName = "teacher", useWrapping = false)
    private TeacherLink[] teacher;

    public ParallelLink getLink() {
        return link;
    }

    public void setLink(ParallelLink link) {
        this.link = link;
    }

    public TeacherLink[] getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherLink[] teacher) {
        this.teacher = teacher;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParallelType() {
        return parallelType;
    }

    public void setParallelType(String parallelType) {
        this.parallelType = parallelType;
    }

    public KosTimetableSlot getTimetableSlot() {
        return timetableSlot;
    }

    public void setTimetableSlot(KosTimetableSlot timetableSlot) {
        this.timetableSlot = timetableSlot;
    }

    public TeacherLink[] getTeacherlinks() {
        return teacher;
    }

    public void setTeacherlinks(TeacherLink[] teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KosParallel that = (KosParallel) o;
        return Objects.equals(capacity, that.capacity) && Objects.equals(code, that.code) && Objects.equals(parallelType, that.parallelType) && Objects.equals(timetableSlot, that.timetableSlot) && Arrays.equals(teacher, that.teacher);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(capacity, code, parallelType, timetableSlot);
        result = 31 * result + Arrays.hashCode(teacher);
        return result;
    }

    @Override
    public String toString() {
        return "KosParallel{" +
                "capacity='" + capacity + '\'' +
                ", code='" + code + '\'' +
                ", parallelType='" + parallelType + '\'' +
                ", timetableSlot=" + timetableSlot +
                ", teacher=" + Arrays.toString(teacher) +
                '}';
    }
}
