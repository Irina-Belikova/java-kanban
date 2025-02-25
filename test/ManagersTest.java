import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm, "Экземпляр класса TaskManager не создан.");
    }

    @Test
    void getHistory() {
        HistoryManager hm = Managers.getHistory();
        assertNotNull(hm, "Экземпляр класса HistoryManager не создан.");
    }
}
