package lomtev.dev.service.processor;

public interface PositiveNumberChecker {
    default boolean isPositiveNumber(Long value) {
        return value != null && value > 0;
    }
}
