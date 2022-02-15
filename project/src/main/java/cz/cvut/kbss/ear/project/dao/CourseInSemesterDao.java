package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.*;

import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class CourseInSemesterDao extends BaseDao<CourseInSemester> {

    public CourseInSemesterDao() {
        super(CourseInSemester.class);
    }

    public CourseInSemester findCourseInSemester(Course course, Semester semester) {
        try {
            return em.createNamedQuery("CourseInSemester.findCourseInSemester",
                    CourseInSemester.class)
                .setParameter("course", course)
                .setParameter("semester", semester)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CourseStudent> findStudents(CourseInSemester course){
        try {
            return em.createNamedQuery("CourseInSemester.findStudents",
                    CourseStudent.class)
                    .setParameter("course", course)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CourseTeacher> findTeachers(CourseInSemester course){
        try {
            return em.createNamedQuery("CourseInSemester.findTeachers",
                    CourseTeacher.class)
                    .setParameter("course", course)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CourseParticipant> findAllParticipants(CourseInSemester course){
        try {
            return em.createNamedQuery("CourseInSemester.findAllParticipants",
                    CourseParticipant.class)
                    .setParameter("course", course)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CourseParticipant findParticipant(CourseInSemester course, User user){
        try {
            return em.createNamedQuery("CourseInSemester.findParticipantByUser",
                    CourseParticipant.class)
                    .setParameter("course", course)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CourseInSemester> findUsersCourses(Semester semester, User user){
        try {
            return em.createNamedQuery("CourseInSemester.findUsersCoursesInSemester",
                    CourseInSemester.class)
                    .setParameter("user", user)
                    .setParameter("semester", semester)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CourseInSemester> findCoursesCoursesInSemester(Semester semester) {
        try {
            return em.createNamedQuery("CourseInSemester.findCoursesInSemester",
                CourseInSemester.class)
                .setParameter("semester", semester)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
