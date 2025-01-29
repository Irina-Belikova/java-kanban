package manager;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }
}
