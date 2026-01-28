package lomtev.dev.service;

import lomtev.dev.model.Account;
import lomtev.dev.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class OperationsConsoleListener {
    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner = new Scanner(System.in);

    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public AvailableOperations readOperation() {
        while (true) {
            System.out.println("Enter your command: " +
                    "\nCreate user: USER_CREATE" +
                    "\nShow all users: SHOW_ALL_USERS" +
                    "\nCreate account: ACCOUNT_CREATE" +
                    "\nClose account: ACCOUNT_CLOSE" +
                    "\nDeposit account: ACCOUNT_DEPOSIT" +
                    "\nTransfer money: ACCOUNT_TRANSFER" +
                    "\nWithdraw money: ACCOUNT_WITHDRAW");

            String input = readLine();

            try {
                return AvailableOperations.valueOf(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("Operation: " + input + " is not supported");
            }
        }
    }

    public void processOperation(AvailableOperations operation) {
        switch (operation) {
            case USER_CREATE:
                createUser();
                break;
            case SHOW_ALL_USERS:
                showAllUsers();
                break;
            case ACCOUNT_CREATE:
                createAccount();
                break;
            case ACCOUNT_CLOSE:
                closeAccount();
                break;
            case ACCOUNT_DEPOSIT:
                depositAccount();
                break;
            case ACCOUNT_TRANSFER:
                transferMoney();
                break;
            case ACCOUNT_WITHDRAW:
                withdrawMoney();
                break;
        }
    }

    private String readLine() {
            return scanner.nextLine().trim();
    }

    private void createUser() {
        System.out.println("Enter login for new user:");
        String login = readLine();

        userService.createUser(login);
    }

    private void showAllUsers() {
        userService.showAllUsers().forEach(System.out::println);
    }

    private void createAccount() {
        System.out.println("Enter userId for account creation:");
        String userIdAsString = readLine();

        try {
            Long userId = Long.parseLong(userIdAsString);

            if (isPositiveNumber(userId)) {
                User user = userService.getUserById(userId);
                Account newAccount = accountService.createAccount(userId);
                user.getAccountList().add(newAccount);
                System.out.println("New account with ID: " + newAccount.getId() + " created for user: " + user.getLogin());
            } else {
                System.out.println("Account creation failed, 'userId' must be positive integer number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Account creation failed, 'userId' must be positive integer number!");
        }
    }

    private void closeAccount() {
        System.out.println("Enter accountId for closing:");
        String accountIdAsString = readLine();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                accountService.closeAccount(accountId);
            } else  {
                System.out.println("Account closing failed, 'accountId' must be positive integer number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Account closing failed, 'accountId' must be positive integer number!");
        }
    }

    private void depositAccount() {
        System.out.println("Enter accountId for deposit:");
        String accountIdAsString = readLine();
        System.out.println("Enter amount for deposit:");
        String depositAmountAsString = readLine();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                BigDecimal depositAmount = new BigDecimal(depositAmountAsString);
                accountService.depositAccount(accountId, depositAmount);
                System.out.println("Account with id " + accountId + " deposited for deposit amount: " + depositAmount);
            } else  {
                System.out.println("Account deposit failed, 'accountId' and 'amount' must be positive integer number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Account deposit failed, 'accountId' and 'amount' must be positive integer number!");
        }
    }

    private void transferMoney() {
        System.out.println("Enter accountId for transfer from:");
        String accountIdFromAsString = readLine();
        System.out.println("Enter accountId for transfer to:");
        String accountIdToAsString = readLine();
        System.out.println("Enter amount for transfer:");
        String amountAsString = readLine();

        try {
            Long accountIdFrom = Long.parseLong(accountIdFromAsString);
            Long accountIdTo = Long.parseLong(accountIdToAsString);

            if (isPositiveNumber(accountIdFrom) && isPositiveNumber(accountIdTo)) {
                BigDecimal transferAmount = new BigDecimal(amountAsString);
                accountService.transferMoney(accountIdFrom, accountIdTo, transferAmount);
            } else {
                System.out.println("Transfer money failed, 'accountId' and 'amount' must be positive integer numbers!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Transfer money failed, 'accountId' and 'amount' must be positive integer numbers!");
        }
    }

    private void withdrawMoney() {
        System.out.println("Enter accountId to withdraw money:");
        String accountIdAsString = readLine();
        System.out.println("Enter amount to withdraw:");
        String amountAsString = readLine();

        try {
            Long accountId = Long.parseLong(accountIdAsString);

            if (isPositiveNumber(accountId)) {
                BigDecimal withdrawAmount = new BigDecimal(amountAsString);
                accountService.withdrawMoney(accountId, withdrawAmount);
            } else {
                System.out.println("Withdraw money failed, 'accountId' and 'amount' must be positive integer numbers!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Withdraw money failed, 'accountId' and 'amount' must be positive integer numbers!");
        }
    }

    private boolean isPositiveNumber(Long value) {
        return value != null && value > 0;
    }
}
