package lomtev.dev.service;

import lomtev.dev.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private Long currentId = 0L;
    private final AccountService accountService;

    public UserService(AccountService accountService) {
        this.accountService = accountService;
    }

    private Long nextId() {
        return ++currentId;
    }

    public void createUser(String login) {
        if (checkIfUserWithLoginCanBeCreated(login)) {
            Long userId = nextId();
            User user = new User(userId, login, new ArrayList<>(List.of(accountService.createDefaultAccount(userId))));
            users.add(user);

            System.out.println("User created: " + user);
        } else {
            System.out.println("User creation failed. User with login " + login + " already exists!");
        }
    }

    public List<User> showAllUsers() {
        return users;
    }

    public boolean checkIfAccountCanBeClosed(User user) {
        int accountsCount = user.getAccountList().size();

        if(accountsCount <= 1) {
            System.out.println("Operation failed, account can't be closed, user " + user + " has only 1 account");
            return  false;
        }
        return true;
    }

    private boolean checkIfUserWithLoginCanBeCreated(String login) {
        return users.stream().noneMatch(user -> login.equals(user.getLogin()));
    }

    public User getUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Operation failed, user with id " + userId + " not found"));
    }
}
