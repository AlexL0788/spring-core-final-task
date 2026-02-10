package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AccountService;
import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.processor.OperationCommandProcessor;
import lomtev.dev.service.processor.PositiveNumberChecker;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountProcessor implements OperationCommandProcessor, PositiveNumberChecker {
    private final Scanner scanner;
    private final AccountService accountService;

    public CloseAccountProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter accountId for closing:");
        String accountIdAsString = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                accountService.closeAccount(accountId);
                System.out.println("Account with id " + accountId + " was closed");
            } else {
                throw new IllegalArgumentException("Account closing failed, 'accountId' must be positive integer number!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account closing failed, 'accountId' must be positive integer number!");
        }
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.ACCOUNT_CLOSE;
    }
}
