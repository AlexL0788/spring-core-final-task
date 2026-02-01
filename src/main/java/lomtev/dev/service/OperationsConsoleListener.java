package lomtev.dev.service;

import lomtev.dev.exception.NoSuitableProcessorException;
import lomtev.dev.service.processor.OperationCommandProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class OperationsConsoleListener {
    private final Scanner scanner;
    private final List<OperationCommandProcessor> operationCommandProcessorList;

    public OperationsConsoleListener(Scanner scanner, List<OperationCommandProcessor> operationCommandProcessorList) {
        this.scanner = scanner;
        this.operationCommandProcessorList = operationCommandProcessorList;
    }

    public AvailableOperation readOperation() {
        while (true) {
            System.out.println("Enter your command: " +
                    "\nCreate user: USER_CREATE" +
                    "\nShow all users: SHOW_ALL_USERS" +
                    "\nCreate account: ACCOUNT_CREATE" +
                    "\nClose account: ACCOUNT_CLOSE" +
                    "\nDeposit account: ACCOUNT_DEPOSIT" +
                    "\nTransfer money: ACCOUNT_TRANSFER" +
                    "\nWithdraw money: ACCOUNT_WITHDRAW");

            String input = scanner.nextLine().trim();

            try {
                return AvailableOperation.valueOf(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("Operation: " + input + " is not supported");
            }
        }
    }

    public void processOperation(AvailableOperation operation) {
        OperationCommandProcessor processor = operationCommandProcessorList.stream()
                .filter(p -> p.getOperationType().equals(operation))
                .findFirst()
                .orElseThrow(() -> new NoSuitableProcessorException("Suitable processor for operation " + operation + " doesn't exist"));

        processor.processOperation();
    }
}
