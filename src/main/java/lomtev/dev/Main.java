package lomtev.dev;

import lomtev.dev.service.AvailableOperation;
import lomtev.dev.service.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("lomtev.dev");
        OperationsConsoleListener operationsConsoleListener = context.getBean(OperationsConsoleListener.class);

        while (true) {
            try {
                AvailableOperation availableOperation = operationsConsoleListener.readOperation();
                operationsConsoleListener.processOperation(availableOperation);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
