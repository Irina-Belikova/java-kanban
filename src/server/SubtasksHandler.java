package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import file.ManagerSaveException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] split = path.split("/");

        if (split.length == 2) {
            handleAllSubtasks(exchange, method);
        } else if (split.length == 3) {
            Optional<Integer> optId = getTaskId(split[2]);
            if (optId.isEmpty()) {
                sendNotFound(exchange, "Неверный формат id подзадачи.");
            } else {
                handleSingleSubtask(exchange, method, optId.get());
            }
        }
    }

    private void handleAllSubtasks(HttpExchange exchange, String method) throws IOException {
        try {
            switch (method) {
                case GET_TASK -> {
                    List<Subtask> subtasks = manager.getAllSubtasks();
                    String jsonString = gson.toJson(subtasks);
                    sendText(exchange, jsonString);
                }
                case POST_TASK -> {
                    try {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask jsonSubtask = gson.fromJson(body, new SubtaskTypeToken().getType());
                        Subtask subtask = manager.addSubtasks(new Subtask(jsonSubtask.getName(), jsonSubtask.getDescription(),
                                jsonSubtask.getStartTime(), jsonSubtask.getDuration(), jsonSubtask.getEpicId()));
                        if (subtask.getId() != 0) {
                            sendSuccess(exchange, "Подзадача успешно создана.");
                        } else {
                            sendHasInteractions(exchange, "Подзадача пересекается по времени с уже существующими.");
                        }
                    } catch (NullPointerException exp) {
                        sendEmptyData(exchange, "Поля подзадачи не могут быть пустыми.");
                    }
                }
                case DELETE_TASK -> {
                    manager.clearSubtasks();
                    sendSuccess(exchange, "Все подзадачи удалены.");
                }
                default -> sendError(exchange, "Метод " + method + " не поддерживается.");
            }
        } catch (ManagerSaveException exp) {
            sendFileError(exchange, "Ошибка сохранения в файл.");
        }
    }

    private void handleSingleSubtask(HttpExchange exchange, String method, int id) throws IOException {
        Subtask subtask = manager.getSubtaskById(id);

        if (subtask == null) {
            sendNotFound(exchange, "Не существует подзадачи с таким id: " + id + ".");
        } else {
            try {
                switch (method) {
                    case GET_TASK -> sendText(exchange, gson.toJson(subtask));
                    case POST_TASK -> {
                        try {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Subtask updateSubtask = manager.changeSubtask(gson.fromJson(body, new SubtaskTypeToken().getType()));
                            Subtask aSubtask = manager.getSubtaskById(updateSubtask.getId());
                            if (updateSubtask.equals(aSubtask)) {
                                sendSuccess(exchange, "Подзадача успешно обновлена.");
                            } else {
                                sendHasInteractions(exchange, "Подзадача пересекается по времени с уже существующими.");
                            }
                        } catch (NullPointerException exp) {
                            sendEmptyData(exchange, "Поля обновляемой подзадачи не могут быть пустыми.");
                        }
                    }
                    case DELETE_TASK -> {
                        manager.removeSubtaskById(id);
                        sendSuccess(exchange, "Подзадача успешно удалена.");
                    }
                    default -> sendError(exchange, "Метод " + method + " не поддерживается.");
                }
            } catch (ManagerSaveException exp) {
                sendFileError(exchange, "Ошибка сохранения в файл.");
            }
        }
    }
}
