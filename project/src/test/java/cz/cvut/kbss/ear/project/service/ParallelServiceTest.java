package cz.cvut.kbss.ear.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import cz.cvut.kbss.ear.project.enviroment.Environment;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.exception.EnrolmentException;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseStudent;
import cz.cvut.kbss.ear.project.model.CourseTeacher;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;
import cz.cvut.kbss.ear.project.model.enums.Role;
import cz.cvut.kbss.ear.project.model.enums.SemesterState;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ParallelServiceTest {

    @Autowired
    private ParallelService parallelService;

    @PersistenceContext
    private EntityManager em;

    private Parallel parallel;
    private CourseInSemester courseInSemester;
    private User user;


    @Before
    public void setUp() {
        final User user = Generator.generateUser();
        user.setRole(Role.ADMIN);
        Environment.setCurrentUser(user);
        this.user = user;
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

        final CourseInSemester courseInSemester = new CourseInSemester();
        courseInSemester.setCourse(course);
        courseInSemester.setSemester(semester);
        this.courseInSemester = courseInSemester;
        em.persist(courseInSemester);

        final Parallel parallel = new Parallel();
        parallel.setName("C101");
        parallel.setStartTime(new Time(16, 15, 0));
        parallel.setEndTime(new Time(17, 45, 0));
        parallel.setDayOfWeek(DayOfWeek.WEDNESDAY);
        parallel.setCapacity(20);
        parallel.setParallelType(ParallelType.TUTORIAL);
        this.parallel = parallel;
    }

    @Test
    public void addParallelToCourse_addParallel_parallelAdded() {
        parallelService.addParallelToCourse(parallel, courseInSemester);
        em.persist(parallel);

        final CourseInSemester result = em.find(CourseInSemester.class, courseInSemester.getId());
        assertEquals(1, result.getParallels().size());
        assertEquals(courseInSemester, parallel.getCourseInSemester());
    }

    @Test
    public void removeParallelFromCourse_removeParallel_parallelRemoved() {
        courseInSemester.addParallel(parallel);
        em.persist(parallel);

        parallelService.removeParallelFromCourse(parallel);
        final CourseInSemester result = em.find(CourseInSemester.class, courseInSemester.getId());
        assertEquals(0, result.getParallels().size());
        assertNull(parallel.getCourseInSemester());
    }

    @Test
    public void enrollInParallel_enrollStudent_studentEnrolled() {
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourse(courseInSemester);
        courseStudent.setUser(user);
        em.persist(courseStudent);

        parallelService.enrollInParallel(courseStudent, parallel);
        em.persist(parallel);

        final Parallel result = em.find(Parallel.class, parallel.getId());
        final CourseStudent resultStudent = em.find(CourseStudent.class, courseStudent.getId());
        assertEquals(1, result.getCourseStudents().size());
        assertEquals(1, resultStudent.getParallels().size());
    }

    @Mock
    Collection<CourseStudent> courseStudents;

    // I disabled this test and commented out the part of code preventing user to be enrolled in a full parallel,
    // because in KOS are parallels, which have more members than their maximum capacity, so the synchronisation would break.
    @Test
    @Ignore
    public void enrollInParallel_enrollStudentToFullParallel_throwsEnrollmentException() {
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourse(courseInSemester);
        courseStudent.setUser(user);
        em.persist(courseStudent);

        when(courseStudents.size()).thenReturn(20);
        parallel.setCourseStudents(courseStudents);

        assertThrows(EnrolmentException.class,
            () -> parallelService.enrollInParallel(courseStudent, parallel));
    }

    @Test
    public void enrollInParallel_enrollTeacher_teacherEnrolled() {
        CourseTeacher courseTeacher = new CourseTeacher();
        courseTeacher.setCourse(courseInSemester);
        courseTeacher.setUser(user);
        em.persist(courseTeacher);

        parallelService.enrollInParallel(courseTeacher, parallel);
        em.persist(parallel);

        final Parallel result = em.find(Parallel.class, parallel.getId());
        final CourseTeacher resultStudent = em.find(CourseTeacher.class, courseTeacher.getId());
        assertEquals(1, result.getCourseTeachers().size());
        assertEquals(1, resultStudent.getParallels().size());
    }

    @Test
    public void unenrollInParallel_unenrollStudent_studentUnenrolled() {
        CourseStudent courseStudent = new CourseStudent();
        courseStudent.setCourse(courseInSemester);
        courseStudent.setUser(user);
        courseStudent.getParallels().add(parallel);
        em.persist(courseStudent);
        parallel.getCourseStudents().add(courseStudent);
        em.persist(parallel);

        parallelService.unenrollFromParallel(courseStudent, parallel);

        final Parallel result = em.find(Parallel.class, parallel.getId());
        final CourseStudent resultStudent = em.find(CourseStudent.class, courseStudent.getId());
        assertEquals(0, result.getCourseStudents().size());
        assertEquals(0, resultStudent.getParallels().size());
    }

    @Test
    public void unenrollInParallel_unenrollTeacher_teacherUnenrolled() {
        CourseTeacher courseTeacher = new CourseTeacher();
        courseTeacher.setCourse(courseInSemester);
        courseTeacher.setUser(user);
        courseTeacher.getParallels().add(parallel);
        em.persist(courseTeacher);
        parallel.getCourseTeachers().add(courseTeacher);
        em.persist(parallel);

        parallelService.unenrollFromParallel(courseTeacher, parallel);

        final Parallel result = em.find(Parallel.class, parallel.getId());
        final CourseTeacher resultTeacher = em.find(CourseTeacher.class, courseTeacher.getId());
        assertEquals(0, result.getCourseTeachers().size());
        assertEquals(0, resultTeacher.getParallels().size());
    }
}
