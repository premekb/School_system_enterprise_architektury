package cz.cvut.kbss.ear.project.rest.controllers;

import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseParticipant;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.rest.dto.ParallelDTO;
import cz.cvut.kbss.ear.project.rest.dto.UsernameDTO;
import cz.cvut.kbss.ear.project.rest.util.RestUtils;
import cz.cvut.kbss.ear.project.service.CourseInSemesterService;
import cz.cvut.kbss.ear.project.service.ParallelService;
import cz.cvut.kbss.ear.project.service.UserService;
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

/**
* /api/parallels/id DONE
* - DELETE:
*      - remove parallel DONE
* /api/parallels/id/participants DONE
* - POST:
*      - enrol DONE
* - DELETE:
*      - unenrol DONE
*/
@RestController
@RequestMapping("/api/parallels")
public class ParallelController {
    private static final Logger LOG = LoggerFactory.getLogger(ParallelController.class);

    private final ParallelService parallelService;

    private final UserService userService;

    private final CourseInSemesterService courseInSemesterService;

    public ParallelController(ParallelService parallelService, UserService userService, CourseInSemesterService courseInSemesterService) {
        this.parallelService = parallelService;
        this.userService = userService;
        this.courseInSemesterService = courseInSemesterService;
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParallel(@PathVariable Integer parallelId) {
        Parallel parallel = parallelService.find(parallelId);
        parallelService.removeParallelFromCourse(parallel);
        LOG.debug("Removed parallel {}.", parallel);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{parallelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ParallelDTO getParallelById(@PathVariable Integer parallelId) {
        Parallel parallel = parallelService.find(parallelId);
        return new ParallelDTO(parallel);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{parallelId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getParallelUsers(@PathVariable Integer parallelId) {
        Parallel parallel = parallelService.find(parallelId);
        return parallel.getAllParticipants()
                .stream()
                .map(CourseParticipant::getUser)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @DeleteMapping(value = "/{parallelId}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenrolFromParallel(@PathVariable Integer parallelId, @RequestBody UsernameDTO usernameDTO) {
        Parallel parallel = parallelService.find(parallelId);
        CourseInSemester courseInSemester = parallel.getCourseInSemester();
        User user = userService.findByUsername(usernameDTO.getUsername());
        CourseParticipant courseParticipant = courseInSemesterService.getCourseParticipant(courseInSemester, user);
        parallelService.unenrollFromParallel(courseParticipant, parallel);
        LOG.debug("Unenrolled user {} from parallel {} in course {}.", user, parallel, courseInSemester);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_STUDY_DEPARTMENT_EMPLOYEE')")
    @PostMapping(value = "/{parallelId}/participants")
    public ResponseEntity<Void> enrolInParallel(@PathVariable Integer parallelId, @RequestBody UsernameDTO usernameDTO) {
        Parallel parallel = parallelService.find(parallelId);
        CourseInSemester courseInSemester = parallel.getCourseInSemester();
        User user = userService.findByUsername(usernameDTO.getUsername());
        CourseParticipant courseParticipant = courseInSemesterService.getCourseParticipant(courseInSemester, user);
        parallelService.enrollInParallel(courseParticipant, parallel);
        LOG.debug("Enroled user {} in parallel {} in course {}.", user, parallel, courseInSemester);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{parallelId}/participants", parallel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
