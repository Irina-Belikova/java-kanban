package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    ArrayList<Subtask> list;
    private final HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> mapOfSubtasks = new HashMap<>();
    private final HashMap<Integer, Epic> mapOfEpics = new HashMap<>();
    private static int id = 1;

    private int generateId() {
        return id++;
    }

    public Task addTasks(Task task) {
        if (task.getId() != 0) {
            return task;
        }
        task.setId(generateId());
        mapOfTasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpics(Epic epic) {
        if (epic.getId() != 0) {
            return epic;
        }
        epic.setId(generateId());
        mapOfEpics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtasks(Subtask subtask) {
        if (subtask.getId() != 0 || subtask.getEpicId() == 0) {
            return subtask;
        }
        subtask.setId(generateId());
        mapOfSubtasks.put(subtask.getId(), subtask);
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());
        return subtask;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return mapOfTasks;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return mapOfSubtasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return mapOfEpics;
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        list = new ArrayList<>();
        for (int i : epic.getSubtasksId()) {
            if (mapOfSubtasks.containsKey(i)) {
                list.add(mapOfSubtasks.get(i));
            }
        }
        return list;
    }

    public Task getTaskById(int id) {
        return mapOfTasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return mapOfSubtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return mapOfEpics.get(id);
    }

    public void clearTasks() {
        mapOfTasks.clear();
    }

    public void clearSubtasks() {
        mapOfSubtasks.clear();
    }

    public void clearEpics() {
        mapOfEpics.clear();
    }

    public void removeTaskById(int id) {
        mapOfTasks.remove(id);
    }

    public void removeSubtaskById(Integer id) {
        Subtask subtask = mapOfSubtasks.get(id);
        int index = subtask.getEpicId();
        Epic epic = mapOfEpics.get(index);
        ArrayList<Integer> listId = epic.getSubtasksId();
        listId.remove(id);
        mapOfSubtasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = mapOfEpics.get(id);
        for (int i : epic.getSubtasksId()) {
            if (mapOfSubtasks.containsKey(i)) {
                mapOfSubtasks.remove(i);
            }
        }
        mapOfEpics.remove(id);
    }

    public Task changeTask(Task task) {
        if (!mapOfTasks.containsKey(task.getId())) {
            return task;
        }
        mapOfTasks.put(task.getId(), task);
        return task;
    }

    public Subtask changeSubtask(Subtask subtask) {
        if (!mapOfSubtasks.containsKey(subtask.getId())) {
            return subtask;
        }
        mapOfSubtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public Epic changeEpic(Epic epic) {
        if (!mapOfEpics.containsKey(epic.getId())) {
            epic.setStatus(TaskStatus.NEW);
            return epic;
        }
        if (epic.getSubtasksId().isEmpty() && mapOfEpics.containsKey(epic.getId())) {
            epic.setStatus(TaskStatus.NEW);
            mapOfEpics.put(epic.getId(), epic);
            return epic;
        }
        for (int i : epic.getSubtasksId()) {
            if (mapOfSubtasks.containsKey(i)) {
                list.add(mapOfSubtasks.get(i));
            }
            for (Subtask sub : list) {
                if (sub.getStatus() == TaskStatus.DONE) {
                    epic.setStatus(TaskStatus.DONE);
                } else if (sub.getStatus() == TaskStatus.IN_PROGRESS) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    mapOfEpics.put(epic.getId(), epic);
                    return epic;
                } else {
                    epic.setStatus(TaskStatus.NEW);
                }
            }
        }
        mapOfEpics.put(epic.getId(), epic);
        return epic;
    }
}



