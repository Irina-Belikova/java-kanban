package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> listOfHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

        listOfHistory.add(task.getOldTask());

        if (listOfHistory.size() > 10) {
            listOfHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return listOfHistory;
    }
}
