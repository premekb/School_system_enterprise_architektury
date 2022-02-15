package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.dao.ClassroomDao;
import cz.cvut.kbss.ear.project.model.Classroom;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomService {

    private final ClassroomDao dao;

    @Autowired
    public ClassroomService(ClassroomDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<Classroom> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Classroom find(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    public Classroom findByName(String name){ return dao.findByName(name);}

    @Transactional
    public void persist(Classroom classroom) {
        Objects.requireNonNull(classroom);
        dao.persist(classroom);
    }

    @Transactional
    public void merge(Classroom classroom) {
        Objects.requireNonNull(classroom);
        dao.update(classroom);
    }

    @Transactional(readOnly = true)
    public boolean exists(String name){
        Objects.requireNonNull(name);
        return dao.exists(name);
    }
}
