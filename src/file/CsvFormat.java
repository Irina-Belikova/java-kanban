package file;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

public class CsvFormat {

    public static String headline() {
        return "id,type,name,status,description,epicId\n";
    }

    public static String toString(Task task) {
        return task.getId() + "," +
                TaskType.TASK + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ", " + "\n";
    }

    public static String toString(Epic epic) {
        return epic.getId() + "," +
                TaskType.EPIC + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription() + ", " + "\n";
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + "," +
                TaskType.SUBTASK + "," +
                subtask.getName() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicId() + "\n";
    }
}
