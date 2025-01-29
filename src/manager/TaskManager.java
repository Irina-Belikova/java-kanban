package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task addTasks(Task task);

    Epic addEpics(Epic epic);

    Subtask addSubtasks(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getEpicSubtasks(Epic epic);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    Task changeTask(Task task);

    Subtask changeSubtask(Subtask subtask);

    Epic changeEpic(Epic newEpic);

    ArrayList<Task> getHistory();
}
