package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AccountService;
import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.processor.OperationCommandProcessor;
import lomtev.dev.service.processor.PositiveNumberChecker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class WithdrawAccountProcessor implements OperationCommandProcessor, PositiveNumberChecker {
    private final Scanner scanner;
    private final AccountService accountService;

    public WithdrawAccountProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter accountId to withdraw money:");
        String accountIdAsString = scanner.nextLine().trim();
        System.out.println("Enter amount to withdraw:");
        String amountAsString = scanner.nextLine().trim();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                BigDecimal withdrawAmount = new BigDecimal(amountAsString);
                accountService.withdrawMoney(accountId, withdrawAmount);
                System.out.println("Successful withdrawal of amount " + withdrawAmount + " from account with id " + accountId);
            } else {
                throw new IllegalArgumentException("Withdraw money failed, 'accountId' and 'amount' must be positive integer numbers!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Withdraw money failed, 'accountId' and 'amount' must be positive integer numbers!");
        }
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.ACCOUNT_WITHDRAW;
    }
}
