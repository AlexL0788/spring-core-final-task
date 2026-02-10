package lomtev.dev.service;

import lomtev.dev.exception.LoginAlreadyExistsException;
import lomtev.dev.model.User;
import lomtev.dev.util.TransactionHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final AccountService accountService;
    private final TransactionHelper transactionHelper;
    private final SessionFactory sessionFactory;

    public UserService(AccountService accountService, TransactionHelper transactionHelper, SessionFactory sessionFactory) {
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {
        if (checkIfUserWithLoginCanBeCreated(login)) {
            return transactionHelper.executeInTransaction(session -> {
                User user = new User(login);
                session.persist(user);
                session.flush();
                user.getAccountList().add(accountService.createDefaultAccount(user));

                return user;
            });

        } else {
            throw new LoginAlreadyExistsException("User creation failed. User with login " + login + " already exists!");
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u left join fetch u.accountList", User.class).list();
        }
    }

    private boolean checkIfUserWithLoginCanBeCreated(String login) {
        return getAllUsers().stream().noneMatch(user -> login.equals(user.getLogin()));
    }
}
