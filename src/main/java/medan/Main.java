package medan;

import medan.util.DataGenerator;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Start!");
        DataGenerator.generateEmployeesAndRequests(10, 1000);
        System.out.printf("End!");
    }
}