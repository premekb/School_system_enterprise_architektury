package cz.cvut.kbss.ear.project.model;

import org.hibernate.annotations.DiscriminatorOptions;

import java.util.Collection;
import java.util.HashSet;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedQueries({
    @NamedQuery(name = "CourseStudent.findByUser", query = "SELECT cs from CourseStudent cs WHERE :user = cs.user"),
    @NamedQuery(name = "CourseTeacher.findByUser", query = "SELECT ct from CourseTeacher ct WHERE :user = ct.user"),
    @NamedQuery(name = "CourseParticipant.findByUser", query = "SELECT cp from CourseParticipant cp WHERE :user = cp.user"),
    @NamedQuery(name = "CourseParticipant.findByUserAndCourse", query = "SELECT cp from CourseParticipant cp WHERE :user = cp.user AND :course = cp.course")
})
public abstract class CourseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_participant_generator")
    @SequenceGenerator(name = "course_participant_generator",sequenceName = "course_participant_id_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private CourseInSemester course;

    @OrderBy
    @ManyToMany
    private Collection<Parallel> parallels = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Collection<Parallel> getParallels() {
        return parallels;
    }

    public void setParallels(Collection<Parallel> parallels) {
        this.parallels = parallels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CourseInSemester getCourse() {
        return course;
    }

    public void setCourse(CourseInSemester course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "CourseParticipant{" +
            "user=" + user +
            '}';
    }

    public abstract void enrollInParallel(Parallel parallel);

    public abstract void unenrollFromParallel(Parallel parallel);

    public abstract void unenrollFromCourse();
}
