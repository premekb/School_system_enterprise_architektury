package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.dao.CourseDao;
import cz.cvut.kbss.ear.project.exception.CourseException;
import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseDao dao;

    public CourseService(CourseDao courseDao) {
        this.dao = courseDao;
    }

    @Transactional
    @Deprecated // Dont use, use persist instead, this does not contain all course attributes
    public Course createNewCourse(
        String name, Integer credits, String code,
        CourseCompletionType completionType
    ) {
        if (dao.findByCode(code) != null) {
            throw new CourseException("Course with this code already exists");
        }

        Course course = new Course();
        course.setName(name);
        course.setCredits(credits);
        course.setCode(code);
        course.setCompletionType(completionType);
        dao.persist(course);

        return course;
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Course find(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    public Course findByCode(String code){
        return dao.findByCode(code);
    }

    @Transactional
    public void persist(Course course) {
        Objects.requireNonNull(course);
        if (dao.findByCode(course.getCode()) != null) {
            throw new CourseException("Course with this code already exists");
        }
        dao.persist(course);
    }
}
