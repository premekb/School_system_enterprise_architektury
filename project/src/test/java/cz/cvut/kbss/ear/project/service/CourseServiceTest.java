package cz.cvut.kbss.ear.project.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import cz.cvut.kbss.ear.project.exception.CourseException;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    public void createNewCourse_createTwoCoursesWithSameCode_CourseException() {
        courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT);

        assertThrows(CourseException.class,
            () -> courseService.createNewCourse("EAR", 5, "B36EAR", CourseCompletionType.CLFD_CREDIT));
    }
}
