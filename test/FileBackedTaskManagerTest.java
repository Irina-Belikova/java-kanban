import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    static File tmpFile;

    static {
        try {
            tmpFile = createTempFile("storage", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static TaskManager manager = Managers.getDefault(tmpFile);
    Task task1;
    Epic epic1;
    Subtask subtask1;

    //проверка восстановления данных из файла
    @Test
    void shouldBeLoadFromFile() {
        task1 = manager.addTasks(new Task("задача-1", "описание зд-1"));
        TaskManager newManager = Managers.getDefault(tmpFile);
        List<Task> tasks = newManager.getAllTasks();

        assertNotNull(tasks, "Данные не восстановились.");
    }

    //проверка создания всех задач и сохранения данных в файл
    @Test
    void shouldBeAddAllTypeOfTasksAndSaveInFile() {
        task1 = manager.addTasks(new Task("задача-1", "описание зд-1"));
        epic1 = manager.addEpics(new Epic("эпик-1", "описание эпика -1"));
        subtask1 = manager.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertNotEquals(0L, tmpFile.length(), "Данные в файле на сохраняются.");
    }

    //проверка удаления всех типов задач и получения пустого файла
    @Test
    void shouldBeClearAllTypeOfTasks() throws IOException {
        task1 = manager.addTasks(new Task("задача-1", "описание зд-1"));
        epic1 = manager.addEpics(new Epic("эпик-1", "описание эпика -1"));
        subtask1 = manager.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        manager.clearTasks();
        manager.clearEpics();
        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(0, tasks.size(), "Все задачи не удаляются.");
        assertEquals(0, epics.size(), "Все эпики не удаляются.");
        assertEquals(0, subtasks.size(), "После удаления эпика его подзадачи не удалились.");

        try (BufferedReader reader = new BufferedReader(new FileReader(tmpFile))) {
            assertNull(reader.readLine(), "Данные из файла не удалились.");
        }
    }
}
