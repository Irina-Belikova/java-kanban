package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> mapOfTasks = new HashMap<>();
    private final Map<Integer, Subtask> mapOfSubtasks = new HashMap<>();
    private final Map<Integer, Epic> mapOfEpics = new HashMap<>();
    private static int id = 1;
    private final HistoryManager hm = Managers.getHistory();

    private int generateId() {
        return InMemoryTaskManager.id++;
    }

    @Override
    public Task addTasks(Task task) {
        if (task.getId() != 0) {
            return task;
        }
        task.setId(generateId());
        mapOfTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpics(Epic epic) {
        if (epic.getId() != 0) {
            return epic;
        }
        epic.setId(generateId());
        mapOfEpics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtasks(Subtask subtask) {
        if (subtask.getId() != 0 || subtask.getEpicId() == 0 || !mapOfEpics.containsKey(subtask.getEpicId())) {
            return subtask;
        }
        subtask.setId(generateId());
        mapOfSubtasks.put(subtask.getId(), subtask);
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        epic.getSubtasksId().add(subtask.getId());
        checkEpicStatus(epic);
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(mapOfTasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(mapOfSubtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(mapOfEpics.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> list = new ArrayList<>();
        for (int i : epic.getSubtasksId()) {
            if (mapOfSubtasks.containsKey(i)) {
                list.add(mapOfSubtasks.get(i));
            }
        }
        return list;
    }

    @Override
    public Task getTaskById(int id) {
        hm.add(mapOfTasks.get(id));
        return mapOfTasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        hm.add(mapOfSubtasks.get(id));
        return mapOfSubtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        hm.add(mapOfEpics.get(id));
        return mapOfEpics.get(id);
    }

    @Override
    public void clearTasks() {
        mapOfTasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (int id : mapOfEpics.keySet()) {
            Epic epic = mapOfEpics.get(id);
            epic.getSubtasksId().clear();
            epic.setStatus(TaskStatus.NEW);
        }
        mapOfSubtasks.clear();
    }

    @Override
    public void clearEpics() {
        mapOfSubtasks.clear();
        mapOfEpics.clear();
    }

    @Override
    public void removeTaskById(int id) {
        mapOfTasks.remove(id);
        hm.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = mapOfSubtasks.get(id);
        int index = subtask.getEpicId();
        Epic epic = mapOfEpics.get(index);
        epic.deleteSubtask(id);
        mapOfSubtasks.remove(id);
        checkEpicStatus(epic);
        hm.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = mapOfEpics.get(id);
        for (int index : epic.getSubtasksId()) {
            mapOfSubtasks.remove(index);
            hm.remove(index);
        }
        mapOfEpics.remove(id);
        hm.remove(id);
    }

    @Override
    public Task changeTask(Task task) {
        if (!mapOfTasks.containsKey(task.getId())) {
            return task;
        }
        mapOfTasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask changeSubtask(Subtask subtask) {
        if (!mapOfSubtasks.containsKey(subtask.getId())) {
            return subtask;
        }
        mapOfSubtasks.put(subtask.getId(), subtask);
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        checkEpicStatus(epic);
        return subtask;
    }

    @Override
    public Epic changeEpic(Epic newEpic) {
        if (!mapOfEpics.containsKey(newEpic.getId())) {
            return newEpic;
        }
        Epic epic = mapOfEpics.get(newEpic.getId());
        epic.setName(newEpic.getName());
        epic.setDescription(newEpic.getDescription());
        return epic;
    }

    private void checkEpicStatus(Epic epic) {
        ArrayList<Subtask> list = new ArrayList<>();
        for (int index : epic.getSubtasksId()) {
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

    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }
}
