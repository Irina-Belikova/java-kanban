import file.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class OptionalClass {
    private static final String FILE_FOR_SAVE = "src/resources/storage.csv";

    public static void main(String[] args) {
        File storageFile = createFile(FILE_FOR_SAVE);
        TaskManager manager = Managers.getDefault(storageFile);

        //Создание простых задач и внесение их в хеш-таблицу(+ присваивание id)
        Task task1 = manager.addTasks(new Task("задача-1", "описание зд-1",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30)));
        Task task2 = manager.addTasks(new Task("задача-2", "описание зд-2",
                LocalDateTime.of(2025, 1, 1, 12, 40), Duration.ofMinutes(30)));

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic1 = manager.addEpics(new Epic("эпик-1", "описание эпика-1"));
        Epic epic2 = manager.addEpics(new Epic("эпик-2", "описание эпика-2"));

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask1 = manager.addSubtasks(new Subtask("подзадача-1", "описание пзд-1",
                LocalDateTime.of(2025, 1, 1, 14, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask2 = manager.addSubtasks(new Subtask("подзадача-2", "описание пзд-2",
                LocalDateTime.of(2025, 1, 2, 12, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask3 = manager.addSubtasks(new Subtask("подзадача-3", "описание пзд-3",
                LocalDateTime.of(2025, 2, 1, 9, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));

        manager.clearTasks();
        manager.clearSubtasks();

        System.out.println("новый менеджер");
        TaskManager newManager = Managers.getDefault(storageFile);
        Task task3 = newManager.addTasks(new Task("задача-3", "описание зд-3",
                LocalDateTime.of(2025, 1, 1, 17, 0), Duration.ofMinutes(30)));
        System.out.println(newManager.getAllTasks());
        System.out.println(newManager.getAllEpics());
        System.out.println(newManager.getAllSubtasks());
    }

    private static File createFile(String fileName) {
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла.");
        }
        return file;
    }
}
