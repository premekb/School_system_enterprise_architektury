package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.CourseStudent;
import cz.cvut.kbss.ear.project.model.User;
import java.util.Collection;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class CourseStudentDao extends BaseDao<CourseStudent> {

    public CourseStudentDao() {
        super(CourseStudent.class);
    }

    public Collection<CourseStudent> findAllByUser(User user) {
        try {
            return em.createNamedQuery("CourseStudent.findByUser", CourseStudent.class)
                .setParameter("user", user)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
