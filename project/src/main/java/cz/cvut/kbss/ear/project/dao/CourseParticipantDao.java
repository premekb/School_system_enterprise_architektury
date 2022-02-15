package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseParticipant;
import cz.cvut.kbss.ear.project.model.User;
import java.util.Collection;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class CourseParticipantDao extends BaseDao<CourseParticipant> {

    public CourseParticipantDao() {
        super(CourseParticipant.class);
    }

    public Collection<CourseParticipant> findAllByUser(User user) {
        try {
            return em.createNamedQuery("CourseParticipant.findByUser", CourseParticipant.class)
                .setParameter("user", user)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CourseParticipant findByUserAndCourse(User user, CourseInSemester course) {
        try {
            return em.createNamedQuery("CourseParticipant.findByUserAndCourse",
                    CourseParticipant.class)
                .setParameter("user", user)
                .setParameter("course", course)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
