package medan.service;

import medan.dao.StatusHistoryDao;
import medan.model.StatusHistory;

import java.util.List;

public class StatusHistoryService {
    public List<StatusHistory> getStatusHistory(Long id){
        StatusHistoryDao statusHistoryDao = new StatusHistoryDao();
        if (id != null) return statusHistoryDao.findByRequest(id);
        else return statusHistoryDao.findAll();
    }
}
