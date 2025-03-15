package manager;

import file.CsvFormat;
import file.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            if (!mapOfTasks.isEmpty() || !mapOfEpics.isEmpty() || !mapOfSubtasks.isEmpty()) {
                writer.write(CsvFormat.headline());
            }

            for (Map.Entry<Integer, Task> entry : mapOfTasks.entrySet()) {
                Task task = entry.getValue();
                writer.write(CsvFormat.toString(task));
            }

            for (Map.Entry<Integer, Epic> entry : mapOfEpics.entrySet()) {
                Epic epic = entry.getValue();
                writer.write(CsvFormat.toString(epic));
            }

            for (Map.Entry<Integer, Subtask> entry : mapOfSubtasks.entrySet()) {
                Subtask subtask = entry.getValue();
                writer.write(CsvFormat.toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка работы с файлом.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager result = new FileBackedTaskManager(file);
        List<Integer> findMax = new ArrayList<>();

        if (file.length() == 0) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] split = line.split(",");
                String index = split[0];
                String name = split[2];
                String status = split[3];
                String description = split[4];
                String epicIndex = split[5];

                switch (split[1]) {
                    case "TASK":
                        int taskId = Integer.parseInt(index);
                        Task task = new Task(taskId, name, description, TaskStatus.valueOf(status));
                        mapOfTasks.put(taskId, task);
                        findMax.add(taskId);
                        break;
                    case "EPIC":
                        int epcId = Integer.parseInt(index);
                        Epic epic = new Epic(epcId, name, description, TaskStatus.valueOf(status));
                        mapOfEpics.put(epcId, epic);
                        findMax.add(epcId);
                        break;
                    case "SUBTASK":
                        int subtaskId = Integer.parseInt(index);
                        int epicId = Integer.parseInt(epicIndex);
                        Subtask subtask = new Subtask(subtaskId, name, description, epicId, TaskStatus.valueOf(status));
                        mapOfSubtasks.put(subtaskId, subtask);
                        findMax.add(subtaskId);
                        Epic epic1 = mapOfEpics.get(epicId);
                        epic1.getSubtasksId().add(subtaskId);
                        break;
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка работы с файлом.");
        }
        id = Collections.max(findMax) + 1;
        return result;
    }

    @Override
    public Task addTasks(Task task) {
        Task aTask = super.addTasks(task);
        save();
        return aTask;
    }

    @Override
    public Epic addEpics(Epic epic) {
        Epic anEpic = super.addEpics(epic);
        save();
        return anEpic;
    }

    @Override
    public Subtask addSubtasks(Subtask subtask) {
        Subtask aSubtask = super.addSubtasks(subtask);
        save();
        return aSubtask;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public Task changeTask(Task task) {
        Task aTask = super.changeTask(task);
        save();
        return aTask;
    }

    @Override
    public Subtask changeSubtask(Subtask subtask) {
        Subtask aSubtask = super.changeSubtask(subtask);
        save();
        return aSubtask;
    }

    @Override
    public Epic changeEpic(Epic newEpic) {
        Epic anEpic = super.changeEpic(newEpic);
        save();
        return anEpic;
    }
}

class OptionalTask {

    private static final String FILE_FOR_SAVE = "src/resources/storage.csv";

    public static void main(String[] args) {
        File storageFile = createFile(FILE_FOR_SAVE);
        TaskManager manager = Managers.getDefault(storageFile);

        //Создание простых задач и внесение их в хеш-таблицу(+ присваивание id)
        Task task1 = manager.addTasks(new Task("задача-1", "описание зд-1"));
        Task task2 = manager.addTasks(new Task("задача-2", "описание зд-2"));

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic1 = manager.addEpics(new Epic("эпик-1", "описание эпика-1"));
        Epic epic2 = manager.addEpics(new Epic("эпик-2", "описание эпика-2"));

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask1 = manager.addSubtasks(new Subtask("подзадача-1", "описание пзд-1", epic1.getId()));
        Subtask subtask2 = manager.addSubtasks(new Subtask("подзадача-2", "описание пзд-2", epic1.getId()));
        Subtask subtask3 = manager.addSubtasks(new Subtask("подзадача-3", "описание пзд-3", epic1.getId()));

        manager.clearTasks();
        manager.clearSubtasks();

        System.out.println("новый менеджер");
        TaskManager newManager = Managers.getDefault(storageFile);
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
