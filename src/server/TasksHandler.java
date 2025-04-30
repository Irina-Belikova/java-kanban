package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import file.ManagerSaveException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] split = path.split("/");

        if (split.length == 2) {
            handleAllTasks(exchange, method);
        } else if (split.length == 3) {
            Optional<Integer> optId = getTaskId(split[2]);
            if (optId.isEmpty()) {
                sendNotFound(exchange, "Неверный формат id задачи.");
            } else {
                handleSingleTask(exchange, method, optId.get());
            }
        }
    }

    private void handleAllTasks(HttpExchange exchange, String method) throws IOException {
        try {
            switch (method) {
                case GET_TASK -> {
                    List<Task> tasks = manager.getAllTasks();
                    String jsonString = gson.toJson(tasks);
                    sendText(exchange, jsonString);
                }
                case POST_TASK -> {
                    try {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task jsonTask = gson.fromJson(body, new TaskTypeToken().getType());
                        Task task = manager.addTasks(new Task(jsonTask.getName(), jsonTask.getDescription(),
                                jsonTask.getStartTime(), jsonTask.getDuration()));
                        if (task.getId() != 0) {
                            sendSuccess(exchange, "Задача успешно создана.");
                        } else {
                            sendHasInteractions(exchange, "Задача пересекается по времени с уже существующими.");
                        }
                    } catch (NullPointerException exp) {
                        sendEmptyData(exchange, "Поля задач не могут быть пустыми.");
                    }
                }
                case DELETE_TASK -> {
                    manager.clearTasks();
                    sendSuccess(exchange, "Все задачи удалены.");
                }
                default -> sendError(exchange, "Метод " + method + " не поддерживается.");
            }
        } catch (ManagerSaveException exp) {
            sendFileError(exchange, "Ошибка сохранения в файл.");
        }
    }

    private void handleSingleTask(HttpExchange exchange, String method, int id) throws IOException {
        Task task = manager.getTaskById(id);

        if (task == null) {
            sendNotFound(exchange, "Не существует задачи с таким id: " + id + ".");
        } else {
            try {
                switch (method) {
                    case GET_TASK -> sendText(exchange, gson.toJson(task));
                    case POST_TASK -> {
                        try {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Task updateTask = manager.changeTask(gson.fromJson(body, new TaskTypeToken().getType()));
                            Task aTask = manager.getTaskById(updateTask.getId());
                            if (updateTask.equals(aTask)) {
                                sendSuccess(exchange, "Задача успешно обновлена.");
                            } else {
                                sendHasInteractions(exchange, "Задача пересекается по времени с уже существующими.");
                            }
                        } catch (NullPointerException exp) {
                            sendEmptyData(exchange, "Поля обновляемой задачи не могут быть пустыми.");
                        }
                    }
                    case DELETE_TASK -> {
                        manager.removeTaskById(id);
                        sendSuccess(exchange, "Задача успешно удалена.");
                    }
                    default -> sendError(exchange, "Метод " + method + " не поддерживается.");
                }
            } catch (ManagerSaveException exp) {
                sendFileError(exchange, "Ошибка сохранения в файл.");
            }
        }
    }
}
