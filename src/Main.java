
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager tm = Managers.getDefault();

        //Создание простых задач и внесение их в хеш-таблицу(+ присваивание id)
        Task task1 = new Task("задача-1", "описание зд-1");
        Task task2 = new Task("задача-2", "описание зд-2");
        task1 = tm.addTasks(task1);
        task2 = tm.addTasks(task2);

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic1 = new Epic("эпик-1", "описание эпика-1");
        Epic epic2 = new Epic("эпик-2", "описание эпика-2");
        epic1 = tm.addEpics(epic1);
        epic2 = tm.addEpics(epic2);

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask1 = new Subtask("подзадача-1", "описание пзд-1", epic1.getId());
        Subtask subtask2 = new Subtask("подзадача-2", "описание пзд-2", epic1.getId());
        Subtask subtask3 = new Subtask("подзадача-3", "описание пзд-3", epic1.getId());
        subtask1 = tm.addSubtasks(subtask1);
        subtask2 = tm.addSubtasks(subtask2);
        subtask3 = tm.addSubtasks(subtask3);

        System.out.println("\nПроверка, что epic1 удалится из середины и встанет в конец истории.");
        tm.getTaskById(task2.getId());
        tm.getSubtaskById(subtask1.getId());
        tm.getEpicById(epic1.getId());
        tm.getTaskById(task1.getId());
        tm.getEpicById(epic1.getId());
        printAllTasks(tm);

        System.out.println("\nПроверка, что subtask_1 удалится из середины и встанет в конец истории.");
        tm.getEpicById(epic2.getId());
        tm.getSubtaskById(subtask3.getId());
        tm.getSubtaskById(subtask1.getId());
        printAllTasks(tm);

        tm.getEpicById(epic1.getId());
        tm.getSubtaskById(subtask2.getId());
        tm.getSubtaskById(subtask3.getId());

        System.out.println("\nФинальный вариант списка истории.");
        printAllTasks(tm);

        System.out.println("\nУдаление задачи и подзадачи.");
        tm.removeTaskById(task1.getId());
        tm.removeSubtaskById(subtask2.getId());
        printAllTasks(tm);

        System.out.println("\nУдаление эпика с подзадачами.");
        tm.removeEpicById(epic1.getId());
        printAllTasks(tm);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
