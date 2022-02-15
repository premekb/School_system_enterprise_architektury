package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import cz.cvut.kbss.ear.project.service.util.KosapiEntityConverter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@Disabled // Takes too much time
public class CourseSynchronisationServiceTest {
    @Autowired
    private CourseSynchronisationService courseSynchronisationService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private CourseInSemesterService courseInSemesterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private KosapiService kosapiService;

    @Test
    public void synchroniseWithKos_synchroniseEARinB211_noExceptionThrown(){
        semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        KosCourse kosCourse = kosapiService.getCourse("B6B36EAR");
        courseService.persist(KosapiEntityConverter.kosCourseToCourse(kosCourse));
        courseInSemesterService.addCourseToSemester(
                courseService.findByCode("B6B36EAR"),
                semesterService.findByCode("B211")
        );
        CourseInSemester courseInSemester = courseInSemesterService.findByCode("B6B36EAR", "B211");

        courseSynchronisationService.synchroniseWithKos(courseInSemester);
    }
}
