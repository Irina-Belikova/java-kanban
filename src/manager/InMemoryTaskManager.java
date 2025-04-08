package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> mapOfTasks = new HashMap<>();
    protected final Map<Integer, Subtask> mapOfSubtasks = new HashMap<>();
    protected final Map<Integer, Epic> mapOfEpics = new HashMap<>();
    protected final TreeSet<Task> treeOfTasks = new TreeSet<>((t1, t2) ->
            t1.getStartTime().compareTo(t2.getStartTime()));

    protected int id = 1;
    protected final HistoryManager hm = Managers.getHistory();

    private int generateId() {
        return id++;
    }

    @Override
    public Task addTasks(Task task) {
        if (task.getId() != 0) {
            return task;
        }
        if (task.getStartTime() == null) {
            task.setId(generateId());
            mapOfTasks.put(task.getId(), task);
            return task;
        }
        getPrioritizedTasks();

        if (!isCrossTask(task) || treeOfTasks.isEmpty()) {
            task.setId(generateId());
            mapOfTasks.put(task.getId(), task);
        }
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
        getPrioritizedTasks();
        boolean isNotAddSubtask;

        if (subtask.getStartTime() == null || treeOfTasks.isEmpty()) {
            isNotAddSubtask = false;
        } else {
            isNotAddSubtask = isCrossTask(subtask);
        }

        if (!isNotAddSubtask) {
            subtask.setId(generateId());
            mapOfSubtasks.put(subtask.getId(), subtask);
            Epic epic = mapOfEpics.get(subtask.getEpicId());
            epic.getSubtasksId().add(subtask.getId());
            checkEpicStatus(epic);
            checkEpicTime(epic);
        }
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
        return epic.getSubtasksId().stream()
                .map(mapOfSubtasks::get)
                .toList();
    }

    @Override
    public Task getTaskById(int id) {
        Optional<Task> task = Optional.ofNullable(mapOfTasks.get(id));
        task.ifPresent(hm::add);
        return task.orElse(null);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Optional<Subtask> subtask = Optional.ofNullable(mapOfSubtasks.get(id));
        subtask.ifPresent(hm::add);
        return subtask.orElse(null);
    }

    @Override
    public Epic getEpicById(int id) {
        Optional<Epic> epic = Optional.ofNullable(mapOfEpics.get(id));
        epic.ifPresent(hm::add);
        return epic.orElse(null);
    }

    @Override
    public void clearTasks() {
        mapOfTasks.keySet().forEach(id -> hm.remove(id));
        mapOfTasks.clear();
        getPrioritizedTasks();
    }

    @Override
    public void clearSubtasks() {
        mapOfEpics.values().forEach(epic -> {
            epic.getSubtasksId().clear();
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
        });
        mapOfSubtasks.keySet().forEach(hm::remove);
        mapOfSubtasks.clear();
        getPrioritizedTasks();
    }

    @Override
    public void clearEpics() {
        mapOfEpics.keySet().forEach(id -> hm.remove(id)); //не стала использовать ссылку на метод (как в стоке ниже),
        mapOfSubtasks.keySet().forEach(hm::remove);              //чтобы понимание такой записи тоже было
        mapOfSubtasks.clear();
        mapOfEpics.clear();
        getPrioritizedTasks();
    }

    @Override
    public void removeTaskById(int id) {
        mapOfTasks.remove(id);
        hm.remove(id);
        getPrioritizedTasks();
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = mapOfSubtasks.get(id);
        int index = subtask.getEpicId();
        Epic epic = mapOfEpics.get(index);
        epic.deleteSubtask(id);
        mapOfSubtasks.remove(id);
        checkEpicStatus(epic);
        checkEpicTime(epic);
        hm.remove(id);
        getPrioritizedTasks();
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
        getPrioritizedTasks();
    }

    @Override
    public Task changeTask(Task task) {
        if (!mapOfTasks.containsKey(task.getId())) {
            return task;
        }
        treeOfTasks.remove(mapOfTasks.get(task.getId()));

        if (!isCrossTask(task)) {
            mapOfTasks.put(task.getId(), task);
        }
        getPrioritizedTasks();
        return task;
    }

    @Override
    public Subtask changeSubtask(Subtask subtask) {
        treeOfTasks.remove(mapOfSubtasks.get(subtask.getId()));

        if (!mapOfSubtasks.containsKey(subtask.getId()) || isCrossTask(subtask)) {
            return subtask;
        }
        mapOfSubtasks.put(subtask.getId(), subtask);
        Epic epic = mapOfEpics.get(subtask.getEpicId());
        checkEpicStatus(epic);
        checkEpicTime(epic);
        getPrioritizedTasks();
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
        List<Subtask> list = new ArrayList<>();
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

    private void checkEpicTime(Epic epic) {
        List<Subtask> list = getEpicSubtasks(epic);
        list = list.stream().filter(subtask -> Objects.nonNull(subtask.getStartTime()))
                .filter(subtask -> Objects.nonNull(subtask.getDuration()))
                .toList();

        Duration duration = list.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(duration);

        Optional<LocalDateTime> start = list.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo);
        start.ifPresent(epic::setStartTime);

        Optional<LocalDateTime> end = list.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo);
        end.ifPresent(epic::setEndTime);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        mapOfTasks.values().stream()
                .filter(task -> Objects.nonNull(task.getStartTime()))
                .forEach(treeOfTasks::add);
        mapOfSubtasks.values().stream()
                .filter(subtask -> Objects.nonNull(subtask.getStartTime()))
                .forEach(treeOfTasks::add);
        return treeOfTasks;
    }

    private boolean checkCrossTime(Task newTask, Task task) {
        boolean isCross = true;
        if (newTask.getStartTime().isAfter(task.getEndTime()) || newTask.getEndTime().isBefore(task.getStartTime())) {
            isCross = false;
        }
        return isCross;
    }

    private boolean isCrossTask(Task task) {
        return treeOfTasks.stream()
                .anyMatch(treeTask -> checkCrossTime(task, treeTask));
    }
}
