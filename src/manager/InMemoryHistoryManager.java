package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> listOfHistory = new LinkedList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

        listOfHistory.add(task.getOldTask());

        if (listOfHistory.size() > MAX_SIZE) {
            listOfHistory.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(listOfHistory);
    }
}
