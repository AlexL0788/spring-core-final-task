package lomtev.dev;

import lomtev.dev.service.AvailableOperations;
import lomtev.dev.service.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("lomtev.dev");
        OperationsConsoleListener operationsConsoleListener = context.getBean(OperationsConsoleListener.class);

        while (true) {
            AvailableOperations availableOperation = operationsConsoleListener.readOperation();
            operationsConsoleListener.processOperation(availableOperation);
        }
    }
}
