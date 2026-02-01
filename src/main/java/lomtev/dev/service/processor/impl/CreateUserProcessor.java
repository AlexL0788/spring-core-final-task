package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.UserService;
import lomtev.dev.service.processor.OperationCommandProcessor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateUserProcessor implements OperationCommandProcessor {
    private final Scanner scanner;
    private final UserService userService;

    public CreateUserProcessor(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter login for new user:");
        String login = scanner.nextLine().trim();

        userService.createUser(login);
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.USER_CREATE;
    }
}
