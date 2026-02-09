package lomtev.dev.service.processor.impl;

import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.UserService;
import lomtev.dev.service.processor.OperationCommandProcessor;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersProcessor implements OperationCommandProcessor {
    private final UserService userService;

    public ShowAllUsersProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        userService.getAllUsers().forEach(System.out::println);
    }

    @Override
    public AvailableOperation getOperationType() {
        return AvailableOperation.SHOW_ALL_USERS;
    }
}
