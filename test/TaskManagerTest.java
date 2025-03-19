import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T tm;

    protected TaskManagerTest() throws IOException {
        tm = (T) createManager();
    }

    protected abstract TaskManager createManager() throws IOException;

    //Проверка: задача типа Task создаётся в менеджере и добавляется в таблицу задач, задача по Id находится.
    @Test
    void shouldBeAddTasks() {
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1"));
        int taskId = task1.getId();
        Task task2 = tm.getTaskById(taskId);
        List<Task> tasks = tm.getAllTasks();

        assertNotNull(task2, "Задача не найдена.");
        assertEquals(task1, task2, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    //Проверка: эпик типа Epic создаётся в менеджере и добавляется в таблицу эпиков, эпик по Id находится.
    @Test
    void shouldBeAddEpics() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        int epicId = epic1.getId();
        Epic epic2 = tm.getEpicById(epicId);
        List<Epic> epics = tm.getAllEpics();

        assertNotNull(epic2, "Эпик не найден.");
        assertEquals(epic1, epic2, "Эпики не совпадают.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    //Проверка: подзадача типа Subtask создаётся в менеджере и добавляется в таблицу подзадач, подзадача по Id находится,
    //подзадача добавляется в список подзадач эпика, список подзадач эпика не пустой.
    @Test
    void shouldBeAddSubtasks() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        int epicId = epic1.getId();
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        int subtaskId = subtask1.getId();
        Subtask subtask2 = tm.getSubtaskById(subtaskId);
        List<Subtask> subtasks1 = tm.getAllSubtasks();
        List<Subtask> listEpicSubtasks = tm.getEpicSubtasks(epic1);

        assertNotNull(subtask2, "Подзадача не найдена.");
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
        assertNotNull(subtasks1, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks1.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks1.getFirst(), "Подзадачи не совпадают.");
        assertEquals(subtask1.getEpicId(), epicId, "Подзадача не принадлежит эпику.");
        assertFalse(listEpicSubtasks.isEmpty(), "Список подзадач не найден.");
        assertTrue(listEpicSubtasks.contains(subtask1), "Подзадача не добавлена в список подзадач эпика.");
    }

    //неизменность всех полей задачи при добавлении в менеджер
    @Test
    void shouldTasksFieldsEqual() {
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1"));
        int taskId = task1.getId();
        Task task2 = tm.getTaskById(taskId);

        assertEquals(taskId, task2.getId(), "Id задач не равны.");
        assertEquals(task1.getName(), task2.getName(), "Имена задач не равны.");
        assertEquals(task1.getDescription(), task2.getDescription(), "Описания задач не равны.");
        assertEquals(task1.getStatus(), task2.getStatus(), "Статус задач разный.");
    }

    //неизменность всех полей эпика при добавлении в менеджер
    @Test
    void shouldEpicsFieldsEqual() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        int epicId = epic1.getId();
        Epic epic2 = tm.getEpicById(epicId);

        assertEquals(epicId, epic2.getId(), "Id эпиков не равны.");
        assertEquals(epic1.getName(), epic2.getName(), "Имена эпиков не равны.");
        assertEquals(epic1.getDescription(), epic2.getDescription(), "Описания эпиков не равны.");
        assertEquals(epic1.getSubtasksId(), epic2.getSubtasksId(), "Списки подзадач эпиков не равны.");
        assertEquals(epic1.getStatus(), epic2.getStatus(), "Статус эпиков разный.");
    }

    //неизменность всех полей подзадачи при добавлении в менеджер
    @Test
    void shouldSubtasksFieldsEqual() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        int subtaskId = subtask1.getId();
        Subtask subtask2 = tm.getSubtaskById(subtaskId);

        assertEquals(subtaskId, subtask2.getId(), "Id подзадач не равны.");
        assertEquals(subtask1.getName(), subtask2.getName(), "Имена подзадач не равны.");
        assertEquals(subtask1.getDescription(), subtask2.getDescription(), "Описания подзадач не равны.");
        assertEquals(subtask1.getEpicId(), subtask2.getEpicId(), "Id эпика подзадач не равны.");
        assertEquals(subtask1.getStatus(), subtask2.getStatus(), "Статус подзадач разный.");
    }

    //проверка, что подзадачи нельзя добавлять в подзадачи
    @Test
    void shouldNotAddSubtaskInSubtask() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        Subtask subtask3 = tm.addSubtasks(new Subtask("подзадача-3", "описание пзд-3", subtask1.getId()));

        assertEquals(0, subtask3.getId(), "Подзадачу можно добавить в подзадачу.");
    }

    //проверка заполнения истории задач и удаления из истории повторных просмотров задачи
    @Test
    void shouldGetHistory() {
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1"));
        tm.getTaskById(task1.getId());
        tm.getTaskById(task1.getId());
        List<Task> historyList = tm.getHistory();

        assertNotNull(historyList, "Список истории просмотров пуст.");
        assertEquals(1, historyList.size(), "Предыдущий просмотр задачи не удаляется.");
    }

    //проверка удаления подзадачи из эпика при удалении самой подзадачи
    @Test
    void shouldDeleteSubtaskFromEpic() {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        int subtaskId = subtask1.getId();
        tm.removeSubtaskById(subtaskId);
        List<Subtask> listOfSubtasks = tm.getEpicSubtasks(epic1);

        assertTrue(listOfSubtasks.isEmpty(), "Подзадача не удалилась из списка подзадач эпика.");
    }

    //проверка удаления подзадач, если их эпик удаляется
    @Test
    void shouldClearSubtasksIfTheirEpicClear() throws IOException {
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        tm.clearEpics();
        List<Epic> epics = tm.getAllEpics();
        List<Subtask> subtasks = tm.getAllSubtasks();

        assertEquals(0, epics.size(), "Все эпики не удаляются.");
        assertEquals(0, subtasks.size(), "После удаления эпика его подзадачи не удалились.");
    }

    //проверка удаления всех задач из истории при удалении всех задач
    @Test
    void shouldClearHistoryIfClearTasks() {
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1"));
        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика -1"));
        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача1", "описание пзд1", epic1.getId()));

        tm.getTaskById(task1.getId());
        tm.getEpicById(epic1.getId());
        tm.getSubtaskById(subtask1.getId());
        tm.clearSubtasks();
        List<Task> historyList = tm.getHistory();

        assertEquals(2, historyList.size(), "Все подзадачи не удаляются из истории.");

        tm.clearTasks();
        historyList = tm.getHistory();

        assertEquals(1, historyList.size(), "Все задачи не удаляются из истории.");

        Subtask subtask2 = tm.addSubtasks(new Subtask("подзадача-2", "описание пзд-2", epic1.getId()));
        tm.getSubtaskById(subtask2.getId());
        tm.clearEpics();
        historyList = tm.getHistory();

        assertTrue(historyList.isEmpty(), "Все эпики и подзадачи не удаляются из истории.");
    }
}
