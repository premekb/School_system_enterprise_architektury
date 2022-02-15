package cz.cvut.kbss.ear.project.service.util;

import cz.cvut.kbss.ear.project.kosapi.entities.*;
import cz.cvut.kbss.ear.project.model.Classroom;
import cz.cvut.kbss.ear.project.model.Course;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import cz.cvut.kbss.ear.project.model.enums.ParallelType;
import cz.cvut.kbss.ear.project.model.enums.Role;

import java.sql.Time;
import java.time.DayOfWeek;

public class KosapiEntityConverter {

    public static Course kosCourseToCourse(KosCourse kosCourse){
        Course course = new Course();
        course.setName(kosCourse.getName());
        course.setCredits(Integer.parseInt(kosCourse.getCredits()));
        course.setCode(kosCourse.getCode());
        course.setCompletionType(CourseCompletionType.valueOf(kosCourse.getCompletion()));
        return course;
    }

    public static User kosParticipantToUser(KosParticipant kosParticipant){
        User user = new User();
        user.setEmail(kosParticipant.getEmail());
        user.setFirstName(kosParticipant.getFirstName());
        user.setLastName(kosParticipant.getLastName());
        user.setUsername(kosParticipant.getUsername());
        user.setRole(Role.REGULAR_USER);
        user.setPassword("heslo123");
        return user;
    }

    public static Parallel kosParallelToParallel(KosParallel kosParallel){
        Parallel parallel = new Parallel();
        addKosTimetableSlotToParallel(parallel, kosParallel.getTimetableSlot());
        parallel.setParallelType(ParallelType.valueOf(kosParallel.getParallelType()));
        parallel.setCapacity(Integer.parseInt(kosParallel.getCapacity()));
        parallel.setName(kosParallel.getCode());

        return parallel;
    }

    private static Parallel addKosTimetableSlotToParallel(Parallel parallel, KosTimetableSlot kosTimetableSlot){
        Classroom room = new Classroom();
        if (kosTimetableSlot.getRoom() != null) {
            room.setName(kosTimetableSlot.getRoom().toString());
        } else {
            room.setName("");
        }

        parallel.setClassroom(room);
        parallel.setDayOfWeek(DayOfWeek.of(Integer.parseInt(kosTimetableSlot.getDay())));
        parallel.setStartTime(Time.valueOf(kosTimetableSlot.getStartTime()));
        parallel.setEndTime(Time.valueOf(kosTimetableSlot.getEndTime()));
        return parallel;
    }
}
