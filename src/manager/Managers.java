package manager;

import java.io.File;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getHistory() {
        return new InMemoryHistoryManager();
    }
}
