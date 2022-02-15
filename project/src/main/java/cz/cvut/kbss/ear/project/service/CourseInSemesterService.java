package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.dao.*;
import cz.cvut.kbss.ear.project.exception.CourseException;
import cz.cvut.kbss.ear.project.exception.EnrolmentException;
import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.CourseParticipant;
import cz.cvut.kbss.ear.project.model.CourseStudent;
import cz.cvut.kbss.ear.project.model.CourseTeacher;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseInSemesterService {

    private final CourseTeacherDao courseTeacherDao;

    private final CourseStudentDao courseStudentDao;

    private final CourseParticipantDao courseParticipantDao;

    private final CourseDao courseDao;

    private final SemesterDao semesterDao;

    private final CourseInSemesterDao courseInSemesterDao;

    private final KosapiService kosapiService;

    public CourseInSemesterService(
        CourseTeacherDao courseTeacherDao,
        CourseStudentDao courseStudentDao,
        CourseParticipantDao courseParticipantDao,
        CourseInSemesterDao courseInSemesterDao,
        CourseDao courseDao,
        SemesterDao semesterDao,
        KosapiService kosapiService
    ) {
        this.courseTeacherDao = courseTeacherDao;
        this.courseStudentDao = courseStudentDao;
        this.courseParticipantDao = courseParticipantDao;
        this.courseDao = courseDao;
        this.semesterDao = semesterDao;
        this.courseInSemesterDao = courseInSemesterDao;
        this.kosapiService = kosapiService;
    }

    @Transactional(readOnly = true)
    public List<CourseInSemester> findAll() {
        return courseInSemesterDao.findAll();
    }

    @Transactional(readOnly = true)
    public CourseInSemester find(Integer id) {
        return courseInSemesterDao.find(id);
    }

    @Transactional(readOnly = true)
    public CourseInSemester findByCode(String courseCode, String semesterCode){
        CourseInSemester result = courseInSemesterDao.findCourseInSemester(courseDao.findByCode(courseCode), semesterDao.findByCode(semesterCode));
        if (result == null) {
            throw NotFoundException.create("CourseInSemester", "Coursecode:" + courseCode + ", SemesterCode: " + semesterCode);
        }
        return result;
    }

    @Transactional
    public void persist(CourseInSemester courseInSemester) {
        courseInSemesterDao.persist(courseInSemester);
    }

    @Transactional
    public CourseInSemester addCourseToSemester(Course course, Semester semester) {
        Objects.requireNonNull(course);
        Objects.requireNonNull(semester);

        if (courseInstanceExists(course, semester)) {
            throw new CourseException("Course: " + course.getCode() +
                " already has an instance in semester " + semester.getCode());
        }

        CourseInSemester courseInSemester = new CourseInSemester();
        courseInSemester.setCourse(course);
        courseInSemester.setSemester(semester);
        courseInSemesterDao.persist(courseInSemester);

        return courseInSemester;
    }

    @Transactional
    @PreAuthorize("@securityConditions.checkIsAllowedToEdit(#course.semester)")
    public CourseStudent enrolAsStudentInCourse(User user, CourseInSemester course) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(course);
        if (isUserEnroled(user, course)) throw new EnrolmentException(user.getUsername()  + " is already enroled.");

        CourseStudent student = new CourseStudent();
        student.setCourse(course);
        student.setUser(user);
        courseStudentDao.persist(student);
        return student;
    }

    @Transactional
    public CourseTeacher enrolAsTeacherInCourse(User user, CourseInSemester course) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(course);
        if (isUserEnroled(user, course)) throw new EnrolmentException(user.getUsername()  + " is already enroled.");

        CourseTeacher teacher = new CourseTeacher();
        teacher.setUser(user);
        course.addTeacher(teacher);
        courseTeacherDao.persist(teacher);
        return teacher;
    }

    @Transactional
    @PreAuthorize("@securityConditions.checkIsAllowedToEdit(#course.semester)")
    public void unenrolFromCourse(User user, CourseInSemester course) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(course);

        CourseParticipant participant = courseParticipantDao.findByUserAndCourse(user, course);
        if (participant == null){
            throw NotFoundException.create("CourseParticipant", user);
        }
        participant.unenrollFromCourse();
        courseParticipantDao.remove(participant);
    }

    @Transactional(readOnly = true)
    public boolean isUserEnroled(User user, CourseInSemester course) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(course);

        CourseParticipant participant = courseParticipantDao.findByUserAndCourse(user, course);
        return participant != null;
    }

    @Transactional(readOnly = true)
    public boolean courseInstanceExists(Course course, Semester semester) {
        Objects.requireNonNull(course);
        Objects.requireNonNull(semester);

        return courseInSemesterDao.findCourseInSemester(course, semester) != null;
    }

    @Transactional(readOnly = true)
    public Collection<Parallel> getParallels(CourseInSemester courseInSemester) {
        Objects.requireNonNull(courseInSemester);

        return courseInSemesterDao.find(courseInSemester.getId()).getParallels();
    }

    @Transactional(readOnly = true)
    public List<CourseStudent> getStudents(CourseInSemester courseInSemester){
        Objects.requireNonNull(courseInSemester);

        return courseInSemesterDao.findStudents(courseInSemester);
    }

    @Transactional(readOnly = true)
    public List<CourseTeacher> getTeachers(CourseInSemester courseInSemester){
        Objects.requireNonNull(courseInSemester);

        return courseInSemesterDao.findTeachers(courseInSemester);
    }

    @Transactional(readOnly = true)
    public List<CourseParticipant> getAllParticipants(CourseInSemester courseInSemester){
        Objects.requireNonNull(courseInSemester);

        return courseInSemesterDao.findAllParticipants(courseInSemester);
    }

    @Transactional(readOnly = true)
    public CourseParticipant getCourseParticipant(CourseInSemester courseInSemester, User user){
        Objects.requireNonNull(courseInSemester);
        Objects.requireNonNull(user);

        CourseParticipant courseParticipant = courseInSemesterDao.findParticipant(courseInSemester, user);

        if (courseParticipant == null) {
            throw new EnrolmentException("User is not enrolled in the course.");
        }

        return courseParticipant;
    }

    @Transactional(readOnly = true)
    public List<CourseInSemester> getAllUsersCoursesInSemester(Semester semester, User user){
        Objects.requireNonNull(semester);
        Objects.requireNonNull(user);

        return courseInSemesterDao.findUsersCourses(semester, user);
    }

    public List<CourseInSemester> getAllCoursesInSemester(Semester semester){
        Objects.requireNonNull(semester);

        return courseInSemesterDao.findCoursesCoursesInSemester(semester);
    }
}
