package medan.service;

import medan.dao.StatusHistoryDao;
import medan.model.StatusHistory;

import java.util.List;

/**
 * Сервис для работы с историей изменений статусов.
 */

public class StatusHistoryService {

    /**
     * Возвращает список записей истории для указанной заявки или все записи, если ID равен null.
     * Записи сортируются по времени изменения (от старых к новым).
     *
     * @param id ID заявки (может быть null – тогда возвращаются все записи)
     * @return список записей истории (может быть пустым)
     */
    public List<StatusHistory> getStatusHistory(Long id){
        StatusHistoryDao statusHistoryDao = new StatusHistoryDao();
        if (id != null) return statusHistoryDao.findByRequest(id);
        else return statusHistoryDao.findAll();
    }
}
