package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
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
        if (subtask.getId() != 0 || subtask.getEpicId() == 0 || !mapOfEpics.containsKey(subtask.getEpicId())) {
            return subtask; //надеюсь правильно поняла, что нужно проверить, что эпик есть в таблице (тогда он существует)
        }
        subtask.setId(generateId());
        mapOfSubtasks.put(subtask.getId(), subtask);
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());
        checkEpicStatus(epic);
        return subtask;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        tasksList.addAll(mapOfTasks.values());
        return tasksList;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        subtasksList.addAll(mapOfSubtasks.values());
        return subtasksList;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        epicsList.addAll(mapOfEpics.values());
        return epicsList;
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> list = new ArrayList<>();
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
        for (int id : mapOfEpics.keySet()) {
            Epic epic = mapOfEpics.get(id);
            epic.getSubtasksId().clear();
            epic.setStatus(TaskStatus.NEW);
        }
        mapOfSubtasks.clear();
    }

    public void clearEpics() {
        mapOfSubtasks.clear();
        mapOfEpics.clear();
    }

    public void removeTaskById(int id) {
        mapOfTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = mapOfSubtasks.get(id);
        int index = subtask.getEpicId();
        Epic epic = mapOfEpics.get(index);
        epic.deleteSubtask(id);
        mapOfSubtasks.remove(id);
        checkEpicStatus(epic);
    }

    public void removeEpicById(int id) {
        Epic epic = mapOfEpics.get(id);
        for (int index : epic.getSubtasksId()) {
            mapOfSubtasks.remove(index);
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
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        checkEpicStatus(epic);
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
        checkEpicStatus(epic);
        return epic;
    }

    private void checkEpicStatus(Epic epic) {         //полностью переделала определение статуса эпика, так как
        ArrayList<Subtask> list = new ArrayList<>();  //поняла, что первый вариант не все вероятности проверял,
        for (int index : epic.getSubtasksId()) {          //например, 1 подзадача NEW, а остальные DONE.
            list.add(mapOfSubtasks.get(index));
        }
        int aDone = 0;
        int aNew = 0;
        for (Subtask sub : list) {
            if (sub.getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                mapOfEpics.put(epic.getId(), epic);
                break;
            } else if (sub.getStatus() == TaskStatus.DONE) {
                aDone += 1;
            } else {
                aNew += 1;
            }
        }
        if (aDone == list.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (aNew == list.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        mapOfEpics.put(epic.getId(), epic);
    }
}
