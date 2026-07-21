package medan.model;

/**
 * Перечисление возможных статусов заявки
 * Поддерживает переходы: NEW → IN_PROGRESS → COMPLETED
 */

public enum RequestStatus {
    NEW,
    IN_PROGRESS,
    COMPLETED;

    /**
     * Проверяет, допустим ли переход от текущего статуса к новому
     *
     * @param next целевой статус
     * @return true, если переход разрешён, иначе false
     */

    public boolean canTransitionTo(RequestStatus next) {
        boolean isNewToInProgress = this == NEW && next == IN_PROGRESS;
        boolean isInProgressToCompleted = this == IN_PROGRESS && next == COMPLETED;
        return isNewToInProgress || isInProgressToCompleted;
    }
}
