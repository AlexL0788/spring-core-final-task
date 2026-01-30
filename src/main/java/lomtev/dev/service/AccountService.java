package lomtev.dev.service;

import lomtev.dev.exception.NotEnoughMoneyException;
import lomtev.dev.exception.UserHasOnlyOneAccountException;
import lomtev.dev.model.Account;
import lomtev.dev.model.User;
import lomtev.dev.properties.AccountProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountProperties accountProperties;
    private final UserService userService;
    private Long currentId = 0L;
    private final List<Account> accounts = new ArrayList<>();

    public AccountService(AccountProperties accountProperties, @Lazy UserService userService) {
        this.accountProperties = accountProperties;
        this.userService = userService;
    }

    public Account createDefaultAccount(Long userId) {
        Account account = new Account(nextId(), userId, accountProperties.getDefaultAmount());
        accounts.add(account);

        return  account;
    }

    public Account createAccount(Long userId) {
        Account account = new Account(nextId(), userId, BigDecimal.ZERO);
        accounts.add(account);

        return  account;
    }

    public void closeAccount(Long accountId) {
        Optional<Account> accountOptional = getAccountById(accountId);

        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
        }

        Account account = accountOptional.get();
        User user = userService.getUserById(account.getUserId());

        if (checkIfAccountCanBeClosed(user)) {
            accounts.remove(account);
            user.getAccountList().remove(account);
            Account anotherAccount = user.getAccountList().getFirst();
            anotherAccount.setMoneyAmount(anotherAccount.getMoneyAmount().add(account.getMoneyAmount()));
            System.out.println("Account with id " + account.getId() + " was closed");
        } else {
            throw new UserHasOnlyOneAccountException("Operation failed, account can't be closed, user " + user + " has only 1 account");
        }
    }

    public void depositAccount(Long accountId, BigDecimal amount) {
        Optional<Account> accountOptional = getAccountById(accountId);

        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
        }

        if (isPositiveInteger(amount)) {
            Account account = accountOptional.get();
            account.setMoneyAmount(account.getMoneyAmount().add(amount));
        } else {
            throw new IllegalArgumentException("Operation failed, deposit amount must be positive integer number");
        }
    }

    public void transferMoney(Long accountIdFrom, Long accountIdTo, BigDecimal amount) {
        Optional<Account> optionalAccountFrom = getAccountById(accountIdFrom);
        if (optionalAccountFrom.isEmpty()) {
            throw new IllegalArgumentException("Operation failed, account with id " + accountIdFrom + " was not found");
        }

        Optional<Account> optionalAccountTo = getAccountById(accountIdTo);
        if (optionalAccountTo.isEmpty()) {
            throw new IllegalArgumentException("Operation failed, account with id " + accountIdTo + " was not found");
        }

        Account accountFrom = optionalAccountFrom.get();
        Account accountTo = optionalAccountTo.get();

        if (accountsHaveSameOwner(accountFrom, accountTo)) {
            if (accountHasEnoughMoney(accountFrom, amount)) {
                accountFrom.setMoneyAmount(accountFrom.getMoneyAmount().subtract(amount));
                accountTo.setMoneyAmount(accountTo.getMoneyAmount().add(amount));
                System.out.println("Amount " + amount + " was transferred from account with id " + accountFrom.getId() + " to account with id " + accountTo.getId());
            } else {
                throw new NotEnoughMoneyException("Operation failed, account " + accountFrom + " doesn't have enough money amount: " + amount);
            }
        } else {
            BigDecimal amountWithCommission = calculateAmountWithCommission(amount);
            if (accountHasEnoughMoney(accountFrom, amountWithCommission)) {
                accountFrom.setMoneyAmount(accountFrom.getMoneyAmount().subtract(amountWithCommission));
                accountTo.setMoneyAmount(accountTo.getMoneyAmount().add(amount));
                System.out.println("Amount " + amount + " with commission " + accountProperties.getTransferCommission() + "% was transferred from account with id " + accountFrom.getId() + " to account with id " + accountTo.getId());
            } else {
                throw new NotEnoughMoneyException("Operation failed, account " + accountFrom + " doesn't have enough money amount: " + amountWithCommission);
            }
        }
    }

    public void withdrawMoney(Long accountId, BigDecimal amount) {
        Optional<Account> optionalAccount = getAccountById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
        }

        Account account = optionalAccount.get();
        if (accountHasEnoughMoney(account, amount)) {
            account.setMoneyAmount(account.getMoneyAmount().subtract(amount));
            System.out.println("Successful withdrawal of amount " + amount + " from account with id " + account.getId());
        } else {
            throw new NotEnoughMoneyException("Operation failed, account " + account + " doesn't have enough money amount: " + amount);
        }
    }

    private boolean checkIfAccountCanBeClosed(User user) {
        int accountsCount = user.getAccountList().size();

        return  accountsCount > 1;
    }

    private boolean accountsHaveSameOwner(Account accountFirst, Account accountSecond) {
        User user = userService.getUserById(accountFirst.getUserId());
        return user.getAccountList().contains(accountSecond);
    }

    private boolean accountHasEnoughMoney(Account account, BigDecimal amount) {
        return  account.getMoneyAmount().compareTo(amount) >= 0;
    }

    private BigDecimal calculateAmountWithCommission(BigDecimal amount) {
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal commissionPercent = new BigDecimal(accountProperties.getTransferCommission());
        BigDecimal multiplier = commissionPercent
                .divide(hundred, 10, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
        BigDecimal result = amount.multiply(multiplier);

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private Long nextId() {
        return ++currentId;
    }

    private Optional<Account> getAccountById(Long accountId) {
        return accounts.stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .or(Optional::empty);
    }

    private boolean isPositiveInteger(BigDecimal number) {
        if (number == null) {
            return false;
        }
        if (number.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return number.stripTrailingZeros().scale() <= 0;
    }
}
