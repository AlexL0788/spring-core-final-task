package lomtev.dev.service.processor;

import lomtev.dev.service.AvailableOperation;

public interface OperationCommandProcessor {
    void processOperation();
    AvailableOperation getOperationType();
}
