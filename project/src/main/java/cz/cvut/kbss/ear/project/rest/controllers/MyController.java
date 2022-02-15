package cz.cvut.kbss.ear.project.rest.controllers;

import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseParticipant;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.rest.dto.CourseInSemesterDTO;
import cz.cvut.kbss.ear.project.rest.dto.EnrolCourseFormDTO;
import cz.cvut.kbss.ear.project.rest.dto.EnrolParallelFormDTO;
import cz.cvut.kbss.ear.project.rest.dto.MeDTO;
import cz.cvut.kbss.ear.project.rest.dto.TimetableSlotDTO;
import cz.cvut.kbss.ear.project.rest.util.RestUtils;
import cz.cvut.kbss.ear.project.service.CourseInSemesterService;
import cz.cvut.kbss.ear.project.service.ParallelService;
import cz.cvut.kbss.ear.project.service.SemesterService;
import cz.cvut.kbss.ear.project.service.util.SecurityUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/my")
public class MyController {

    private static final Logger LOG = LoggerFactory.getLogger(MyController.class);

    private final CourseInSemesterService courseInSemesterService;
    private final SemesterService semesterService;
    private final ParallelService parallelService;

    public MyController(
        CourseInSemesterService courseInSemesterService,
        SemesterService semesterService,
        ParallelService parallelService
    ) {
        this.courseInSemesterService = courseInSemesterService;
        this.semesterService = semesterService;
        this.parallelService = parallelService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public MeDTO getCurrent() {
        return new MeDTO(SecurityUtils.getLoggedInUser());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/courses/{semesterCode}")
    public List<CourseInSemesterDTO> getCourses(@PathVariable(required = false) String semesterCode) {
        User user = SecurityUtils.getLoggedInUser();
        Semester semester = semesterCode == null ? semesterService.getCurrentSemester() : semesterService.findByCode(semesterCode);
        return courseInSemesterService.getAllUsersCoursesInSemester(semester, user)
            .stream()
            .map(CourseInSemesterDTO::new)
            .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/courses")
    public ResponseEntity<Void> enrolUserAsStudent(@RequestBody EnrolCourseFormDTO enrolForm) {
        User user = SecurityUtils.getLoggedInUser();
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(enrolForm.getCourse(), enrolForm.getSemester());
        courseInSemesterService.enrolAsStudentInCourse(user, courseInSemester);
        LOG.debug("Enroled {} as student in course {}.", user.getUsername(), courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/courses");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/courses/{courseCode}/{semesterCode}")
    public void unenrolUserAsStudent(@PathVariable String courseCode, @PathVariable String semesterCode) {
        User user = SecurityUtils.getLoggedInUser();
        CourseInSemester courseInSemester = courseInSemesterService.findByCode(courseCode, semesterCode);
        courseInSemesterService.unenrolFromCourse(user, courseInSemester);
        LOG.debug("Unenroled {} from course {}.", user.getUsername(), courseInSemester);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/timetable", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TimetableSlotDTO> getTimetable() {
        User user = SecurityUtils.getLoggedInUser();
        Semester semester = semesterService.getCurrentSemester();
        return parallelService.getUsersParallelsInSemester(user, semester)
            .stream()
            .map(TimetableSlotDTO::new)
            .collect(Collectors.toList());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/timetable")
    public ResponseEntity<Void> enrolInParallel(@RequestBody EnrolParallelFormDTO enrolParallelForm) {
        User user = SecurityUtils.getLoggedInUser();
        Parallel parallel = parallelService.find(enrolParallelForm.getParallel());
        CourseInSemester courseInSemester = parallel.getCourseInSemester();
        CourseParticipant courseParticipant = courseInSemesterService.getCourseParticipant(courseInSemester, user);
        parallelService.enrollInParallel(courseParticipant, parallel);
        LOG.debug("Enroled user {} in parallel {} in course {}.", user, parallel, courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/timetable", parallel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
