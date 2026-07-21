package medan;

import medan.dao.RequestDao;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;
import medan.model.StatusHistory;
import medan.service.EmployeeService;
import medan.service.RequestService;
import medan.service.StatusHistoryService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        boolean exit = false;
        while (!exit){
            printMenu();
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
                    printEmployees();
                    break;
                case "8":
                    showReport();
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

    private static void printMenu() {
        System.out.println("\n============== МЕНЮ =============");
        System.out.println("Сотрудники:");
        System.out.println("  1  -> Добавить сотрудника");
        System.out.println("  2  -> Редактировать сотрудника");
        System.out.println("  7  -> Показать сотрудников");
        System.out.println("Заявки:");
        System.out.println("  3  -> Создать заявку");
        System.out.println("  4  -> Изменить статус заявки");
        System.out.println("  5  -> Назначить исполнителя");
        System.out.println("  6  -> Показать заявки");
        System.out.println("  12 -> Показать заявку по номеру");
        System.out.println("Отчетность и история:");
        System.out.println("  8  -> Отчётность");
        System.out.println("  11 -> История статусов заявок");
        System.out.println("Утилиты:");
        System.out.println("  9  -> Заполнить тестовыми данными");
        System.out.println("  10 -> Замерить производительность");
        System.out.println("0 -> Выход");
        System.out.print("Выберите пункт: ");
    }

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

    private static void printEmployees(){
        int maxIdLen = 10;
        int maxNameLen = 80;
        int maxDepartamentLen = 40;
        int maxPositionLen = 40;

        EmployeeService service = new EmployeeService();
        List<Employee> employees = service.getAllEmployees();
        System.out.println("\n======== СОТРУДНИКИ ========");
        System.out.println(
                "|" + " ".repeat(maxIdLen / 2 - 1) + "id" + " ".repeat(maxIdLen / 2 - 1) +
                "|" + " ".repeat(maxNameLen / 2 - 1) + "name" + " ".repeat(maxNameLen / 2 - 1) +
                        "|" + " ".repeat(maxDepartamentLen / 2 - 6) + "departament" + " ".repeat(maxDepartamentLen / 2 - 6) +
                        "|" + " ".repeat(maxPositionLen / 2 - 4) + "position" + " ".repeat(maxPositionLen / 2 - 4) + "|");
        System.out.println("|" + "=".repeat(maxIdLen + maxNameLen + maxDepartamentLen + maxPositionLen + 4) + "|");
        employees.forEach(emp -> {
            int nameLen = emp.getFullName().length();
            int departLen = emp.getDepartament().length();
            int posLen = emp.getPosition().length();
            int idLen = String.valueOf(emp.getId()).length();

            System.out.println(
                    "|" + emp.getId() + " ".repeat(maxIdLen - idLen) +
                    "|" + emp.getFullName() + " ".repeat(maxNameLen - nameLen + 2) +
                    "|" + emp.getDepartament() + " ".repeat(maxDepartamentLen - departLen - 1) +
                    "|" + emp.getPosition() + " ".repeat(maxPositionLen - posLen) + "|");

        });

        System.out.println("=".repeat(maxIdLen + maxNameLen + maxDepartamentLen + maxPositionLen + 1 + 4 + 1));
    }

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
        } catch (NumberFormatException ex){
            System.out.println("Некорректный id пользователя");
        }
    }

    private static void showRequests(){
        try {
            System.out.print("Статус (NEW, IN_PROGRESS, COMPLETED) (ALL): ");
            String statusStr = scanner.nextLine().trim();
            RequestStatus status = statusStr.isEmpty() ? null : RequestStatus.valueOf(statusStr.toUpperCase());

            System.out.print("ID исполнителя (ALL): ");
            String execStr = scanner.nextLine().trim();
            Long executorId = execStr.isEmpty() ? null : Long.parseLong(execStr);

            System.out.print("Подразделение (ALL): ");
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
                printRequests(requests);
            }
        } catch (IllegalArgumentException ex1){
            System.out.println("Неверное заполнение полей фильтрации: " + ex1.getMessage());
        } catch (Exception ex2){
            System.out.println("Ошбка фильтрации: " + ex2.getMessage());
        }
    }

    private static void showRequest(){
        System.out.println("\n======== ПРОСМОТР ЗАЯВКИ ========");
        System.out.print("Введите номер заявки: ");
        String requestNumber = scanner.nextLine();
        RequestService service = new RequestService();
        Request request = service.findRequestByNumber(requestNumber);
        printRequest(request);
    }

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

    private static void showReport(){
        RequestService service = new RequestService();
        service.printReport();
    }

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

    private static void printRequests(List<Request> reqs){
        int maxIdLen = 10;
        int maxNumberLen = 22;
        int maxStatusLen = 12;
        int maxDueDateLen = 20;
        int maxExecutorFullName = 80;

        System.out.println(
                "|" + " ".repeat(maxIdLen / 2 - 1) + "id" + " ".repeat(maxIdLen / 2 - 1) +
                "|" + " ".repeat(maxNumberLen / 2 - 3) + "number" + " ".repeat(maxNumberLen / 2 - 3) +
                "|" + " ".repeat(maxStatusLen / 2 - 3) + "status" + " ".repeat(maxStatusLen / 2 - 3) +
                "|" + " ".repeat(maxDueDateLen / 2 - 4) + "due date" + " ".repeat(maxDueDateLen / 2 - 4) +
                "|" + " ".repeat(maxExecutorFullName / 2 - 4) + "executor" + " ".repeat(maxExecutorFullName / 2 - 4) +
                "|"
        );

        System.out.println(" ".repeat(maxIdLen + maxNumberLen + maxStatusLen + maxDueDateLen + maxExecutorFullName + 6));

        reqs.forEach(r -> {
            int idLen = String.valueOf(r.getId()).length();
            int numberLen = r.getNumber().length();
            int statusLen = r.getStatus().toString().length();
            int dueDateLen = r.getDueDate().toString().length();
            int executorNameLen = r.getExecutor().getFullName().length();

            System.out.println("|" + r.getId() + " ".repeat(maxIdLen - idLen) +
                    "|" + r.getNumber() + " ".repeat(maxNumberLen - numberLen) +
                    "|" + r.getStatus() + " ".repeat(maxStatusLen - statusLen) +
                    "|" + r.getDueDate() + " ".repeat(maxDueDateLen - dueDateLen) +
                    "|" + r.getExecutor().getFullName() + " ".repeat(maxExecutorFullName - executorNameLen) +
                    "|");
        });
        System.out.println(" ".repeat(maxIdLen + maxNumberLen + maxStatusLen + maxDueDateLen + maxExecutorFullName + 6));
    }

    private static void showStatusOfRequests(){
        System.out.println("\n======== ИСТОРИЯ ИЗМЕНЕНИЯ СТАТУСОВ ========");
        System.out.print("Введите номер заявки (ALL): ");
        String number = scanner.nextLine();

        try {
            RequestService service = new RequestService();
            Long id = null;
            if (!number.trim().isEmpty()) id = service.findIdByRequestNumber(number);
            List<StatusHistory> statuses = (new StatusHistoryService()).getStatusHistory(id);
            if (statuses.isEmpty()){
                System.out.println("Записи о изменениях статусов не найдены");
            } else {
                printStatuses(statuses);
            }
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void printStatuses(List<StatusHistory> statuses){
        int maxIdLen = 10;
        int maxRequestIdLen = 22;
        int maxStatusLen = 12;
        int maxNameLen = 80;

        System.out.println("|" + " ".repeat(maxIdLen / 2 - 1) + "id" + " ".repeat(maxIdLen / 2 - 1) +
                "|" + " ".repeat(maxRequestIdLen / 2 - 3) + "number" + " ".repeat(maxRequestIdLen / 2 - 3) +
                "|" + " ".repeat(maxStatusLen / 2 - 5) + "old status" + " ".repeat(maxStatusLen / 2 - 5) +
                "|" + " ".repeat(maxStatusLen / 2 - 5) + "new status" + " ".repeat(maxStatusLen / 2 - 5) +
                "|" + " ".repeat(maxNameLen / 2 - 2) + "name" + " ".repeat(maxNameLen / 2 - 2) +
                "|"
        );

        System.out.println("=".repeat(maxIdLen + maxRequestIdLen + maxStatusLen * 2 + maxNameLen + 6));

        statuses.forEach(s -> {
            long sid = s.getId();
            String rNumber = s.getRequest().getNumber();
            String oldStatus = s.getOldStatus().toString();
            String newStatus = s.getNewStatus().toString();
            String name = s.getChangedBy().getFullName();

            int idLen = String.valueOf(sid).length();
            int numberLen = rNumber.length();
            int oldStatusLen = oldStatus.length();
            int newStatusLen = newStatus.length();
            int nameLen = name.length();

            System.out.println("|" + sid + " ".repeat(maxIdLen - idLen) +
                    "|" + rNumber + " ".repeat(maxRequestIdLen - numberLen) +
                    "|" + oldStatus + " ".repeat(maxStatusLen - oldStatusLen) +
                    "|" + newStatus + " ".repeat(maxStatusLen - newStatusLen) +
                    "|" + name + " ".repeat(maxNameLen - nameLen) +
                    "|"
            );
        });
        System.out.println("=".repeat(maxIdLen + maxRequestIdLen + maxStatusLen * 2 + maxNameLen + 6));
    }

    private static void printRequest(Request request){
        System.out.println("======= ЗАЯВКА ========");
        System.out.printf("            ID -> %s\n", request.getId());
        System.out.printf("         Номер -> %s\n", request.getNumber());
        System.out.printf(" Дата создания -> %s\n", request.getCreatedDate());
        System.out.printf("Срок испонения -> %s\n", request.getDueDate());
        System.out.printf("         Автор -> %s\n", request.getAuthor().getFullName());
        System.out.printf("   Исполнитель -> %s\n", request.getExecutor().getFullName());
        System.out.printf("      Описание -> %s\n", request.getDescription());
        System.out.printf("        Статус -> %s\n\n", request.getStatus());

    }
}