package file;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvFormat {


    public static String headline() {
        return "id,type,name,status,description,startTime,duration,epicId";
    }

    public static String toString(Task task) {
        return task.getId() + "," +
                TaskType.TASK + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getStartTime() + "," +
                task.getDuration().toMinutes() + ", ";
    }

    public static String toString(Epic epic) {
        return epic.getId() + "," +
                TaskType.EPIC + "," +
                epic.getName() + "," +
                epic.getStatus() + "," +
                epic.getDescription() + "," +
                epic.getStartTime() + "," +
                epic.getDuration().toMinutes() + ", ";
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + "," +
                TaskType.SUBTASK + "," +
                subtask.getName() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getStartTime() + "," +
                subtask.getDuration().toMinutes() + "," +
                subtask.getEpicId();
    }

    public static Task fromString(String value) {
        Task task = new Task();
        LocalDateTime startTime = null;
        Duration duration = Duration.ZERO;
        int id = 0;

        String[] split = value.split(",");
        String index = split[0];
        String name = split[2];
        String status = split[3];
        String description = split[4];
        String start = split[5];
        String span = split[6];
        String epicIndex = split[7];

        if (!start.equals("null") && !start.equals("startTime")) {
            startTime = LocalDateTime.parse(start);
        }

        if (!span.equals("0") && !span.equals("duration")) {
            duration = Duration.ofMinutes(Long.parseLong(span));
        }

        if (!index.equals("id")) {
            id = Integer.parseInt(index);
        }

        switch (split[1]) {
            case "TASK" -> task = new Task(id, name, description, TaskStatus.valueOf(status), startTime, duration);
            case "EPIC" -> task = new Epic(id, name, description, TaskStatus.valueOf(status), startTime, duration);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(epicIndex);
                task = new Subtask(id, name, description, startTime, duration, epicId, TaskStatus.valueOf(status));
            }
        }
        return task;
    }

    public static int findMaxIndex(String value) {
        int id = 1;
        String[] split = value.split(",");
        String index = split[0];

        if (index.equals("id")) {
            return id;
        } else {
            return Math.max(id, Integer.parseInt(index));
        }
    }
}
