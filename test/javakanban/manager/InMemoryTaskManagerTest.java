package javakanban.manager;
import static javakanban.model.TypeTaskManager.IN_MEMORY;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>  {
    protected InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}