package lomtev.dev.service;

import lomtev.dev.exception.NotEnoughMoneyException;
import lomtev.dev.exception.UserHasOnlyOneAccountException;
import lomtev.dev.model.Account;
import lomtev.dev.model.User;
import lomtev.dev.properties.AccountProperties;
import lomtev.dev.util.TransactionHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountService {
    private final AccountProperties accountProperties;
    private final TransactionHelper transactionHelper;

    public AccountService(AccountProperties accountProperties, TransactionHelper transactionHelper) {
        this.accountProperties = accountProperties;
        this.transactionHelper = transactionHelper;
    }

    public Account createDefaultAccount(User user) {
        return new Account(accountProperties.getDefaultAmount(), user);
    }

    public Account createAccount(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            User user = session.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("Operation failed, user with id " + userId + " was not found");
            }

            Account account = new Account(BigDecimal.ZERO, user);
            session.persist(account);
            return account;
        });
    }

    public void closeAccount(Long accountId) {
        transactionHelper.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
            }

            User user = account.getUser();

            if (checkIfAccountCanBeClosed(user)) {
                session.remove(account);
                user.getAccountList().remove(account);
                Account anotherAccount = session.find(Account.class, user.getAccountList().getFirst().getId());
                anotherAccount.setMoneyAmount(anotherAccount.getMoneyAmount().add(account.getMoneyAmount()));
            } else {
                throw new UserHasOnlyOneAccountException("Operation failed, account can't be closed, user " + user + " has only 1 account");
            }
        });
    }

    public void depositAccount(Long accountId, BigDecimal amount) {
        transactionHelper.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
            }

            if (!isPositiveInteger(amount)) {
                throw new IllegalArgumentException("Operation failed, deposit amount must be positive integer number");
            }

            account.setMoneyAmount(account.getMoneyAmount().add(amount));
        });
    }

    public void transferMoney(Long accountIdFrom, Long accountIdTo, BigDecimal amount) {
        transactionHelper.executeInTransaction(session -> {
            Account accountFrom = session.find(Account.class, accountIdFrom);
            if (accountFrom == null) {
                throw new IllegalArgumentException("Operation failed, account with id " + accountIdFrom + " was not found");
            }

            Account accountTo = session.find(Account.class, accountIdTo);
            if (accountTo == null) {
                throw new IllegalArgumentException("Operation failed, account with id " + accountIdTo + " was not found");
            }

            if (!isPositiveInteger(amount)) {
                throw new IllegalArgumentException("Operation failed, transfer amount must be positive integer number");
            }

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
        });
    }

    public void withdrawMoney(Long accountId, BigDecimal amount) {
        transactionHelper.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Operation failed, account with id " + accountId + " was not found");
            }

            if (!isPositiveInteger(amount)) {
                throw new IllegalArgumentException("Operation failed, withdraw amount must be positive integer number");
            }

            if (accountHasEnoughMoney(account, amount)) {
                account.setMoneyAmount(account.getMoneyAmount().subtract(amount));
            } else {
                throw new NotEnoughMoneyException("Operation failed, account " + account + " doesn't have enough money amount: " + amount);
            }
        });
    }

    private boolean checkIfAccountCanBeClosed(User user) {
        int accountsCount = user.getAccountList().size();

        return accountsCount > 1;
    }

    private boolean accountsHaveSameOwner(Account accountFirst, Account accountSecond) {
        return accountFirst.getUser().getId().equals(accountSecond.getUser().getId());
    }

    private boolean accountHasEnoughMoney(Account account, BigDecimal amount) {
        return account.getMoneyAmount().compareTo(amount) >= 0;
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
