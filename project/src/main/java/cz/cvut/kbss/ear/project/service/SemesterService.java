package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.dao.SemesterDao;
import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.exception.SemesterException;
import cz.cvut.kbss.ear.project.model.Semester;
import cz.cvut.kbss.ear.project.model.enums.SemesterState;
import cz.cvut.kbss.ear.project.model.enums.SemesterType;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SemesterService {

    private final SemesterDao dao;

    public SemesterService(SemesterDao semesterDao) {
        this.dao = semesterDao;
    }

    @Transactional(readOnly = true)
    public List<Semester> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Semester find(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    public Semester findByCode(String code){ return dao.findByCode(code); }

    @Transactional
    public void persist(Semester semester) {
        Objects.requireNonNull(semester);
        dao.persist(semester);
    }

    @Transactional
    public Semester addNewSemester(String code, String year, SemesterType semesterType) {
        if (code == null || year == null || semesterType == null){
            throw new NullPointerException("Code and year and semesterType cannot be null.");
        }

        if (!isCodeUnique(code)) {
            throw new SemesterException("Semester with this code already exists");
        }

        if (existsSemesterWithSameType(semesterType, year)) {
            throw new SemesterException(
                "Semester with type: " + semesterType.toString() + "already exists in year: "
                    + year);
        }

        Semester semester = new Semester();
        semester.setCode(code);
        semester.setState(SemesterState.PREPARATION);
        semester.setYear(year);
        semester.setType(semesterType);
        dao.persist(semester);

        return semester;
    }

    @Transactional
    public void makeSemesterCurrent(Semester newSemester) {
        if (newSemester.getState() == SemesterState.CURRENT) {
            throw new SemesterException(
                "Semester: " + newSemester.getCode() + " is already CURRENT.");
        }

        if (newSemester.getState() == SemesterState.ARCHIVED) {
            throw new SemesterException("Semester: " + newSemester.getCode() + " is ARCHIVED." +
                " Cannot make ARCHIVED semester CURRENT");
        }

        List<Semester> semesters = dao.findByState(SemesterState.CURRENT);
        if (semesters != null && semesters.size() != 0) {
            Semester currentSemester = semesters.get(0);
            currentSemester.setState(SemesterState.ARCHIVED);
            dao.update(currentSemester);
        }

        newSemester.setState(SemesterState.CURRENT);
        dao.update(newSemester);
    }

    @Transactional
    public Semester getCurrentSemester() {
        List<Semester> semesters = dao.findByState(SemesterState.CURRENT);
        if (semesters.size() > 0) {
            return semesters.get(0);
        }

        throw new NotFoundException("There's no current semester");
    }

    private boolean isCodeUnique(String code) {
        return dao.findByCode(code) == null;
    }

    private boolean existsSemesterWithSameType(SemesterType semesterType, String year) {
        for (Semester semester : dao.findByYear(year)) {
            if (semester.getType() == semesterType) {
                return true;
            }
        }

        return false;
    }
}
