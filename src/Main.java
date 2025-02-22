
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
        Task task_1 = new Task("задача-1", "описание зд-1");
        Task task_2 = new Task("задача-2", "описание зд-2");
        task_1 = tm.addTasks(task_1);
        task_2 = tm.addTasks(task_2);

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic_1 = new Epic("эпик-1", "описание эпика -1");
        Epic epic_2 = new Epic("эпик-2", "описание эпика -2");
        epic_1 = tm.addEpics(epic_1);
        epic_2 = tm.addEpics(epic_2);

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask_1 = new Subtask("подзадача-1", "описание пзд-1", epic_1.getId());
        Subtask subtask_2 = new Subtask("подзадача-2", "описание пзд-2", epic_1.getId());
        Subtask subtask_3 = new Subtask("подзадача-3", "описание пзд-3", epic_1.getId());
        subtask_1 = tm.addSubtasks(subtask_1);
        subtask_2 = tm.addSubtasks(subtask_2);
        subtask_3 = tm.addSubtasks(subtask_3);

        System.out.println("\nПроверка, что epic_1 удалится из середины и встанет в конец истории.");
        tm.getTaskById(task_2.getId());
        tm.getSubtaskById(subtask_1.getId());
        tm.getEpicById(epic_1.getId());
        tm.getTaskById(task_1.getId());
        tm.getEpicById(epic_1.getId());
        printAllTasks(tm);

        System.out.println("\nПроверка, что subtask_1 удалится из середины и встанет в конец истории.");
        tm.getEpicById(epic_2.getId());
        tm.getSubtaskById(subtask_3.getId());
        tm.getSubtaskById(subtask_1.getId());
        printAllTasks(tm);

        tm.getEpicById(epic_1.getId());
        tm.getSubtaskById(subtask_2.getId());
        tm.getSubtaskById(subtask_3.getId());

        System.out.println("\nФинальный вариант списка истории.");
        printAllTasks(tm);

        System.out.println("\nУдаление задачи и подзадачи.");
        tm.removeTaskById(task_1.getId());
        tm.removeSubtaskById(subtask_2.getId());
        printAllTasks(tm);

        System.out.println("\nУдаление эпика с подзадачами.");
        tm.removeEpicById(epic_1.getId());
        printAllTasks(tm);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
