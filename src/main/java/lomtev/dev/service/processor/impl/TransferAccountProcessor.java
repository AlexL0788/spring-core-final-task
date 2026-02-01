package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AccountService;
import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.processor.OperationCommandProcessor;
import lomtev.dev.service.processor.PositiveNumberChecker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class TransferAccountProcessor implements OperationCommandProcessor, PositiveNumberChecker {
    private final Scanner scanner;
    private final AccountService accountService;

    public TransferAccountProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter accountId for transfer from:");
        String accountIdFromAsString = scanner.nextLine().trim();
        System.out.println("Enter accountId for transfer to:");
        String accountIdToAsString = scanner.nextLine().trim();
        System.out.println("Enter amount for transfer:");
        String amountAsString = scanner.nextLine().trim();

        try {
            Long accountIdFrom = Long.parseLong(accountIdFromAsString);
            Long accountIdTo = Long.parseLong(accountIdToAsString);

            if (isPositiveNumber(accountIdFrom) && isPositiveNumber(accountIdTo)) {
                BigDecimal transferAmount = new BigDecimal(amountAsString);
                accountService.transferMoney(accountIdFrom, accountIdTo, transferAmount);
            } else {
                throw new IllegalArgumentException("Transfer money failed, 'accountId' and 'amount' must be positive integer numbers!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Transfer money failed, 'accountId' and 'amount' must be positive integer numbers!");
        }
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.ACCOUNT_TRANSFER;
    }
}
