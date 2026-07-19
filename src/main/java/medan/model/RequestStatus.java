package medan.model;

public enum RequestStatus {
    NEW,
    IN_PROGRESS,
    COMPLETED;

    public boolean canTransitionTo(RequestStatus next) {
        boolean isNewToInProgress = this == NEW && next == IN_PROGRESS;
        boolean isInProgressToCompleted = this == IN_PROGRESS && next == COMPLETED;
        return isNewToInProgress || isInProgressToCompleted;
    }
}
