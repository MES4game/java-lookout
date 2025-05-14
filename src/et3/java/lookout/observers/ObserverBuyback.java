package et3.java.lookout.observers;

import et3.java.lookout.Lookout;
import et3.java.lookout.entities.Entity;
import et3.java.lookout.entities.Media;
import et3.java.lookout.events.Event;
import et3.java.lookout.events.Buyback;
import et3.java.lookout.exceptions.ELOEntityNotFound;

import java.util.ArrayList;
import java.util.List;

public class ObserverBuyback extends Observer {
    private final List<String> history = new ArrayList<>();

    public ObserverBuyback(Entity target) {
        super(target);
    }

    @Override
    public void update(Event event) throws ELOEntityNotFound {
        if (event instanceof Buyback<?> _buy) {
            this.history.add(_buy.toString());
            if (this.target instanceof Media _media) {
                if (_media.getOwner(_buy.newOwner) == _buy.percentage)
                    Lookout.addHistory("New owner: " + _buy.newOwner + " for " + _media);
            }
        }
    }

    public void printHistory() {
        System.out.println("History:");
        for (String s : this.history) System.out.println("\n" + s);
    }
}
