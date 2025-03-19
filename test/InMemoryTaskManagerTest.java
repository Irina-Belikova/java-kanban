import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    protected InMemoryTaskManagerTest() throws IOException {
    }

    @Override
    protected TaskManager createManager() {
        return Managers.getDefault();
    }
}
