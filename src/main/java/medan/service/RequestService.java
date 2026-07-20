package medan.service;

import medan.dao.EmployeeDao;
import medan.dao.RequestDao;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

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

    private String generateNumber(){
        return String.format("REQ-%s", System.currentTimeMillis());
    }

    public void changeStatus(long requestId, RequestStatus newStatus, long changedByEmployeeId){
        requestDao.updateStatus(requestId, newStatus, changedByEmployeeId);
    }

    public void changeExecutor(long requestId, long newExecutorId){
        requestDao.updateExecutor(requestId, newExecutorId);
    }

    public List<Request> filterRequests(RequestStatus status, long executorId, String departament, boolean overDue){
        return requestDao.findWithFilters(status, executorId, departament, overDue);
    }
}
