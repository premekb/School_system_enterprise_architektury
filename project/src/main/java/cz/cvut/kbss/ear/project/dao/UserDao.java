package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.User;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User findByUsername(String username) {
        try {
            return em.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
