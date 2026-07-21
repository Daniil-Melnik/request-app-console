package medan.service;

import medan.dao.EmployeeDao;
import medan.dao.RequestDao;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final RequestDao requestDao = new RequestDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    public Request createRequest(long authorId, long executorId, String description, LocalDateTime dueDate){
        Employee author = employeeDao.findById(authorId);
        Employee executor = employeeDao.findById(executorId);
        String number = generateNumber();

        Request request = new Request(number, author, executor, description, dueDate);
        requestDao.save(request);

        return request;
    }

    public Long findIdByRequestNumber(String number){
        return (new RequestDao()).findIdByNumber(number);
    }

    public Request findRequestByNumber(String number){
        return (new RequestDao()).findByNumber(number);
    }

    private String generateNumber(){
        return String.format("REQ-%s", System.currentTimeMillis());
    }

    public void changeStatus(long requestId, RequestStatus newStatus, Long changedByEmployeeId){
        requestDao.updateStatus(requestId, newStatus, changedByEmployeeId);
    }

    public void changeExecutor(Long requestId, Long newExecutorId){
        requestDao.updateExecutor(requestId, newExecutorId);
    }

    public List<Request> filterRequests(RequestStatus status, Long executorId, String departament, Boolean overDue){
        return requestDao.findWithFilters(status, executorId, departament, overDue);
    }

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
}
