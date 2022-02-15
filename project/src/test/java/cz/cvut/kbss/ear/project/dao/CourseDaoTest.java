package cz.cvut.kbss.ear.project.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cz.cvut.kbss.ear.project.Application;
import cz.cvut.kbss.ear.project.config.KosApiConfig;
import cz.cvut.kbss.ear.project.kosapi.oauth2.TokenManager;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.rest.controllers.*;
import cz.cvut.kbss.ear.project.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@DataJpaTest
@ComponentScan(basePackageClasses = Application.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                KosApiConfig.class, TokenManager.class, KosapiService.class, CourseController.class,
                UserController.class, ParallelService.class, CourseInSemesterService.class,
                CourseSynchronisationService.class, MyController.class, ParallelController.class,
                CourseInSemesterService.class, SemesterController.class
        }))
public class CourseDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private CourseService courseService;

    @Test
    public void findByCode_courseExists_returnsCourseWithMatchingCode() {
        courseService.createNewCourse("Kurz", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);

        final Course result = courseDao.findByCode("B36EAR");

        assertNotNull(result);
        assertEquals("B36EAR", result.getCode());
    }
}
