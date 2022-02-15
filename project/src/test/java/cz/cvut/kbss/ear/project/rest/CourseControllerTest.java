package cz.cvut.kbss.ear.project.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.project.enviroment.Generator;
import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.rest.controllers.CourseController;
import cz.cvut.kbss.ear.project.rest.util.Code;
import cz.cvut.kbss.ear.project.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest extends BaseControllerTestRunner{

    @Mock
    private CourseService courseService;

    @Mock
    private CourseInSemesterService courseInSemesterService;

    @Mock
    private SemesterService semesterService;

    @Mock
    private ParallelService parallelService;

    @Mock
    private KosapiService kosapiService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setup(){
        super.setUp(courseController);
    }

    @Test
    public void getCourseInSemester_mockCourseInSemester_findByCodeCalledOnServiceWithCorrectArguments() throws Exception {
        final Semester semester = new Semester();
        semester.setCode("B211");
        final Course course = new Course();
        course.setId(Generator.randomInt());
        course.setName("course");
        course.setCode("TESTCODE");
        final CourseInSemester courseInSemester = new CourseInSemester();
        courseInSemester.setSemester(semester);
        courseInSemester.setCourse(course);

        when(courseInSemesterService.findByCode(course.getCode(), semester.getCode())).thenReturn(courseInSemester);
        final MvcResult mvcResult = mockMvc.perform(get("/api/courses/" + course.getCode() + "/" + semester.getCode())).andReturn();

        final CourseInSemester result = readValue(mvcResult, CourseInSemester.class);
        assertNotNull(result);
        assertEquals(courseInSemester.getCourse(), result.getCourse());
        assertEquals(courseInSemester.getSemester(), result.getSemester());
    }

    @Test
    public void getCourses_mockCourses_findAllCalledOnServiceAndmockedCoursesReturned() throws Exception {
        final List<Course> courses = IntStream.range(0, 5).mapToObj(i -> {
            final Course course = new Course();
            course.setName("Course" + i);
            course.setCode("Course" + i);
            course.setId(Generator.randomInt());
            return course;
        }).collect(Collectors.toList());
        when(courseService.findAll()).thenReturn(courses);

        final MvcResult mvcResult = mockMvc.perform(get("/api/courses")).andReturn();
        final List<Course> result = readValue(mvcResult, new TypeReference<List<Course>>() {
        });
        assertEquals(courses, result);
        verify(courseService).findAll();
    }

    @Test
    public void getCourseByCode_mockCourse_getCourseWithMockedCourseCodeCalledOnService() throws Exception {
        final Course course = new Course();
        course.setId(Generator.randomInt());
        course.setName("course");
        course.setCode("TESTCODE");
        when(courseService.findByCode(course.getCode())).thenReturn(course);
        final MvcResult mvcResult = mockMvc.perform(get("/api/courses/" + course.getCode())).andReturn();

        final Course result = readValue(mvcResult, Course.class);
        assertNotNull(result);
        assertEquals(course, result);
    }

    @Test
    public void createCourse_createCustomCourse_courseCreatedCalledOnService() throws Exception {
        final Course toCreate = new Course();
        toCreate.setName("Custom course");
        toCreate.setCredits(4);
        toCreate.setDescription("Custom course desc");
        toCreate.setCompletionType(CourseCompletionType.CLFD_CREDIT);
        toCreate.setCode("CUSTOMCODE");

        mockMvc.perform(post("/api/courses").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        final ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseService).persist(captor.capture());
        assertEquals(toCreate, captor.getValue());
    }

    @Test
    public void createCourseFromKos_createEAR_courseCreatedCalledOnService() throws Exception {
        final String code = "B6B36EAR";
        final Code codeObject = new Code();
        codeObject.setCode(code);
        KosCourse kosCourse = new KosCourse();
        kosCourse.setCode(code);
        kosCourse.setCredits("5");
        kosCourse.setName(new String[]{"ear", "ear"});
        kosCourse.setCompletion("CREDIT");
        when(kosapiService.getCourse(code)).thenReturn(kosCourse);

        mockMvc.perform(post("/api/courses/kos").content(toJson(codeObject)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        final ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseService).persist(captor.capture());
        assertEquals(kosCourse.getCode(), captor.getValue().getCode());
        assertEquals(Integer.parseInt(kosCourse.getCredits()), captor.getValue().getCredits());
        assertEquals(kosCourse.getName(), captor.getValue().getName());
        assertEquals(kosCourse.getCompletion(), captor.getValue().getCompletionType().toString());
    }

    @Test
    public void createCourseInSemester_createMockCourseInMockSemester_serviceCalledWithCorrectArgumentsAndCreatedResponse() throws Exception {
        final String semesterCode = "B211";
        final String courseCode = "B6B36EAR";
        Semester semesterMock = mock(Semester.class);
        Course courseMock = mock(Course.class);
        CourseInSemester courseInSemesterMock = mock(CourseInSemester.class);
        when(courseService.findByCode(courseCode)).thenReturn(courseMock);
        when(semesterService.findByCode(semesterCode)).thenReturn(semesterMock);
        when(courseInSemesterService.addCourseToSemester(courseMock, semesterMock)).thenReturn(courseInSemesterMock);

        mockMvc.perform(post("/api/courses/" + courseCode + "/" + semesterCode).content("").contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        final ArgumentCaptor<Course> captorCourse = ArgumentCaptor.forClass(Course.class);
        final ArgumentCaptor<Semester> captorSemester = ArgumentCaptor.forClass(Semester.class);
        verify(courseInSemesterService).addCourseToSemester(captorCourse.capture(), captorSemester.capture());
        assertEquals(courseMock, captorCourse.getValue());
        assertEquals(semesterMock, captorSemester.getValue());
    }
}
