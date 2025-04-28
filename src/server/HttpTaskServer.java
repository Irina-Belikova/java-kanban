package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import file.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    public static final int PORT = 8080;
    protected final HttpServer httpServer;
    protected final TaskManager manager;
    protected final Gson gson;

    public HttpTaskServer(TaskManager manager) {
        try {
            this.manager = manager;
        } catch (ManagerSaveException exp) {
            throw new ManagerSaveException("Ошибка чтения сохранённых в менеджер данных.");
        }

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationAdapter())
                    .create();
            httpServer.createContext("/tasks", new TasksHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(manager, gson));
            httpServer.createContext("/epics", new EpicsHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка запуска сервера", exp);
        }
    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        try {
            System.out.println("HTTP-сервер запущен на порту:" + PORT);
            this.httpServer.start();
        } catch (Exception exp) {
            throw new RuntimeException("Ошибка запуска сервера.", exp);
        }
    }

    public void stop() {
        try {
            this.httpServer.stop(0);
            System.out.println("HTTP-сервер остановлен.");
        } catch (Exception exp) {
            throw new RuntimeException("Ошибка остановки сервера.", exp);
        }
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            long minutes = duration.toMinutes();
            if (minutes == 0) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(minutes);
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return Duration.ZERO;
            }
            return Duration.ofMinutes(jsonReader.nextLong());
        }
    }

    static class TaskTypeToken extends TypeToken<Task> {
    }

    static class SubtaskTypeToken extends TypeToken<Subtask> {
    }

    static class EpicTypeToken extends TypeToken<Epic> {
    }

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

        HttpTaskServer server = new HttpTaskServer(tm);
        server.start();

        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30)));
        Task task2 = tm.addTasks(new Task("задача-2", "описание зд-2",
                LocalDateTime.of(2025, 1, 1, 12, 40), Duration.ofMinutes(30)));

        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика-1"));
        Epic epic2 = tm.addEpics(new Epic("эпик-2", "описание эпика-2"));

        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1",
                LocalDateTime.of(2025, 1, 1, 14, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask2 = tm.addSubtasks(new Subtask("подзадача-2", "описание пзд-2",
                LocalDateTime.of(2025, 1, 2, 12, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask3 = tm.addSubtasks(new Subtask("подзадача-3", "описание пзд-3",
                LocalDateTime.of(2025, 2, 1, 9, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));

        System.out.println(tm.getPrioritizedTasks());
    }
}
