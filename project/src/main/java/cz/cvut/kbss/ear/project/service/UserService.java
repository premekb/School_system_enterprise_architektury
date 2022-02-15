package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.dao.CourseParticipantDao;
import cz.cvut.kbss.ear.project.dao.UserDao;
import cz.cvut.kbss.ear.project.exception.NotFoundException;
import cz.cvut.kbss.ear.project.exception.UserException;
import cz.cvut.kbss.ear.project.kosapi.entities.KosStudent;
import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.Role;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cglib.proxy.Mixin;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserDao dao;
    private final CourseParticipantDao courseParticipantDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserDao userDao, CourseParticipantDao courseParticipantDao, PasswordEncoder passwordEncoder
    ) {
        this.dao = userDao;
        this.courseParticipantDao = courseParticipantDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return dao.findAll();
    }

    @Transactional
    public void setUserRole(User user, Role role) {
        user.setRole(role);
        dao.update(user);
    }

    @Transactional(readOnly = true)
    public User find(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public void persist(User user) {
        Objects.requireNonNull(user);

        if (exists(user.getUsername())) {
            throw new UserException(
                String.format("User with username %s already exists", user.getUsername()));
        }

        user.encodePassword(passwordEncoder);

        dao.persist(user);
    }

    @Transactional(readOnly = true)
    public Collection<Parallel> getTimetableInSemester(User user) {
        return courseParticipantDao.findAllByUser(user).stream()
            .flatMap(u -> u.getParallels().stream()).collect(
                Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return dao.findByUsername(username) != null;
    }

    public User findByUsername(String username) {
        User user = dao.findByUsername(username);
        if (user == null) {
            throw NotFoundException.create("User", username);
        }
        return user;
    }
}
