package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.enviroment.Environment;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.kosapi.entities.KosParallel;
import cz.cvut.kbss.ear.project.kosapi.entities.KosStudent;
import cz.cvut.kbss.ear.project.kosapi.entities.KosTeacher;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class KosapiServiceTest {

    @Autowired
    KosapiService kosapiService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = Generator.generateUser();
        user.setRole(Role.ADMIN);
        Environment.setCurrentUser(user);
    }

    @Test
    public void getCourseInSemester_getB6B36EAR_courseFetchedFromKosAPI(){
        String code = "B6B36EAR";
        String semesterCode = "B211";
        KosCourse course = kosapiService.getCourseInSemester(code, semesterCode);

        Assertions.assertNotNull(course);
    }

    @Test
    public void getCourseInSemester_tryToGetNotExistingCourse_httpClientExceptionThrown(){
        String code = "wieGehtsMann?";
        String semesterCode = "B211";
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> kosapiService.getCourseInSemester(code, semesterCode));
    }

    @Test
    public void getAllCoursesInSemester_notExistingSemester_httpClientExceptionThrown(){
        String semester_code = "yoyoyoMann";
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> kosapiService.getAllCoursesInSemester(semester_code));
    }

    @Test
    @Disabled // The test takes a minute to execute
    public void getAllCoursesInSemester_B211semester_coursesFetchedFromKosAPI(){
        String semester_code = "B211";

        List<KosCourse> courses = kosapiService.getAllCoursesInSemester(semester_code);

        Assertions.assertTrue(courses.size() > 2000);
    }

    @Test
    public void getTeachersInCourse_getEARInB211_LedvinkaInTeachers(){
        String code = "B6B36EAR";
        String semesterCode = "B211";
        Boolean ledvinkaFound = false;
        KosCourse kosCourse = kosapiService.getCourseInSemester(code, semesterCode);

        List<KosTeacher> teachers = kosapiService.getTeachersInCourse(kosCourse);

        for (KosTeacher teacher : teachers){
            if (teacher.getLastName().equals("Ledvinka")) ledvinkaFound = true;
        }
        Assertions.assertTrue(ledvinkaFound);
    }

    @Test
    public void getParallelsInCourse_getEARInB211Parallels_103ParallelInKNE328(){
        String code = "B6B36EAR";
        String semesterCode = "B211";
        boolean parallelFound = false;

        List<KosParallel> parallels = kosapiService.getParallelsInCourse(code, semesterCode);

        for (KosParallel parallel : parallels){
            if (parallel.getCode().equals("103")) parallelFound = true;
        }
        Assertions.assertTrue(parallelFound);
    }

    @Test
    public void getTeachersInParallel_getTeachersFromEARLecture_kremenAndAubrechtInTeachers(){
        String code = "B6B36EAR";
        String semesterCode = "B211";
        boolean aubrechtFound = false;
        boolean kremenFound = false;
        List<KosParallel> parallels = kosapiService.getParallelsInCourse(code, semesterCode);
        KosParallel lecture = null;
        for (KosParallel parallel : parallels){
            if (parallel.getParallelType().equals("LECTURE")) lecture = parallel;
        }

        List<KosTeacher> kosTeachers = kosapiService.getTeachersInParallel(lecture);

        for (KosTeacher kosTeacher : kosTeachers){
            if (kosTeacher.getUsername().equals("aubrech")) aubrechtFound = true;
            if (kosTeacher.getUsername().equals("kremep1")) kremenFound = true;
        }

        Assertions.assertTrue(aubrechtFound);
        Assertions.assertTrue(kremenFound);
    }

    @Test
    public void getStudentsInParallel_getStudentsInEARParallel103_belkapreInStudents(){
        String courseCode = "B6B36EAR";
        String semesterCode = "B211";
        boolean belkapreFound = false;
        List<KosParallel> parallels = kosapiService.getParallelsInCourse(courseCode, semesterCode);
        KosParallel parallel103 = null;
        for (KosParallel parallel : parallels){
            if (parallel.getCode().equals("103")) parallel103 = parallel;
        }

        List<KosStudent> students = kosapiService.getStudentsInParallel(parallel103);

        for (KosStudent student : students){
            if (student.getUsername().equals("belkapre")) belkapreFound = true;
        }

        Assertions.assertTrue(belkapreFound);
    }

    @Test
    public void gestStudentsInCourse_getStudentsInEAR_plocivojInStudents(){
        String courseCode = "B6B36EAR";
        String semesterCode = "B211";
        boolean plocivojFound = false;
        KosCourse kosCourse = kosapiService.getCourseInSemester(courseCode, semesterCode);

        List<KosStudent> students = kosapiService.getStudentsInCourse(kosCourse);

        for (KosStudent student : students){
            if (student.getUsername().equals("plocivoj")) plocivojFound = true;
        }

        Assertions.assertTrue(plocivojFound);
    }
}
