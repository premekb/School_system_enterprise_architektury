package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.Classroom;
import cz.cvut.kbss.ear.project.model.Course;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class ClassroomDao extends BaseDao<Classroom> {

    public ClassroomDao() {
        super(Classroom.class);
    }

    public Classroom findByName(String name){
        try {
            return em.createNamedQuery("Classroom.findByName", Classroom.class).setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean exists(String name){
        return findByName(name) != null;
    }
}
