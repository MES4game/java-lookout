package et3.java.lookout.observers;

import et3.java.lookout.entities.Entity;
import et3.java.lookout.events.Event;
import et3.java.lookout.exceptions.ELOObserver;
import et3.java.lookout.exceptions.ELOObserverNotFound;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class Observer {
    private static int NEXT_ID = 0;
    private static final Map<Integer, Observer> observers = new HashMap<>();

    private final int id;
    protected final Entity target;
    private final LocalDateTime start;

    public Observer(Entity target) {
        this.id = Observer.NEXT_ID++;
        this.target = target;
        this.start = LocalDateTime.now();

        this.target.register(this);
        Observer.observers.put(this.id, this);
    }

    public static Observer getObserver(int id) throws ELOObserverNotFound {
        if (!observers.containsKey(id))
            throw new ELOObserverNotFound("Can't find in observer list");
        return Observer.observers.get(id);
    }

    public static void printObservers() {
        System.out.println("Observers:");
        for (Observer observer : Observer.observers.values()) System.out.println("\t" + observer);
    }

    abstract void update(Event event) throws ELOObserver;

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + this.id + ", target=" + this.target + ", start=" + this.start + "]";
    }
}
