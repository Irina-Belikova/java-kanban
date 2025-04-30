package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import file.ManagerSaveException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private static final String WAY_VALUE = "subtasks";

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] split = path.split("/");

        if (split.length == 2) {
            handleAllEpics(exchange, method);
        } else {
            Optional<Integer> optId = getTaskId(split[2]);
            if (optId.isEmpty()) {
                sendNotFound(exchange, "Неверный формат id задачи.");
            } else if (split.length == 3) {
                handleSingleEpic(exchange, method, optId.get());
            } else if (split.length == 4) {
                handleEpicSubtasks(exchange, method, optId.get(), split[3]);
            }
        }
    }

    private void handleAllEpics(HttpExchange exchange, String method) throws IOException {
        try {
            switch (method) {
                case GET_TASK -> {
                    List<Epic> epics = manager.getAllEpics();
                    String jsonString = gson.toJson(epics);
                    sendText(exchange, jsonString);
                }
                case POST_TASK -> {
                    try {
                        InputStream inputStream = exchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic jsonEpic = gson.fromJson(body, new EpicTypeToken().getType());
                        Epic epic = manager.addEpics(new Epic(jsonEpic.getName(), jsonEpic.getDescription()));
                        if (epic.getId() != 0) {
                            sendSuccess(exchange, "Эпик успешно создан.");
                        } else {
                            sendHasInteractions(exchange, "Эпик не может быть создан.");
                        }
                    } catch (NullPointerException exp) {
                        sendEmptyData(exchange, "Поля эпика не могут быть пустыми.");
                    }
                }
                case DELETE_TASK -> {
                    manager.clearEpics();
                    sendSuccess(exchange, "Все эпики удалены.");
                }
                default -> sendError(exchange, "Метод " + method + " не поддерживается.");
            }
        } catch (ManagerSaveException exp) {
            sendFileError(exchange, "Ошибка сохранения в файл.");
        }
    }

    private void handleSingleEpic(HttpExchange exchange, String method, int id) throws IOException {
        Epic epic = manager.getEpicById(id);

        if (epic == null) {
            sendNotFound(exchange, "Не существует эпика с таким id: " + id + ".");
        } else {
            try {
                switch (method) {
                    case GET_TASK -> sendText(exchange, gson.toJson(epic));
                    case POST_TASK -> {
                        try {
                            InputStream inputStream = exchange.getRequestBody();
                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Epic updateEpic = manager.changeEpic(gson.fromJson(body, new EpicTypeToken().getType()));
                            Epic anEpic = manager.getEpicById(updateEpic.getId());
                            if (updateEpic.equals(anEpic)) {
                                sendSuccess(exchange, "Эпик успешно обновлён.");
                            } else {
                                sendHasInteractions(exchange, "Эпик не может быть обновлён.");
                            }
                        } catch (NullPointerException exp) {
                            sendEmptyData(exchange, "Поля обновляемого эпика не могут быть пустыми.");
                        }
                    }
                    case DELETE_TASK -> {
                        manager.removeEpicById(id);
                        sendSuccess(exchange, "Эпик успешно удалён.");
                    }
                    default -> sendError(exchange, "Метод " + method + " не поддерживается.");
                }
            } catch (ManagerSaveException exp) {
                sendFileError(exchange, "Ошибка сохранения в файл.");
            }
        }
    }

    private void handleEpicSubtasks(HttpExchange exchange, String method, int id, String value) throws IOException {
        Epic epic = manager.getEpicById(id);

        if (method.equals(GET_TASK)) {
            if (epic == null) {
                sendNotFound(exchange, "Не существует эпика с таким id: " + id + ".");
            } else if (value.equals(WAY_VALUE)) {
                List<Subtask> subtasks = manager.getEpicSubtasks(epic);
                sendText(exchange, gson.toJson(subtasks));
            } else {
                sendNotFound(exchange, value + " - неверный путь.");
            }
        } else {
            sendError(exchange, "Метод " + method + " не поддерживается.");
        }
    }
}
