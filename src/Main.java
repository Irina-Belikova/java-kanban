
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager tm = Managers.getDefault();

        //Создание простых задач и внесение их в хеш-таблицу(+ присваивание id)
        Task task1 = new Task("задача-1", "описание зд-1", LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30));
        Task task2 = new Task("задача-2", "описание зд-2", LocalDateTime.of(2025, 1, 1, 12, 20), Duration.ofMinutes(30));
        Task task3 = new Task("задача-3", "описание зд-3");
        Task task4 = new Task("задача-4", "описание зд-4", LocalDateTime.of(2025, 1, 3, 12, 20), Duration.ofMinutes(30));
        task1 = tm.addTasks(task1);
        task2 = tm.addTasks(task2);
        task3 = tm.addTasks(task3);
        task4 = tm.addTasks(task4);

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic1 = new Epic("эпик-1", "описание эпика-1");
        Epic epic2 = new Epic("эпик-2", "описание эпика-2");
        epic1 = tm.addEpics(epic1);
        epic2 = tm.addEpics(epic2);

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask1 = new Subtask("подзадача-1", "описание пзд-1",
                LocalDateTime.of(2025, 1, 1, 12, 40), Duration.ofMinutes(30), epic1.getId());
        Subtask subtask2 = new Subtask("подзадача-2", "описание пзд-2",
                LocalDateTime.of(2025, 1, 2, 12, 0), Duration.ofMinutes(30), epic1.getId());
        Subtask subtask3 = new Subtask("подзадача-3", "описание пзд-3",
                LocalDateTime.of(2025, 1, 1, 9, 0), Duration.ofMinutes(30), epic1.getId());
        Subtask subtask4 = new Subtask("подзадача-4", "описание пзд-4", epic1.getId());
        subtask1 = tm.addSubtasks(subtask1);
        subtask2 = tm.addSubtasks(subtask2);
        subtask3 = tm.addSubtasks(subtask3);
        subtask4 = tm.addSubtasks(subtask4);

        Task upTask = new Task(task2.getId(), "задача-2", "описание зд-2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 1, 12, 20), Duration.ofMinutes(30));
        task2 = tm.changeTask(upTask);
        Subtask upSubtask = new Subtask(subtask3.getId(), "подзадача-3", "описание пзд-3",
                LocalDateTime.of(2025, 1, 1, 9, 50), Duration.ofMinutes(30),
                epic1.getId(), TaskStatus.IN_PROGRESS);
        subtask3 = tm.changeSubtask(upSubtask);
        Task newTask = tm.getTaskById(1);
        Epic newEpic = tm.getEpicById(epic1.getId());
        Subtask newSubtask = tm.getSubtaskById(subtask3.getId());

        printAllTasks(tm);

        System.out.println("\n" + tm.getSubtaskById(subtask3.getId()));
        System.out.println(tm.getEpicById(epic2.getId()));
        System.out.println(tm.getEpicById(1));

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getEpicSubtasks(epic)) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("\nПорядок выполнения:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
