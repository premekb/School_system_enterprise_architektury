package cz.cvut.kbss.ear.project.model;

import com.sun.istack.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.persistence.*;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "CourseInSemester.findCourseInSemester",
        query = "SELECT c FROM CourseInSemester c WHERE c.semester = :semester AND c.course = :course"
    ),
    @NamedQuery(
            name = "CourseInSemester.findStudents",
            query = "SELECT c FROM CourseStudent c WHERE c.course = :course"
    ),
    @NamedQuery(
            name = "CourseInSemester.findTeachers",
            query = "SELECT c FROM CourseTeacher c WHERE c.course = :course"
    ),
    @NamedQuery(
            name = "CourseInSemester.findAllParticipants",
            query = "SELECT c FROM CourseParticipant c WHERE c.course = :course"
    ),
    @NamedQuery(
            name = "CourseInSemester.findParticipantByUser",
            query = "SELECT c FROM CourseParticipant c WHERE c.course = :course AND c.user = :user"
    ),
    @NamedQuery(name = "CourseInSemester.findUsersCoursesInSemester", query = "SELECT cis FROM CourseInSemester cis" +
            " JOIN CourseParticipant cp ON cp.course = cis WHERE cp.user = :user AND cis.semester = :semester"),
    @NamedQuery(
        name = "CourseInSemester.findCoursesInSemester",
        query = "SELECT c FROM CourseInSemester c WHERE c.semester = :semester"
    )
})
public class CourseInSemester extends AbstractEntity {

    @ManyToOne(optional = false)
    @NotNull
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    private Semester semester;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CourseTeacher> teachers = new HashSet<>();

    @OrderBy
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Parallel> parallels = new HashSet<>();

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

    public Collection<CourseTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Collection<CourseTeacher> teachers) {
        this.teachers = teachers;
    }

    public Collection<Parallel> getParallels() {
        return parallels;
    }

    public void setParallels(Collection<Parallel> parallels) {
        this.parallels = parallels;
    }

    public void addTeacher(CourseTeacher teacher) {
        teachers.add(teacher);
        teacher.setCourse(this);
    }

    public void addParallel(Parallel parallel) {
        parallels.add(parallel);
        parallel.setCourseInSemester(this);
    }

    public void removeParallel(Parallel parallel) {
        parallels.remove(parallel);
        parallel.setCourseInSemester(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseInSemester that = (CourseInSemester) o;
        return Objects.equals(course, that.course) && Objects.equals(semester, that.semester) && Objects.equals(teachers, that.teachers) && Objects.equals(parallels, that.parallels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, semester, teachers, parallels);
    }
}
