package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager tm = Managers.getDefault();
    static HistoryManager hm = Managers.getHistory();
    static Task task1;
    static Epic epic1;
    static Subtask subtask1;

    @BeforeEach
    void beforeEach() {
        task1 = new Task("задача-1", "описание зд-1");
        task1 = tm.addTasks(task1);

        epic1 = new Epic("эпик-1", "описание эпика -1");
        epic1 = tm.addEpics(epic1);

        subtask1 = new Subtask("подзадача-1", "описание пзд-1", epic1.getId());
        subtask1 = tm.addSubtasks(subtask1);
    }

    //проверка добавления задачи в историю, удаление повторных просмотров и добавление задачи в конец списка истории
    @Test
    void shouldAddTasksInHistory() {
        hm.add(task1);
        hm.add(subtask1);
        hm.add(epic1);
        hm.add(task1);
        List<Task> historyList = hm.getHistory();
        Task endTask = historyList.getLast();

        assertNotNull(historyList, "Список истории просмотров пуст.");
        assertEquals(3, historyList.size(), "Предыдущий просмотр задачи не удаляется.");
        assertEquals(task1.getId(), endTask.getId(), "Задача не добавляется в конец списка.");

    }

    //проверка удаления задачи из истории
    @Test
    void shouldRemoveTaskFromHistory() {
        hm.add(task1);
        hm.remove(task1.getId());
        List<Task> historyList = hm.getHistory();

        assertFalse(historyList.contains(task1), "Задача не удалена из списка истории.");
    }
}
