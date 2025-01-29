
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager tm = Managers.getDefault();
        Task upTask;
        Subtask upSubtask;
        Epic upEpic;

        //Создание простых задач и внесение их в хеш-таблицу(+ присваивание id)
        Task task_1 = new Task("задача-1", "описание зд-1");
        Task task_2 = new Task("задача-2", "описание зд-2");
        task_1 = tm.addTasks(task_1);
        task_2 = tm.addTasks(task_2);

        //Создание эпиков и внесение их в хеш-таблицу(+ присваивание id)
        Epic epic_1 = new Epic("эпик-1", "описание эпика -1");
        Epic epic_2 = new Epic("эпик-2", "описание эпика -2");
        Epic epic_3 = new Epic("эпик-3", "описание эпика -3");
        epic_1 = tm.addEpics(epic_1);
        epic_2 = tm.addEpics(epic_2);
        epic_3 = tm.addEpics(epic_3);

        //Создание подзадач, внесение их в хеш-таблицу(+ id) и связывание по id с эпиками(обмен id)
        Subtask subtask_1 = new Subtask("подзадача-1", "описание пзд-1", epic_1.getId());
        Subtask subtask_2 = new Subtask("подзадача-2", "описание пзд-2", epic_1.getId());
        Subtask subtask_3 = new Subtask("подзадача-3", "описание пзд-3", epic_1.getId());
        Subtask subtask_4 = new Subtask("подзадача-4", "описание пзд-4", epic_2.getId());
        Subtask subtask_5 = new Subtask("подзадача-5", "описание пзд-5", epic_2.getId());
        Subtask subtask_6 = new Subtask("подзадача-6", "описание пзд-6", epic_3.getId());
        subtask_1 = tm.addSubtasks(subtask_1);
        subtask_2 = tm.addSubtasks(subtask_2);
        subtask_3 = tm.addSubtasks(subtask_3);
        subtask_4 = tm.addSubtasks(subtask_4);
        subtask_5 = tm.addSubtasks(subtask_5);
        subtask_6 = tm.addSubtasks(subtask_6);

        System.out.println("Получение списка всех задач.");
        printAllTasks(tm);

        System.out.println("\nПолучение списка подзадач заданного эпика.");
        System.out.println(tm.getEpicSubtasks(epic_2));
        System.out.println(tm.getEpicSubtasks(epic_1));
        System.out.println(tm.getEpicSubtasks(epic_3));

        System.out.println("\nПолучение по идентификатору.");
        System.out.println(tm.getTaskById(task_2.getId()));
        System.out.println(tm.getSubtaskById(subtask_5.getId()));
        System.out.println(tm.getEpicById(epic_1.getId()));

        System.out.println("\nВнесение изменений в простые задачи, подзадачи и эпики. Печать истории просмотров.");
        upTask = new Task(task_1.getId(), "задача-1", "описание зд-1", TaskStatus.IN_PROGRESS);
        task_1 = tm.changeTask(upTask);

        upTask = new Task(task_2.getId(), "задача-2", "новое описание зд-2", TaskStatus.DONE);
        task_2 = tm.changeTask(upTask);

        upSubtask = new Subtask(subtask_1.getId(), "подзадача-1", "описание пзд-1", epic_1.getId(), TaskStatus.DONE);
        subtask_1 = tm.changeSubtask(upSubtask);
        upSubtask = new Subtask(subtask_2.getId(), "подзадача-2", "новое описание пзд-2", epic_1.getId(), TaskStatus.DONE);
        subtask_2 = tm.changeSubtask(upSubtask);
        upSubtask = new Subtask(subtask_3.getId(), "подзадача-3", "описание пзд-3", epic_1.getId(), TaskStatus.NEW);
        subtask_3 = tm.changeSubtask(upSubtask);

        upSubtask = new Subtask(subtask_6.getId(), "подзадача-6", "новое описание пзд-6", epic_3.getId(), TaskStatus.DONE);
        subtask_6 = tm.changeSubtask(upSubtask);

        upSubtask = new Subtask(subtask_4.getId(), "подзадача-4", "новое описание пзд-4", epic_2.getId(), TaskStatus.DONE);
        subtask_4 = tm.changeSubtask(upSubtask);
        upSubtask = new Subtask(subtask_5.getId(), "подзадача-5", "новое описание пзд-5", epic_2.getId(), TaskStatus.NEW);
        subtask_5 = tm.changeSubtask(upSubtask);
        upEpic = new Epic(epic_2.getId(), "новое имя для эпик-2", "новое описание эпика -2", epic_2.getSubtasksId());
        upEpic.setStatus(TaskStatus.DONE);
        epic_2 = tm.changeEpic(upEpic);

        tm.getTaskById(task_2.getId());
        tm.getSubtaskById(subtask_5.getId());
        tm.getEpicById(epic_1.getId());
        printAllTasks(tm);

        System.out.println("\nУдаление по идентификатору.");
        tm.removeTaskById(task_1.getId());
        tm.removeSubtaskById(subtask_3.getId());
        tm.removeEpicById(epic_2.getId());
        printAllTasks(tm);

        System.out.println("\nУдаление всех задач.");
        tm.clearTasks();
        tm.clearEpics();
        tm.clearSubtasks();
        printAllTasks(tm);
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

        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
