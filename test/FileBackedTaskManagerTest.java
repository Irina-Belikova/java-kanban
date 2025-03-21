import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File tempFile;

    protected FileBackedTaskManagerTest() throws IOException {
    }

    @Override
    protected TaskManager createManager() throws IOException {
        tempFile = File.createTempFile("storage", ".csv");
        return Managers.getDefault(tempFile);
    }

    //Проверка: задача типа Task создаётся в менеджере и добавляется в таблицу задач, задача по Id находится,
    //задача сохраняется в файл
    @Override
    @Test
    void shouldBeAddTasks() {
        super.shouldBeAddTasks();
        assertNotEquals(0L, tempFile.length(), "Данные в файле на сохраняются.");
    }

    //Проверка: эпик типа Epic создаётся в менеджере и добавляется в таблицу эпиков, эпик по Id находится,
    //сохраняется в файл
    @Override
    @Test
    void shouldBeAddEpics() {
        super.shouldBeAddEpics();
        assertNotEquals(0L, tempFile.length(), "Данные в файле на сохраняются.");
    }

    //Проверка: подзадача типа Subtask создаётся в менеджере и добавляется в таблицу подзадач, подзадача по Id находится,
    //подзадача добавляется в список подзадач эпика, список подзадач эпика не пустой, сохраняется в файл
    @Override
    @Test
    void shouldBeAddSubtasks() {
        super.shouldBeAddSubtasks();
        assertNotEquals(0L, tempFile.length(), "Данные в файле на сохраняются.");
    }

    //проверка восстановления данных из файла
    @Test
    void shouldBeLoadFromFile() {
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1"));
        TaskManager newManager = Managers.getDefault(tempFile);
        List<Task> tasks = newManager.getAllTasks();

        assertNotNull(tasks, "Данные не восстановились.");
    }

    //проверка удаления подзадач, если их эпик удаляется и получения пустого файла
    @Override
    @Test
    void shouldClearSubtasksIfTheirEpicClear() throws IOException {
        super.shouldClearSubtasksIfTheirEpicClear();

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            assertNull(reader.readLine(), "Данные из файла не удалились.");
        }
    }
}
