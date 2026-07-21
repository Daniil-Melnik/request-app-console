package medan.service;

import medan.dao.EmployeeDao;
import medan.dao.RequestDao;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с заявками. Содержит методы создания, изменения статуса,
 * назначения исполнителя, фильтрации, формирования отчётов и замера производительности.
 */
public class RequestService {
    private final RequestDao requestDao = new RequestDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    /**
     * Создаёт новую заявку с заданными параметрами.
     * Генерирует уникальный номер.
     *
     * @param authorId    ID автора
     * @param executorId  ID исполнителя
     * @param description описание
     * @param dueDate     срок выполнения
     * @return созданная заявка
     */
    public Request createRequest(long authorId, long executorId, String description, LocalDateTime dueDate){
        Employee author = employeeDao.findById(authorId);
        Employee executor = employeeDao.findById(executorId);
        String number = generateNumber();

        Request request = new Request(number, author, executor, description, dueDate);
        requestDao.save(request);

        return request;
    }

    /**
     * Находит ID заявки по её номеру
     *
     * @param number уникальный номер заявки
     * @return ID заявки или null, если не найдена (в DAO выбрасывается исключение, если не найдена)
     */
    public Long findIdByRequestNumber(String number){
        return (new RequestDao()).findIdByNumber(number);
    }

    /**
     * Находит заявку по номеру с полной загрузкой автора и исполнителя.
     *
     * @param number номер заявки
     * @return заявка
     */
    public Request findRequestByNumber(String number){
        return (new RequestDao()).findByNumber(number);
    }

    private String generateNumber(){
        return String.format("REQ-%s", System.currentTimeMillis());
    }

    /**
     * Изменяет статус заявки с проверкой допустимости перехода.
     * При успешном изменении записывает запись в историю.
     *
     * @param requestId            ID заявки
     * @param newStatus            новый статус
     * @param changedByEmployeeId  ID сотрудника, выполнившего изменение (может быть null)
     */
    public void changeStatus(long requestId, RequestStatus newStatus, Long changedByEmployeeId){
        requestDao.updateStatus(requestId, newStatus, changedByEmployeeId);
    }

    /**
     * Назначает нового исполнителя для заявки.
     *
     * @param requestId    ID заявки
     * @param newExecutorId ID нового исполнителя
     */
    public void changeExecutor(Long requestId, Long newExecutorId){
        requestDao.updateExecutor(requestId, newExecutorId);
    }

    /**
     * Возвращает список заявок по заданным фильтрам.
     *
     * @param status     статус (может быть null)
     * @param executorId ID исполнителя (может быть null)
     * @param departament подразделение (может быть null)
     * @param overDue    true – только просроченные, false – все (не зависит от due_date)
     * @return отфильтрованный список заявок (может быть пустым)
     */
    public List<Request> filterRequests(RequestStatus status, Long executorId, String departament, Boolean overDue){
        return requestDao.findWithFilters(status, executorId, departament, overDue);
    }

    /**
     * Выводит отчёт в консоль:
     * - количество заявок по каждому статусу,
     * - общее количество просроченных,
     * - количество выполненных заявок по каждому исполнителю.
     */
    public void printReport() {
        Map<RequestStatus, Long> byStatus = requestDao.countByStatus();
        long overDue = requestDao.countOverDue();
        Map<String, Long> completedByExecutor = requestDao.countCompletedByExecutor();

        System.out.println("========= ОТЧЁТ =========");
        System.out.println("Количество заявок по статусам:");
        for (Map.Entry<RequestStatus, Long> entry : byStatus.entrySet()) {
            System.out.printf("  %s : %s\n", entry.getKey(), entry.getValue());
        }
        System.out.println("Количество просроченных заявок: " + overDue);
        System.out.println();
        System.out.println("Количество выполненных заявок по исполнителям:");
        for (Map.Entry<String, Long> entry : completedByExecutor.entrySet()) {
            System.out.printf("  %s : %s\n", entry.getKey(), entry.getValue());
        }
    }

    /**
     * Выполняет замер времени выполнения запроса просроченных заявок в статусе IN_PROGRESS
     * для конкретного исполнителя.
     *
     * @param executorId ID исполнителя
     * @return время выполнения в миллисекундах
     */
    public long measurePerformance(long executorId) {
        long start = System.nanoTime();
        List<Request> result = requestDao.getOverdueInProgressForExecutor(executorId);
        long end = System.nanoTime();
        long elapsedMs = (end - start) / 1_000_000;
        System.out.printf("Найдено заявок: %s\n", result.size());
        return elapsedMs;
    }
}
