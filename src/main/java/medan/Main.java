package medan;

import medan.model.RequestStatus;
import medan.service.RequestService;
import medan.util.DataGenerator;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Start!");
        //DataGenerator.generateEmployeesAndRequests(10, 1000);
        RequestService service = new RequestService();
        service.changeStatus(4, RequestStatus.NEW, 1);
        System.out.printf("End!");
    }
}