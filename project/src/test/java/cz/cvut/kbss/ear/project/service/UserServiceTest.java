package cz.cvut.kbss.ear.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cz.cvut.kbss.ear.project.dao.UserDao;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.exception.UserException;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseStudent;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;
import cz.cvut.kbss.ear.project.model.enums.SemesterState;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserDao userDao;

    @Test
    public void createUser_addValidUser_userAdded() {
        final User user = Generator.generateUser();
        userService.persist(user);

        assertNotNull(userDao.findByUsername(user.getUsername()));
    }

    @Test
    public void createUser_addDuplicateUsernames_exceptionThrown() {
        final User user = Generator.generateUser();
        userService.persist(user);

        assertThrows(UserException.class, () -> userService.persist(user));
    }

    @Test
    public void getTimetableInSemester_userEnrolledInParallels_timetableReturned() {
        final User user = Generator.generateUser();
        em.persist(user);

        final Course course = new Course();
        course.setName("EAR");
        course.setCredits(5);
        course.setCode("B36EAR");
        course.setCompletionType(CourseCompletionType.CLFD_CREDIT);
        em.persist(course);

        final Semester semester = new Semester();
        semester.setCode("B211");
        semester.setYear("2021");
        semester.setState(SemesterState.PREPARATION);
        semester.setType(SemesterType.WINTER);
        em.persist(semester);

        final Parallel parallel1 = new Parallel();
        parallel1.setName("C101");
        parallel1.setStartTime(new Time(16, 15, 0));
        parallel1.setEndTime(new Time(17, 45, 0));
        parallel1.setDayOfWeek(DayOfWeek.WEDNESDAY);
        parallel1.setCapacity(20);
        parallel1.setParallelType(ParallelType.TUTORIAL);

        final Parallel parallel2 = new Parallel();
        parallel2.setName("C102");
        parallel2.setStartTime(new Time(11, 0, 0));
        parallel2.setEndTime(new Time(12, 30, 0));
        parallel2.setDayOfWeek(DayOfWeek.WEDNESDAY);
        parallel2.setCapacity(20);
        parallel2.setParallelType(ParallelType.TUTORIAL);

        final CourseInSemester courseInSemester = new CourseInSemester();
        courseInSemester.setCourse(course);
        courseInSemester.setSemester(semester);
        courseInSemester.addParallel(parallel2);
        em.persist(courseInSemester);

        final CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourse(courseInSemester);
        courseStudent.setUser(user);
        em.persist(courseStudent);

        courseStudent.enrollInParallel(parallel1);
        courseStudent.enrollInParallel(parallel2);
        em.persist(parallel1);
        em.persist(parallel2);

        final Collection<Parallel> result = userService.getTimetableInSemester(user);
        assertEquals(2, result.size());
    }
}
