package et3.java.lookout.events;

import et3.java.lookout.exceptions.ELOEvent;
import et3.java.lookout.exceptions.ELOObserver;

public interface Event {
    void compute() throws ELOEvent, ELOObserver;
}
