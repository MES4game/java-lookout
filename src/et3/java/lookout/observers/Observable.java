package et3.java.lookout.observers;

import et3.java.lookout.events.Event;
import et3.java.lookout.exceptions.ELOObserver;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private final List<Observer> observers = new ArrayList<>();

    public Observable() {}

    public void register(Observer observer) { this.observers.add(observer); }

    public boolean isRegistered(Observer observer) { return this.observers.contains(observer); }

    public void unregister(Observer observer) { this.observers.remove(observer); }

    public void notify(Event event) throws ELOObserver {
        for (Observer observer : this.observers) observer.update(event);
    }

    public void printObservers() {
        System.out.println("Observers: ");
        for (Observer observer : this.observers) System.out.println("\t" + observer);
    }
}
