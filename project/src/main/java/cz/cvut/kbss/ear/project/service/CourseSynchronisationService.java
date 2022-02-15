package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.kosapi.entities.*;
import cz.cvut.kbss.ear.project.model.*;
import cz.cvut.kbss.ear.project.service.util.KosapiEntityConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Synchronises parallels and parallels in a course based on KOS.
 */
@Service
public class CourseSynchronisationService {

    private final CourseInSemesterService courseInSemesterService;

    private final UserService userService;

    private final KosapiService kosapiService;

    private final ParallelService parallelService;

    private final ClassroomService classroomService;

    private KosCourse kosCourse;

    private List<KosStudent> kosStudentsInCourse = new ArrayList<>();

    private List<KosTeacher> kosTeachersInCourse = new ArrayList<>();

    private List<KosParallel> kosParallels = new ArrayList<>();

    private HashMap<KosParallel, List<KosStudent>> parallelStudents = new HashMap<>();

    private HashMap<KosParallel, List<KosTeacher>> parallelTeachers = new HashMap<>();

    private CourseInSemester courseInSemester;

    public CourseSynchronisationService(CourseInSemesterService courseInSemesterService, ParallelService parallelService,
                                        KosapiService kosapiService, UserService userService, ClassroomService classroomService) {
        this.courseInSemesterService = courseInSemesterService;
        this.kosapiService = kosapiService;
        this.userService = userService;
        this.parallelService = parallelService;
        this.classroomService = classroomService;
    }

    @Transactional
    public void synchroniseWithKos(CourseInSemester courseInSemester){
        this.courseInSemester = courseInSemester;
        loadDataFromKosapi();
        synchroniseCourseEnrolments();
        synchroniseParallelEnrolments();
    }

    private void synchroniseCourseEnrolments(){
        List<User> students = convertStudents(kosStudentsInCourse);
        List<User> teachers = convertTeachers(kosTeachersInCourse);
        enrolNewUsersInCourse(students, teachers, courseInSemester);

        //List<User> allParticipantsFromKos = students;
        //allParticipantsFromKos.addAll(teachers);

        //unenrolOldUsersFromCourse(allParticipantsFromKos, courseInSemesterService.getAllParticipants(courseInSemester));
    }

    private void enrolNewUsersInCourse(List<User> students, List<User> teachers, CourseInSemester courseInSemester){
        for (User student : students){
            if (!courseInSemesterService.isUserEnroled(student, courseInSemester)){
                courseInSemesterService.enrolAsStudentInCourse(student, courseInSemester);
            }
        }

        for (User teacher : teachers){
            if (!courseInSemesterService.isUserEnroled(teacher, courseInSemester)){
                courseInSemesterService.enrolAsTeacherInCourse(teacher, courseInSemester);
            }
        }
    }

    private void unenrolOldUsersFromCourse(List<User> participantsFromKos, List<CourseParticipant> currentParticipants){
        boolean foundInKos;
        for (CourseParticipant currentParticipant : currentParticipants){
            foundInKos = false;
            for (User participantKos : participantsFromKos){
                if (participantKos.getUsername().equals(currentParticipant.getUser().getUsername())){
                    foundInKos = true;
                    break;
                }
            }

            if (!foundInKos){
                courseInSemesterService.unenrolFromCourse(currentParticipant.getUser(), currentParticipant.getCourse());
            }
        }
    }

    private List<User> convertTeachers(List<KosTeacher> kosTeachers){
        List<User> convertedUsers = new ArrayList<>();

        for (KosParticipant kosParticipant : kosTeachers){
            convertedUsers.add(createOrGetUser(kosParticipant));
        }

        return convertedUsers;
    }

    private List<User> convertStudents(List<KosStudent> kosStudents){
        List<User> convertedUsers = new ArrayList<>();

        for (KosParticipant kosParticipant : kosStudents){
            convertedUsers.add(createOrGetUser(kosParticipant));
        }

        return convertedUsers;
    }

    private void synchroniseParallelEnrolments(){
        for (KosParallel kosParallel : kosParallels){
            if (kosParallel.getTimetableSlot() != null) {
                Parallel parallel = createOrGetParallel(kosParallel);
                List<KosStudent> studentsInParallel = parallelStudents.get(kosParallel);
                List<KosTeacher> teachersInParallel = parallelTeachers.get(kosParallel);

                enrolNewUsersInParallel(parallel, studentsInParallel, teachersInParallel);
            }
        }
    }

    private void enrolNewUsersInParallel(Parallel parallel, List<KosStudent> kosStudents, List<KosTeacher> kosTeachers){
        List<CourseParticipant> parallelParticipants = parallel.getAllParticipants();
        for (KosStudent student : kosStudents){
            User user = createOrGetUser(student);
            enrolUserInParallel(user, parallel);
        }

        for (KosTeacher teacher : kosTeachers){
            User user = createOrGetUser(teacher);
            enrolUserInParallel(user, parallel);
        }

    }

    private void enrolUserInParallel(User user, Parallel parallel){
        if (!parallelService.isUserEnroled(parallel, user)){
            CourseParticipant courseParticipant = courseInSemesterService.getCourseParticipant(parallel.getCourseInSemester(), user);
            parallelService.enrollInParallel(courseParticipant, parallel);
        }
    }

    private User createOrGetUser(KosParticipant kosParticipant){
        // TODO cache?
        User user = KosapiEntityConverter.kosParticipantToUser(kosParticipant);
        if (!userService.exists(user.getUsername())){
            userService.persist(user);
        }
        return userService.findByUsername(user.getUsername());
    }

    private Parallel createOrGetParallel(KosParallel kosParallel){
        Parallel parallel = KosapiEntityConverter.kosParallelToParallel(kosParallel);
        for (Parallel existingParallel : courseInSemester.getParallels()) {
            if (existingParallel.getName().equals(parallel.getName())) return existingParallel;
        }
        Classroom classroom = parallel.getClassroom();
        if (!classroomService.exists(classroom.getName())){
            classroomService.persist(classroom);
        }
        else{
            parallel.setClassroom(classroomService.findByName(classroom.getName()));
        }

        parallelService.addParallelToCourse(parallel, courseInSemester);
        return parallel;
    }

    private void loadDataFromKosapi(){
        kosCourse = kosapiService.getCourseInSemester(courseInSemester.getCourse().getCode(),
                courseInSemester.getSemester().getCode());
        kosStudentsInCourse = kosapiService.getStudentsInCourse(kosCourse);
        kosTeachersInCourse = kosapiService.getTeachersInCourse(kosCourse);
        kosParallels = kosapiService.getParallelsInCourse(kosCourse.getCode(), courseInSemester.getSemester().getCode());

        for (KosParallel parallel : kosParallels){
            parallelTeachers.put(parallel, kosapiService.getTeachersInParallel(parallel));
            parallelStudents.put(parallel, kosapiService.getStudentsInParallel(parallel));
        }
    }
}
