package cz.cvut.kbss.ear.project.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
public class CourseTeacher extends CourseParticipant {

    @Override
    public void enrollInParallel(Parallel parallel) {
        parallel.enrollParticipant(this);
    }

    @Override
    public void unenrollFromParallel(Parallel parallel) {
        parallel.unenrollParticipant(this);
    }

    @Override
    public void unenrollFromCourse() {
        getCourse().getTeachers().remove(this);
        setCourse(null);
    }
}
