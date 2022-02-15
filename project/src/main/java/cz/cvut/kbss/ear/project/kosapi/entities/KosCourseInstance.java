package cz.cvut.kbss.ear.project.kosapi.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import cz.cvut.kbss.ear.project.kosapi.links.TeacherLink;

public class KosCourseInstance {

    @JacksonXmlProperty(localName = "instructors")
    private TeacherLink[] instructors;

    @JacksonXmlProperty(localName = "lecturers")
    private TeacherLink[] lecturers;

    @JacksonXmlProperty(localName = "semester")
    private String semester;

    public TeacherLink[] getInstructors() {
        return instructors;
    }

    public void setInstructors(TeacherLink[] instructors) {
        this.instructors = instructors;
    }

    public TeacherLink[] getLecturers() {
        return lecturers;
    }

    public void setLecturers(TeacherLink[] lecturers) {
        this.lecturers = lecturers;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSemesterCode(){
        StringBuilder builder = new StringBuilder();
        String year = semester.split(" ")[1].split("/")[0];
        builder.append("B");
        builder.append(year.charAt(2));
        builder.append(year.charAt(3));

        if (semester.charAt(0) == 'Z'){
            builder.append("1");
        }

        else{
            builder.append("2");
        }

        return builder.toString();
    }
}
