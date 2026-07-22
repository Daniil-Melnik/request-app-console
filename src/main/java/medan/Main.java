package medan;

import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;
import medan.model.StatusHistory;
import medan.service.EmployeeService;
import medan.service.RequestService;
import medan.service.StatusHistoryService;
import medan.util.DataGenerator;
import medan.util.PrintUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Консольное приложение для учёта заявок сотрудников.
 * Предоставляет меню для выполнения всех операций: создание, редактирование,
 * фильтрация, отчёты, генерация тестовых данных, замер производительности.
 */

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit){
            PrintUtil.printMenu();
            String input = scanner.nextLine();
            switch (input){
                case "0":
                    exit = true;
                    break;
                case "1":
                    addEmployee();;
                    break;
                case "2":
                    updateEmployee();
                    break;
                case "3":
                    createRequest();
                    break;
                case "4":
                    changeStatus();
                    break;
                case "5":
                    assignExecutor();
                    break;
                case "6":
                    showRequests();
                    break;
                case "7":
                    showEmployees();
                    break;
                case "8":
                    showReport();
                    break;
                case "9":
                    generateTestData();
                    break;
                case "10":
                    measurePerformance();
                    break;
                case "11":
                    showStatusOfRequests();
                    break;
                case "12":
                    showRequest();
                    break;
            }
            System.out.println(" для продолжения нажмите Enter . . .");
            scanner.nextLine();
        }
    }

    /**
     * Обработчик пункта меню "Добавить сотрудника"
     * Запрашивает у пользователя ФИО, подразделение, должность и вызывает сервис для сохранения
     */
    private static void addEmployee(){
        EmployeeService service = new EmployeeService();

        System.out.println("\n======= ДОБАВИТЬ СОТРУДНИКА ======");
        System.out.print("ФИО: ");
        String name = scanner.nextLine();
        System.out.print("Подразделение: ");
        String departament = scanner.nextLine();
        System.out.print("Должность: ");
        String position = scanner.nextLine();
        service.createEmployee(name, departament, position);
        System.out.printf("Добавлен сотрудник %s с должностью - %s в подразделении - %s\n", name, position, departament);
    }

    /**
     * Обработчик пункта меню "Создать заявку"
     * Запрашивает ID автора, исполнителя, описание и срок, создаёт заявку через сервис
     * Перехватывает исключения и выводит понятные сообщения
     */

    private static void createRequest(){
        System.out.println("\n======== СОЗДАНИЕ ЗАЯВКИ ========");
        try {
            RequestService service = new RequestService();
            System.out.print("ID автора: ");
            long authorID = scanner.nextLong();
            scanner.nextLine();
            System.out.print("ID испольнителя: ");
            long executorID = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Описание: ");
            String description = scanner.nextLine();
            System.out.print("Срок выполнения (yyyy-MM-dd HH:mm): ");
            LocalDateTime dueDate = LocalDateTime.parse(scanner.nextLine(), DATE_FORMATTER);
            service.createRequest(authorID,executorID,description,dueDate);
            System.out.println("Заявка успешно создана");
        } catch (NumberFormatException ex0){
            System.out.println("Некорректные ID автора или(и) испольнителя");
        } catch (Exception ex1){
            System.out.println("Ошибка создания заявки: " + ex1.getMessage());
        }

    }

    /**
     * Обработчик пункта меню "Редактировать сотрудника"
     * Запрашивает ID, загружает текущие данные, позволяет изменить поля (оставить пустыми — без изменений)
     */

    private static void updateEmployee(){
        System.out.println("\n======== РЕДАКТИРОВАНИЕ СОТРУДНИКА ========");
        System.out.print("Введите id сотрудника: ");
        try {
            long id = scanner.nextLong();
            scanner.nextLine();
            EmployeeService service = new EmployeeService();
            Employee current = service.getSinggleEmployee(id);

            System.out.println("Сотрудник: " + current.getFullName());
            System.out.print("Новое ФИО (" + current.getFullName() + "): ");
            String newName = scanner.nextLine();
            System.out.print("Новая должность (" + current.getPosition() + "): ");
            String newPos = scanner.nextLine();
            System.out.print("Новое подразделение (" + current.getDepartament() + "): ");
            String newDepart = scanner.nextLine();

            if (!newName.isEmpty()) current.setFullName(newName);
            if (!newPos.isEmpty()) current.setPosition(newPos);
            if (!newDepart.isEmpty()) current.setDepartament(newDepart);
            if (!(newName.isEmpty() && newDepart.isEmpty() && newPos.isEmpty())){
                service.updateEmployee(current);
                System.out.printf("Обновлён сотрудник %s с должностью - %s в подразделении - %s\n",
                        current.getFullName(),
                        current.getPosition(),
                        current.getDepartament());
            }
        } catch (Exception ex){
            System.out.println("Некорректный id пользователя");
        }
    }

    /**
     * Обработчик пункта меню "Показать сотрудников".
     * Выводит всех сотрудников в табличном виде.
     */
    private static void showEmployees(){
        EmployeeService service = new EmployeeService();
        List<Employee> employees = service.getAllEmployees();
        System.out.println("\n======== СОТРУДНИКИ ========");
        PrintUtil.printEmployees(employees);
    }

    /**
     * Обработчик пункта меню "Показать заявки"
     * Запрашивает параметры фильтрации (статус, исполнитель, подразделение, просроченность)
     * и выводит отфильтрованный список заявок в табличном виде
     */
    private static void showRequests(){
        try {
            System.out.println("\n======== ЗАЯВКИ С ФИЛЬТРАЦИЕЙ ========");
            System.out.print("Статус (NEW, IN_PROGRESS, COMPLETED) (умолч. все): ");
            String statusStr = scanner.nextLine().trim();
            RequestStatus status = statusStr.isEmpty() ? null : RequestStatus.valueOf(statusStr.toUpperCase());

            System.out.print("ID исполнителя (умолч. все): ");
            String execStr = scanner.nextLine().trim();
            Long executorId = execStr.isEmpty() ? null : Long.parseLong(execStr);

            System.out.print("Подразделение (умолч. все): ");
            String department = scanner.nextLine().trim();
            if (department.isEmpty()) department = null;

            System.out.print("Показывать только просроченные? (false): ");
            String overStr = scanner.nextLine().trim();
            Boolean overDue = overStr.isEmpty() ? false : Boolean.parseBoolean(overStr);

            RequestService service = new RequestService();
            List<Request> requests = service.filterRequests(status, executorId, department, overDue);

            if (requests.isEmpty()){
                System.out.println("Заявок не найдено");
            } else {
                System.out.printf("Найдено заявок: %s\n", requests.size());
                PrintUtil.printRequests(requests);
            }
        } catch (IllegalArgumentException ex1){
            System.out.println("Неверное заполнение полей фильтрации: " + ex1.getMessage());
        } catch (Exception ex2){
            System.out.println("Ошбка фильтрации: " + ex2.getMessage());
        }
    }

    /**
     * Обработчик пункта меню "Показать заявку по номеру"
     * Запрашивает номер и выводит полную информацию о заявке
     */
    private static void showRequest(){
        System.out.println("\n======== ПРОСМОТР ЗАЯВКИ ========");
        System.out.print("Введите номер заявки: ");
        String requestNumber = scanner.nextLine();
        RequestService service = new RequestService();
        Request request = service.findRequestByNumber(requestNumber);
        PrintUtil.printRequest(request);
    }

    /**
     * Обработчик пункта меню "Назначить исполнителя"
     * Запрашивает ID заявки и ID нового исполнителя, обновляет заявку
     */
    private static void assignExecutor() {
        try {
            System.out.println("\n======== НАЗНАЧИТЬ ИСПОЛНИТЕЛЯ ========");
            System.out.print("ID заявки: ");
            long requestId = Long.parseLong(scanner.nextLine());
            System.out.print("ID нового исполнителя: ");
            long executorId = Long.parseLong(scanner.nextLine());
            RequestService service = new RequestService();
            service.changeExecutor(requestId, executorId);
            System.out.println("Исполнитель назначен");
        } catch (NumberFormatException e) {
            System.out.println("Введён некорректный ID");
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Обработчик пункта меню "Отчётность"
     * Выводит статистику: количество заявок по статусам, просроченные, выполненные по исполнителям
     */

    private static void showReport(){
        RequestService service = new RequestService();
        service.printReport();
    }

    /**
     * Обработчик пункта меню "Изменить статус заявки"
     * Запрашивает ID заявки, новый статус и ID сотрудника, выполняющего изменение
     * Проверяет допустимость перехода через бизнес-правила
     */

    private static void changeStatus() {
        try {
            System.out.println("\n======== ИЗМЕНЕНИЕ СТАТУСА ЗАЯВКИ ========");
            System.out.print("ID заявки: ");

            long requestId = Long.parseLong(scanner.nextLine());

            System.out.print("Новый статус (NEW, IN_PROGRESS, COMPLETED): ");
            String statusStr = scanner.nextLine().toUpperCase();
            RequestStatus newStatus = RequestStatus.valueOf(statusStr);

            System.out.print("ID изменяющего статус: ");
            Long changedBy = Long.parseLong(scanner.nextLine());
            if (changedBy <= 0) changedBy = null;

            RequestService service = new RequestService();
            service.changeStatus(requestId, newStatus, changedBy);
            System.out.println("Статус обновлён");

        } catch (IllegalArgumentException e) {
            System.out.println("Неорректные параметры изменения статуса заявки");
        } catch (Exception e) {
            System.err.printf("Ошибка: %s", e.getMessage());
        }
    }

    /**
     * Обработчик пункта меню "История статусов заявок"
     * Запрашивает номер заявки и выводит историю изменений статусов
     */
    private static void showStatusOfRequests(){
        System.out.println("\n======== ИСТОРИЯ ИЗМЕНЕНИЯ СТАТУСОВ ========");
        System.out.print("Введите номер заявки (умолч. все): ");
        String number = scanner.nextLine();

        try {
            RequestService service = new RequestService();
            Long id = null;
            if (!number.trim().isEmpty()) id = service.findIdByRequestNumber(number);
            List<StatusHistory> statuses = (new StatusHistoryService()).getStatusHistory(id);
            if (statuses.isEmpty()){
                System.out.println("Записи о изменениях статусов не найдены");
            } else {
                PrintUtil.printStatuses(statuses);
            }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Обработчик пункта меню "Замерить производительность"
     * Выполняет несколько раз запрос просроченных заявок в работе для указанного исполнителя
     * замеряет время и выводит среднее, минимальное, максимальное время
     */
    private static void measurePerformance() {
        try {
            System.out.println("\n======== ЗАМЕР ПРОИЗВОДИТЕЛЬНОСТИ ========");
            RequestService service = new RequestService();

            System.out.print("ID исполнителя для поиска просроченных заявок: ");
            long executorId = Long.parseLong(scanner.nextLine());

            System.out.print("Количество запросов: ");
            int iterations = Integer.parseInt(scanner.nextLine());
            if (iterations < 1) iterations = 1;

            System.out.println("Старт");
            long totalTime = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = 0;

            for (int i = 1; i <= iterations; i++) {
                long time = service.measurePerformance(executorId);
                totalTime += time;
                if (time < minTime) minTime = time;
                if (time > maxTime) maxTime = time;
                System.out.printf("  Попытка %d: %d мс%n", i, time);
            }

            double average = (double) totalTime / iterations;
            System.out.println("\n======== РЕЗУЛЬТАТЫ ЗАМЕРА =========");
            System.out.printf("Исполнитель ID: %d%n", executorId);
            System.out.printf("Количество выполнений: %d%n", iterations);
            System.out.printf("Среднее время: %.2f мс%n", average);
            System.out.printf("Минимальное время: %d мс%n", minTime);
            System.out.printf("Максимальное время: %d мс%n", maxTime);
            System.out.println("=====================================");

        } catch (NumberFormatException e) {
            System.out.println("Некорректное число");
        } catch (Exception e) {
            System.err.println("Ошибка замера: " + e.getMessage());
        }
    }

    /**
     * Обработчик пункта меню "Заполнить тестовыми данными"
     * Запрашивает количество сотрудников и заявок, вызывает генератор
     */
    private static void generateTestData() {
        try {
            System.out.println("\n======== ГЕНЕРАЦИЯ ТЕСТОВЫХ ДАННЫХ =========");
            System.out.print("Количество сотрудников: ");
            int empCount = Integer.parseInt(scanner.nextLine());
            System.out.print("Количество заявок: ");
            int reqCount = Integer.parseInt(scanner.nextLine());
            DataGenerator.generateEmployeesAndRequests(empCount, reqCount);
        } catch (NumberFormatException e) {
            System.out.println("Некорректное число");
        } catch (Exception e) {
            System.err.println("Ошибка генерации: " + e.getMessage());
        }
    }
}