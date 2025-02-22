import static org.junit.jupiter.api.Assertions.*;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

class InMemoryTaskManagerTest {
    static TaskManager tm = Managers.getDefault();
    static Task task1;
    static Task task2;
    static Epic epic1;
    static Epic epic2;
    static Subtask subtask1;
    static Subtask subtask2;
    static int taskId;
    static int epicId;
    static int subtaskId;

    @BeforeAll
    static void beforeAll() {
        task1 = new Task("задача-1", "описание зд-1");
        task1 = tm.addTasks(task1);
        taskId = task1.getId();
        task2 = tm.getTaskById(taskId);

        epic1 = new Epic("эпик-1", "описание эпика -1");
        epic1 = tm.addEpics(epic1);
        epicId = epic1.getId();
        epic2 = tm.getEpicById(epicId);

        subtask1 = new Subtask("подзадача-1", "описание пзд-1", epic1.getId());
        subtask1 = tm.addSubtasks(subtask1);
        subtaskId = subtask1.getId();
        subtask2 = tm.getSubtaskById(subtaskId);
    }

    //Проверка: задача типа Task создаётся в менеджере и добавляется в таблицу задач, задача по Id находится.
    @Test
    void shouldBeAddTasks() {
        assertNotNull(task2, "Задача не найдена.");
        assertEquals(task1, task2, "Задачи не совпадают.");

        final List<Task> tasks = tm.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    //Проверка: эпик типа Epic создаётся в менеджере и добавляется в таблицу эпиков, эпик по Id находится.
    @Test
    void shouldBeAddEpics() {
        assertNotNull(epic2, "Эпик не найден.");
        assertEquals(epic1, epic2, "Эпики не совпадают.");

        final List<Epic> epics = tm.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    //Проверка: подзадача типа Subtask создаётся в менеджере и добавляется в таблицу подзадач, подзадача по Id находится,
    //подзадача добавляется в список подзадач эпика, список подзадач эпика не пустой.
    @Test
    void shouldBeAddSubtasks() {
        assertNotNull(subtask2, "Подзадача не найдена.");
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = tm.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");

        assertEquals(subtask1.getEpicId(), epicId, "Подзадача не принадлежит эпику.");

        List<Subtask> listEpicSubtasks = tm.getEpicSubtasks(epic1);

        assertFalse(listEpicSubtasks.isEmpty(), "Список подзадач не найден.");
        assertTrue(listEpicSubtasks.contains(subtask1), "Подзадача не добавлена в список подзадач эпика.");
    }

    //неизменность всех полей задачи при добавлении в менеджер
    @Test
    void shouldTasksFieldsEqual() {
        assertEquals(taskId, task2.getId(), "Id задач не равны.");
        assertEquals(task1.getName(), task2.getName(), "Имена задач не равны.");
        assertEquals(task1.getDescription(), task2.getDescription(), "Описания задач не равны.");
        assertEquals(task1.getStatus(), task2.getStatus(), "Статус задач разный.");
    }

    //неизменность всех полей эпика при добавлении в менеджер
    @Test
    void shouldEpicsFieldsEqual() {
        assertEquals(epicId, epic2.getId(), "Id эпиков не равны.");
        assertEquals(epic1.getName(), epic2.getName(), "Имена эпиков не равны.");
        assertEquals(epic1.getDescription(), epic2.getDescription(), "Описания эпиков не равны.");
        assertEquals(epic1.getSubtasksId(), epic2.getSubtasksId(), "Списки подзадач эпиков не равны.");
        assertEquals(epic1.getStatus(), epic2.getStatus(), "Статус эпиков разный.");
    }

    //неизменность всех полей подзадачи при добавлении в менеджер
    @Test
    void shouldSubtasksFieldsEqual() {
        assertEquals(subtaskId, subtask2.getId(), "Id подзадач не равны.");
        assertEquals(subtask1.getName(), subtask2.getName(), "Имена подзадач не равны.");
        assertEquals(subtask1.getDescription(), subtask2.getDescription(), "Описания подзадач не равны.");
        assertEquals(subtask1.getEpicId(), subtask2.getEpicId(), "Id эпика подзадач не равны.");
        assertEquals(subtask1.getStatus(), subtask2.getStatus(), "Статус подзадач разный.");
    }

    //проверка, что подзадачи нельзя добавлять в подзадачи
    @Test
    void shouldNotAddSubtaskInSubtask() {
        Subtask subtask3 = new Subtask("подзадача-3", "описание пзд-3", subtask1.getId());
        subtask3 = tm.addSubtasks(subtask3);

        assertEquals(0, subtask3.getId(), "Подзадачу можно добавить в подзадачу.");
    }

    //проверка заполнения истории задач и удаления из истории повторных просмотров задачи
    @Test
    void shouldGetHistory() {
        Task task01 = new Task("задача-1", "описание зд-1");
        TaskManager tm1 = Managers.getDefault();
        task01 = tm1.addTasks(task01);
        tm1.getTaskById(task01.getId());
        tm1.getTaskById(task01.getId());
        List<Task> historyList = tm1.getHistory();

        assertNotNull(historyList, "Список истории просмотров пуст.");
        assertEquals(1, historyList.size(), "Предыдущий просмотр задачи не удаляется.");
    }

    //проверка удаления подзадачи из эпика при удалении самой подзадачи
    @Test
    void shouldDeleteSubtaskFromEpic() {
        tm.removeSubtaskById(subtaskId);
        List<Subtask> listOfSubtasks = tm.getEpicSubtasks(epic1);

        assertTrue(listOfSubtasks.isEmpty(), "Подзадача не удалилась из списка подзадач эпика.");
    }
}
