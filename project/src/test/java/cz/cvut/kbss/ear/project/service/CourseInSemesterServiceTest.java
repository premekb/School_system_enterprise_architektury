package cz.cvut.kbss.ear.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cz.cvut.kbss.ear.project.dao.CourseInSemesterDao;
import cz.cvut.kbss.ear.project.dao.CourseStudentDao;
import cz.cvut.kbss.ear.project.dao.CourseTeacherDao;
import cz.cvut.kbss.ear.project.enviroment.Environment;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.exception.CourseException;
import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;
import cz.cvut.kbss.ear.project.model.enums.Role;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.cvut.kbss.ear.project.service.util.KosapiEntityConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class CourseInSemesterServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CourseInSemesterService courseInSemesterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseInSemesterDao dao;

    @Autowired
    private CourseTeacherDao courseTeacherDao;

    @Autowired
    private CourseStudentDao courseStudentDao;

    @Autowired
    private ParallelService parallelService;

    @BeforeEach
    public void setUp() {
        final User user = Generator.generateUser();
        user.setRole(Role.ADMIN);
        Environment.setCurrentUser(user);
    }

    @Test
    public void createNewCourseInSemester_createTwoInstancesInOneSemester_exceptionThrown() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);

        courseInSemesterService.addCourseToSemester(course, semester);
        assertThrows(CourseException.class,
            () -> courseInSemesterService.addCourseToSemester(course, semester));
    }

    @Test
    public void enrolTeacherInCourse_createCourseTeacher_courseTeacherCreated() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        User user = Generator.generateUser();
        userService.persist(user);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);

        courseInSemesterService.enrolAsTeacherInCourse(user, courseInSemester);
        assertTrue(courseTeacherDao.findAllByUser(user).stream().findFirst().isPresent());
        assertEquals(courseTeacherDao.findAllByUser(user).stream().findFirst().get().getCourse(),
            courseInSemester);
        assertNotNull(dao.findCourseInSemester(course, semester).getTeachers().stream()
            .filter(t -> t.getUser().equals(user)).findFirst());
    }

    @Test
    public void enrolStudentInCourse_createCourseStudent_courseStudentCreated() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        User user = Generator.generateUser();
        userService.persist(user);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);

        courseInSemesterService.enrolAsStudentInCourse(user, courseInSemester);
        assertTrue(courseStudentDao.findAllByUser(user).stream().findFirst().isPresent());
        assertEquals(courseStudentDao.findAllByUser(user).stream().findFirst().get().getCourse(),
            courseInSemester);
    }

    @Test
    void unenrolFromCourse_unenrollCourseStudent_courseStudentUnenrolled() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        User user = Generator.generateUser();
        userService.persist(user);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);
        courseInSemesterService.enrolAsStudentInCourse(user, courseInSemester);

        courseInSemesterService.unenrolFromCourse(user, courseInSemester);
        assertEquals(0, courseStudentDao.findAllByUser(user).size());
    }

    @Test
    void unenrolFromCourse_unenrollCourseTeacher_courseTeacherUnenrolled() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        User user = Generator.generateUser();
        userService.persist(user);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);
        courseInSemesterService.enrolAsTeacherInCourse(user, courseInSemester);

        courseInSemesterService.unenrolFromCourse(user, courseInSemester);

        assertEquals(0, courseTeacherDao.findAllByUser(user).size());
        assertEquals(0, courseInSemester.getTeachers().size());
    }

    @Test
    void isUserEnroled_enrolledTeacher_true() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        User user = Generator.generateUser();
        userService.persist(user);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);
        courseInSemesterService.enrolAsTeacherInCourse(user, courseInSemester);

        final boolean result = courseInSemesterService.isUserEnroled(user, courseInSemester);
        assertTrue(result);
    }

    @Test
    void getParallels_courseWithParallels_returnsParallels() {
        Course course = courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
            course, semester);
        Parallel parallel = new Parallel();
        parallel.setName("C101");
        parallel.setStartTime(new Time(16, 15, 0));
        parallel.setEndTime(new Time(17, 45, 0));
        parallel.setDayOfWeek(DayOfWeek.WEDNESDAY);
        parallel.setCapacity(20);
        parallel.setParallelType(ParallelType.TUTORIAL);
        em.persist(parallel);
        courseInSemester.addParallel(parallel);
        em.merge(courseInSemester);

        assertEquals(List.of(parallel), courseInSemesterService.getParallels(courseInSemester));
    }

    @Test
    void getAllUsersCoursesInSemester_addEARB211AndEnrolUser_EARB211AmongUsersCourses() {
        Course course = courseService.createNewCourse("EAR", 5, "B6B36EAR", CourseCompletionType.CLFD_CREDIT);
        Semester semester = semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        CourseInSemester courseInSemester = courseInSemesterService.addCourseToSemester(
                course,
                semester
        );
        User user = Generator.generateUser();
        userService.persist(user);
        courseInSemesterService.enrolAsStudentInCourse(user, courseInSemester);

        List<CourseInSemester> result = courseInSemesterService.getAllUsersCoursesInSemester(semester, user);

        Assertions.assertEquals(true, result.contains(courseInSemester));
    }
}
