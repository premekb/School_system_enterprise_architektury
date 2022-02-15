package cz.cvut.kbss.ear.project.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cz.cvut.kbss.ear.project.Application;
import cz.cvut.kbss.ear.project.config.KosApiConfig;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.kosapi.oauth2.TokenManager;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.rest.controllers.*;
import cz.cvut.kbss.ear.project.service.CourseInSemesterService;
import cz.cvut.kbss.ear.project.service.CourseSynchronisationService;
import cz.cvut.kbss.ear.project.service.KosapiService;
import cz.cvut.kbss.ear.project.service.ParallelService;
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
public class UserDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserDao dao;

    @Test
    public void findByUsername_userExists_returnsUserWithUsername() {
        final User user = Generator.generateUser();
        em.persist(user);

        final User result = dao.findByUsername(user.getUsername());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }
}
