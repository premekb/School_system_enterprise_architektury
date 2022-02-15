package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.enums.SemesterState;
import java.util.List;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class SemesterDao extends BaseDao<Semester> {

    public SemesterDao() {
        super(Semester.class);
    }

    public List<Semester> findByState(SemesterState semesterState) {
        try {
            return em.createNamedQuery("Semester.findByState", Semester.class)
                .setParameter("semesterState", semesterState)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Semester findByCode(String code) {
        try {
            return em.createNamedQuery("Semester.findByCode", Semester.class)
                .setParameter("code", code)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Semester> findByYear(String year) {
        try {
            return em.createNamedQuery("Semester.findByYear", Semester.class)
                .setParameter("year", year)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
