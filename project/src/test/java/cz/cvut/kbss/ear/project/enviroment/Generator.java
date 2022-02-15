package cz.cvut.kbss.ear.project.enviroment;

import cz.cvut.kbss.ear.project.model.User;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setUsername("username" + randomInt());
        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setPassword(Integer.toString(randomInt()));
        user.setPermanentResidence("Residence" + randomInt());
        user.setEmail(user.getUsername() + "@kbss.felk.cvut.cz");
        return user;
    }
}
