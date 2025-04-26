import file.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
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

    //проверка: задача типа Task создаётся в менеджере и добавляется в таблицу задач, задача по Id находится,
    //задача сохраняется в файл
    @Override
    @Test
    void shouldBeAddTasks() {
        super.shouldBeAddTasks();
        assertNotEquals(0L, tempFile.length(), "Данные в файле на сохраняются.");
    }

    //проверка: эпик типа Epic создаётся в менеджере и добавляется в таблицу эпиков, эпик по Id находится,
    //сохраняется в файл
    @Override
    @Test
    void shouldBeAddEpics() {
        super.shouldBeAddEpics();
        assertNotEquals(0L, tempFile.length(), "Данные в файле на сохраняются.");
    }

    //проверка: подзадача типа Subtask создаётся в менеджере и добавляется в таблицу подзадач, подзадача по Id находится,
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
        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30)));
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

    //проверка пробрасываемых исключений
    @Test
    void testException() throws IOException {

        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30)));

        assertNotNull(task1, "Задача не создалась.");
        assertEquals(1, tm.getAllTasks().size(), "Задача не добавилась в менеджер.");

        assertTrue(tempFile.delete(), "Файл не был удалён.");

        assertThrows(ManagerSaveException.class, () -> {
                    tm.testSave();
                }, "Сохранение в несуществующий файл должно приводить к ошибке."
        );

        assertThrows(ManagerSaveException.class, () -> {
            TaskManager newManager = Managers.getDefault(tempFile);
        }, "Нельзя восстановить данные из несуществующего файла.");

    }
}
