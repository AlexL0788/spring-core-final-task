package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AccountService;
import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.processor.OperationCommandProcessor;
import lomtev.dev.service.processor.PositiveNumberChecker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class DepositAccountProcessor implements OperationCommandProcessor, PositiveNumberChecker {
    private final Scanner scanner;
    private final AccountService accountService;

    public DepositAccountProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter accountId for deposit:");
        String accountIdAsString = scanner.nextLine().trim();
        System.out.println("Enter amount for deposit:");
        String depositAmountAsString = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                BigDecimal depositAmount = new BigDecimal(depositAmountAsString);
                accountService.depositAccount(accountId, depositAmount);
                System.out.println("Account with id " + accountId + " deposited for deposit amount: " + depositAmount);
            } else  {
                throw new IllegalArgumentException("Account deposit failed, 'accountId' and 'amount' must be positive integer numbers!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account deposit failed, 'accountId' and 'amount' must be positive integer numbers!");
        }
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.ACCOUNT_DEPOSIT;
    }
}
