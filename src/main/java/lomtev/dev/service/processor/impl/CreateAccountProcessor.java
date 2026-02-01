package lomtev.dev.service.processor.impl;

import lomtev.dev.model.Account;
import lomtev.dev.model.User;
import lomtev.dev.service.AccountService;
import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.UserService;
import lomtev.dev.service.processor.OperationCommandProcessor;
import lomtev.dev.service.processor.PositiveNumberChecker;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateAccountProcessor implements OperationCommandProcessor, PositiveNumberChecker {
    private final Scanner scanner;
    private  final AccountService accountService;
    private final UserService userService;

    public CreateAccountProcessor(Scanner scanner, AccountService accountService, UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter userId for account creation:");
        String userIdAsString = scanner.nextLine().trim();

        try {
            Long userId = Long.parseLong(userIdAsString);

            if (isPositiveNumber(userId)) {
                User user = userService.getUserById(userId);
                Account newAccount = accountService.createAccount(userId);
                user.getAccountList().add(newAccount);
                System.out.println("New account with ID: " + newAccount.getId() + " created for user: " + user.getLogin());
            } else {
                throw new IllegalArgumentException("Account creation failed, 'userId' must be positive integer number!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account creation failed, 'userId' must be positive integer number!");
        }
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.ACCOUNT_CREATE;
    }
}
