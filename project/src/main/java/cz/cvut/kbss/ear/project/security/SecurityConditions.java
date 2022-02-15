package cz.cvut.kbss.ear.project.security;

import cz.cvut.kbss.ear.project.model.CourseInSemester;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.service.util.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class SecurityConditions {
    public boolean checkIsTeacherOf(CourseInSemester course) {
        return course.getTeachers().stream().anyMatch(ct -> ct.getUser() == SecurityUtils.getLoggedInUser());
    }

    public boolean checkIsAllowedToEdit(Semester semester) {
        switch (semester.getState()) {
            case ARCHIVED:
                return false;
            case PREPARATION:
                return true;
            case CURRENT:
                return SecurityUtils.hasRole("ROLE_ADMIN") || SecurityUtils.hasRole("ROLE_STUDY_DEPARTMENT_EMPLOYEE");
        }

        return false;
    }
}
