package cz.cvut.kbss.ear.project.service;

import cz.cvut.kbss.ear.project.model.User;
import cz.cvut.kbss.ear.project.model.enums.Role;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SystemInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    /**
     * Default admin username
     */
    private static final String ADMIN_USERNAME = "ear-admin@fel.cvut.cz";

    /**
     * Default regular user username
     */
    private static final String REGULAR_USER_USERNAME = "ear-user@fel.cvut.cz";

    /**
     * Default study department employee username
     */
    private static final String STUDY_DEPARTMENT_EMPLOYEE_USERNAME = "ear-study@fel.cvut.cz";

    private final UserService userService;

    private final PlatformTransactionManager txManager;

    @Autowired
    public SystemInitializer(UserService userService,
                             PlatformTransactionManager txManager) {
        this.userService = userService;
        this.txManager = txManager;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generateAdmin();
            generateUser();
            generateStudyDepartmentEmployee();
            return null;
        });
    }

    /**
     * Generates an admin account if it does not already exist.
     */
    private void generateAdmin() {
        if (userService.exists(ADMIN_USERNAME)) {
            return;
        }
        final User admin = new User();
        admin.setUsername("admin");
        admin.setEmail(ADMIN_USERNAME);
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setPassword("heslo123");
        admin.setPermanentResidence("");
        admin.setRole(Role.ADMIN);
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        userService.persist(admin);
    }

    /**
     * Generates an user account if it does not already exist.
     */
    private void generateUser() {
        if (userService.exists(REGULAR_USER_USERNAME)) {
            return;
        }
        final User user = new User();
        user.setUsername("user");
        user.setEmail(REGULAR_USER_USERNAME);
        user.setFirstName("System");
        user.setLastName("User");
        user.setPassword("heslo123");
        user.setPermanentResidence("");
        user.setRole(Role.REGULAR_USER);
        LOG.info("Generated regular user with credentials " + user.getUsername() + "/" + user.getPassword());
        userService.persist(user);
    }

    /**
     * Generates an study department employee account if it does not already exist.
     */
    private void generateStudyDepartmentEmployee() {
        if (userService.exists(STUDY_DEPARTMENT_EMPLOYEE_USERNAME)) {
            return;
        }
        final User user = new User();
        user.setUsername("study");
        user.setEmail(STUDY_DEPARTMENT_EMPLOYEE_USERNAME);
        user.setFirstName("System");
        user.setLastName("Study Department Employee");
        user.setPassword("heslo123");
        user.setPermanentResidence("");
        user.setRole(Role.STUDY_DEPARTMENT_EMPLOYEE);
        LOG.info("Generated study department employee with credentials " + user.getUsername() + "/" + user.getPassword());
        userService.persist(user);
    }
}
