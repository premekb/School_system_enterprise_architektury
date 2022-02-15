package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.CourseTeacher;
import cz.cvut.kbss.ear.project.model.User;
import java.util.Collection;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class CourseTeacherDao extends BaseDao<CourseTeacher> {

    public CourseTeacherDao() {
        super(CourseTeacher.class);
    }

    public Collection<CourseTeacher> findAllByUser(User user) {
        try {
            return em.createNamedQuery("CourseTeacher.findByUser", CourseTeacher.class)
                .setParameter("user", user)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
