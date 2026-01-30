package lomtev.dev.exception;

public class UserHasOnlyOneAccountException extends RuntimeException {
    public UserHasOnlyOneAccountException(String message) {
        super(message);
    }
}
