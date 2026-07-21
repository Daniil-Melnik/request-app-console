package medan.util;

import medan.model.Employee;
import medan.model.Request;
import medan.model.StatusHistory;
import medan.service.EmployeeService;

import java.util.List;

/**
 * Утилитный класс для форматированного вывода данных в консоль.
 * Содержит методы для печати меню, списков заявок, сотрудников, истории статусов.
 */

public class PrintUtil {
    /**
     * Выводит информацию о заявке в читаемом виде.
     *
     * @param request заявка для вывода
     */
    public static void printRequest(Request request){
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

    /**
     * Выводит историю изменений статусов в виде таблицы.
     * Для каждой записи показывает ID, номер заявки, старый и новый статус, имя изменившего сотрудника.
     *
     * @param statuses список записей истории
     */
    public static void printStatuses(List<StatusHistory> statuses){
        int maxIdLen = 14;
        int maxRequestIdLen = 22;
        int maxStatusLen = 12;
        int maxNameLen = 80;

        System.out.println("|" + " ".repeat(maxIdLen / 2 - 1) + "id" + " ".repeat(maxIdLen / 2 - 1) +
                "|" + " ".repeat(maxRequestIdLen / 2 - 3) + "number" + " ".repeat(maxRequestIdLen / 2 - 3) +
                "|" + " ".repeat(maxStatusLen / 2 - 5) + "old status" + " ".repeat(maxStatusLen / 2 - 5) +
                "|" + " ".repeat(maxStatusLen / 2 - 5) + "new status" + " ".repeat(maxStatusLen / 2 - 5) +
                "|" + " ".repeat(maxNameLen / 2 - 7) + "name changed by" + " ".repeat(maxNameLen / 2 - 7) +
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

    /**
     * Выводит меню приложения в консоль
     */
    public static void printMenu() {
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


    /**
     * Печатает таблицу с заявками.
     * Ширина колонок фиксирована для единообразного отображения.
     *
     * @param reqs список заявок
     */
    public static void printRequests(List<Request> reqs){
        int maxIdLen = 10;
        int maxNumberLen = 22;
        int maxStatusLen = 12;
        int maxDueDateLen = 28;
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

    /**
     * Выводит список сотрудников в виде таблицы.
     *
     * @param employees список сотрудников
     */
    public static void printEmployees(List<Employee> employees){
        int maxIdLen = 14;
        int maxNameLen = 80;
        int maxDepartamentLen = 40;
        int maxPositionLen = 40;

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

}
