package cz.cvut.kbss.ear.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cz.cvut.kbss.ear.project.dao.SemesterDao;
import cz.cvut.kbss.ear.project.exception.SemesterException;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.enums.SemesterState;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class SemesterServiceTest {

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SemesterDao semesterDao;

    @Test
    public void makeSemesterCurrent_currentSemesterExists_oldSemesterArchivedNewSemesterCurrent() {
        addTwoSemesters();
        Semester semester1 = semesterDao.findByCode("B211");
        Semester semester2 = semesterDao.findByCode("B212");

        semesterService.makeSemesterCurrent(semester1);
        semesterService.makeSemesterCurrent(semester2);

        final SemesterState archived = semester1.getState();
        final SemesterState current = semester2.getState();
        assertEquals(SemesterState.ARCHIVED, archived);
        assertEquals(SemesterState.CURRENT, current);
    }

    @Test
    public void makeSemesterCurrent_makeArchivedSemesterCurrent_exceptionThrown() {
        addTwoSemesters();
        Semester semester1 = semesterDao.findByCode("B211");
        Semester semester2 = semesterDao.findByCode("B212");

        semesterService.makeSemesterCurrent(semester1);
        semesterService.makeSemesterCurrent(semester2);

        assertThrows(SemesterException.class, () -> semesterService.makeSemesterCurrent(semester1));
    }

    @Test
    public void makeSemesterCurrent_makeCurrentSemesterCurrent_exceptionThrown() {
        addTwoSemesters();
        Semester semester1 = semesterDao.findByCode("B211");

        semesterService.makeSemesterCurrent(semester1);

        assertThrows(SemesterException.class, () -> semesterService.makeSemesterCurrent(semester1));
    }

    @Test
    public void addSemester_addDuplicateCodes_exceptionThrown() {
        semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);

        assertThrows(SemesterException.class,
            () -> semesterService.addNewSemester("B211", "2021", SemesterType.SUMMER));
    }

    @Test
    public void addSemester_addDuplicateSemesterTypesInOneYear_exceptionThrown() {
        semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        assertThrows(SemesterException.class,
            () -> semesterService.addNewSemester("B212", "2021", SemesterType.WINTER));
    }

    private void addTwoSemesters() {
        semesterService.addNewSemester("B211", "2021", SemesterType.WINTER);
        semesterService.addNewSemester("B212", "2021", SemesterType.SUMMER);
    }
}
