package manager;

import file.CsvFormat;
import file.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
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

        if (file.length() == 0) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] split = line.split(",");
                Task task = CsvFormat.fromString(line);
                result.id = CsvFormat.findMaxIndex(line) + 1;

                switch (split[1]) {
                    case "TASK" -> result.mapOfTasks.put(task.getId(), task);
                    case "EPIC" -> result.mapOfEpics.put(task.getId(), (Epic) task);
                    case "SUBTASK" -> {
                        result.mapOfSubtasks.put(task.getId(), (Subtask) task);
                        Subtask subtask = (Subtask) task;
                        Epic epic1 = result.mapOfEpics.get(subtask.getEpicId());
                        epic1.getSubtasksId().add(subtask.getId());
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка работы с файлом.");
        }
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
