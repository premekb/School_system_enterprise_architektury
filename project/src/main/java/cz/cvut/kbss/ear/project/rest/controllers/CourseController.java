package cz.cvut.kbss.ear.project.rest.controllers;

import cz.cvut.kbss.ear.project.exception.CourseException;
import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.exception.SemesterException;
import cz.cvut.kbss.ear.project.kosapi.entities.KosCourse;
import cz.cvut.kbss.ear.project.model.*;
import cz.cvut.kbss.ear.project.rest.dto.CourseInSemesterDTO;
import cz.cvut.kbss.ear.project.rest.dto.ParallelDTO;
import cz.cvut.kbss.ear.project.rest.dto.UsernameDTO;
import cz.cvut.kbss.ear.project.rest.util.Code;
import cz.cvut.kbss.ear.project.rest.util.RestUtils;
import cz.cvut.kbss.ear.project.service.*;
import cz.cvut.kbss.ear.project.service.util.KosapiEntityConverter;
import cz.cvut.kbss.ear.project.service.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static cz.cvut.kbss.ear.project.rest.util.DTOConverter.dtoToParallel;

/**
 * Resources
 * /api/courses DONE
 * - POST: DONE
 *      - manualni vytvoreni, bude brat Course entity DONE
 * /api/courses/kos (Tady neni GET)
 * - POST: DONE
 *     - import z kosu DONE
 * /api/courses/code DONE
 * /api/courses/code/semesterCode DONE
 * - POST: DONE
 *    - vytvor instanci kurzu v semestru na zaklade course DONE
 *
 * - /api/courses/code/semesterCode/kos DONE
 * - POST:
 *     - import veci z KOSu DONE
 * /api/courses/code/semesterCode/participants DONE
 * - DELETE:
 *       - unenrol DONE
 * /api/courses/code/semesterCode/participants/students DONE
 * - POST:
 *      - enrol DONE
 * - DELETE:
 *      - unenrol DONE
 * /api/courses/code/semesterCode/participants/teachers DONE
 * - POST:
 *      - enrol DONE
 * - DELETE:
 *      - unenrol DONE
 * /api/courses/code/semesterCode/parallels DONE
 * - POST:
 *     - create parallel DONE
 *
 **/
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    private final CourseInSemesterService courseInSemesterService;

    private final CourseSynchronisationService courseSynchronisationService;

    private final SemesterService semesterService;

    private final ParallelService parallelService;

    private final KosapiService kosapiService;

    private final UserService userService;

    public CourseController(CourseService courseService, CourseInSemesterService courseInSemesterService,
                            SemesterService semesterService, ParallelService parallelService, KosapiService kosapiService,
                            CourseSynchronisationService courseSynchronisationService, UserService userService) {
        this.courseService = courseService;
        this.courseInSemesterService = courseInSemesterService;
        this.semesterService = semesterService;
        this.parallelService = parallelService;
        this.kosapiService = kosapiService;
        this.courseSynchronisationService = courseSynchronisationService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Course> getCourses() {
        return courseService.findAll();
    }

    @GetMapping(value = "/{code}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Course getCourseByCode(@PathVariable String code) {
        Course course = courseService.findByCode(code);
        if (course == null){
            throw NotFoundException.create("Course", code);
        }
        return course;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{courseCode}/{semesterCode}/participants",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUsersInCourse(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        return courseInSemesterService.getAllParticipants(courseInSemester)
                .stream()
                .map((CourseParticipant::getUser))
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{courseCode}/{semesterCode}/participants/students",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getStudentsInCourse(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        return courseInSemesterService.getStudents(courseInSemester)
                .stream()
                .map((CourseParticipant::getUser))
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{courseCode}/{semesterCode}/participants/teachers",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getTeachersIncourse(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        return courseInSemesterService.getTeachers(courseInSemester)
                .stream()
                .map((CourseParticipant::getUser))
                .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{courseCode}/{semesterCode}/parallels", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParallelDTO> getParallelsInCourse(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        return courseInSemesterService.getParallels(courseInSemester)
                .stream()
                .map(ParallelDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{courseCode}/{semesterCode}",produces = MediaType.APPLICATION_JSON_VALUE)
    public CourseInSemesterDTO getCourseInSemester(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        return new CourseInSemesterDTO(courseInSemester);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @PostMapping(value = "/{courseCode}/{semesterCode}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCourseInSemester(@PathVariable String courseCode, @PathVariable String semesterCode) {
        Course course = tryToFindCourse(courseCode);
        Semester semester = tryToFindSemester(semesterCode);
        courseInSemesterService.addCourseToSemester(course, semester);
        LOG.debug("Created course with {} \n " + "in semester {}.", course, semester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}/{semesterCode}", course.getCode(), semesterCode);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCourse(@RequestBody Course course) {
        courseService.persist(course);
        LOG.debug("Created course {}.", course);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", course.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/kos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCourseFromKos(@RequestBody Code code) {
        KosCourse kosCourse = kosapiService.getCourse(code.getCode());
        courseService.persist(KosapiEntityConverter.kosCourseToCourse(kosCourse));
        LOG.debug("Created course from kos course {}.", kosCourse);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", kosCourse.getCode()); // TODO incorrect URI
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/{courseCode}/{semesterCode}/kos")
    public ResponseEntity<Void> synchroniseCourseInSemesterWithKos(@PathVariable String courseCode, @PathVariable String semesterCode) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        courseSynchronisationService.synchroniseWithKos(courseInSemester);
        LOG.debug("Synchronised course {}.", courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{courseCode}/{semesterCode}/parallels", courseCode, semesterCode);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @PostMapping(value = "/{courseCode}/{semesterCode}/participants/teachers")
    public ResponseEntity<Void> enrolAsTeacher(@PathVariable String courseCode, @PathVariable String semesterCode, @RequestBody UsernameDTO usernameDTO) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        User user = tryToFindUser(usernameDTO.getUsername());
        courseInSemesterService.enrolAsTeacherInCourse(user, courseInSemester);
        LOG.debug("Enroled {} as teacher in course {}.", user.getUsername(), courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{courseCode}/{semesterCode}/participants/teachers", courseCode, semesterCode);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @PostMapping(value = "/{courseCode}/{semesterCode}/participants/students")
    public ResponseEntity<Void> enrolAsStudent(@PathVariable String courseCode, @PathVariable String semesterCode, @RequestBody UsernameDTO usernameDTO) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        User user = tryToFindUser(usernameDTO.getUsername());
        courseInSemesterService.enrolAsStudentInCourse(user, courseInSemester);
        LOG.debug("Enroled {} as student in course {}.", user.getUsername(), courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{courseCode}/{semesterCode}/participants/students", courseCode, semesterCode);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @DeleteMapping(value = {"/{courseCode}/{semesterCode}/participants/teachers", "/{courseCode}/{semesterCode}/participants/students", "/{courseCode}/{semesterCode}/participants"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenrolFromCourse(@PathVariable String courseCode, @PathVariable String semesterCode, @RequestBody UsernameDTO usernameDTO) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        User user = tryToFindUser(usernameDTO.getUsername());
        courseInSemesterService.unenrolFromCourse(user, courseInSemester);
        LOG.debug("Unenroled {} from course {}.", user.getUsername(), courseInSemester);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{courseCode}/{semesterCode}/parallels")
    public ResponseEntity<Void> createParallel(@PathVariable String courseCode, @PathVariable String semesterCode, @RequestBody ParallelDTO parallelDTO) {
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        Parallel parallel = dtoToParallel(parallelDTO);
        parallelService.addParallelToCourse(parallel, courseInSemester);
        LOG.debug("Added parallel {} to course {}.", parallel, courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{courseCode}/{semesterCode}/parallels/{parallelId}", courseCode, semesterCode, parallel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    private User tryToFindUser(String username){
        User user = userService.findByUsername(username);
        if (user == null){
            throw NotFoundException.create("User", username);
        }
        return user;
    }

    private Semester tryToFindSemester(String semesterCode){
        Semester semester = semesterService.findByCode(semesterCode);
        if (semester == null){
            throw new NotFoundException("Semester with a given code does not exist.");
        }
        return semester;
    }

    private Course tryToFindCourse(String courseCode){
        Course course = courseService.findByCode(courseCode);
        if (course == null){
            throw new NotFoundException("Course with code " + courseCode + " does not exist, therefore an instance in a semester" +
                    "cannot be created. Create the course first by POST on api/courses.");
        }
        return course;
    }
}
