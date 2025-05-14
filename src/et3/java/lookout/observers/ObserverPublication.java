package et3.java.lookout.observers;

import et3.java.lookout.Lookout;
import et3.java.lookout.entities.Entity;
import et3.java.lookout.entities.Media;
import et3.java.lookout.entities.PersonNatural;
import et3.java.lookout.events.Event;
import et3.java.lookout.events.Publication;
import et3.java.lookout.exceptions.ELOEntity;
import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.exceptions.ELOObserverWrongThreshold;
import et3.java.lookout.exceptions.ELOPercentage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObserverPublication extends Observer {
    private static double threshold = 0.4;

    private final List<String> history = new ArrayList<>();
    private final Map<Integer, Integer> mentions = new HashMap<>();

    public ObserverPublication(Entity target) {
        super(target);
    }

    public static double getThreshold() { return ObserverPublication.threshold; }
    public static void setThreshold(double threshold) throws ELOObserverWrongThreshold {
        if (threshold < 0 || threshold > 1)
            throw new ELOObserverWrongThreshold("Threshold must be between 0 and 1");
        ObserverPublication.threshold = threshold;
    }

    @Override
    public void update(Event event) throws ELOEntity, ELOPercentage {
        if (event instanceof Publication _pub) {
            this.history.add(_pub.toString());

            if (this.target instanceof Media _media) {
                for (Entity _e : _pub.mentions) this.mentions.merge(_e.id, 1, Integer::sum);

                int totalMentions = this.mentions.values().stream().mapToInt(Integer::intValue).sum();

                for (Entity _e : _pub.mentions) {
                    double percentage = ((double) this.mentions.getOrDefault(_e.id, 0)) / totalMentions;

                    if (percentage > ObserverPublication.threshold)
                        Lookout.addHistory("Too many mentions: " + _e + " with " + percentage + "% in " + _media);
                }
            }

            else if (this.target instanceof PersonNatural _natural) {
                this.mentions.merge(_pub.media.id, 1, Integer::sum);

                if (_natural.getEveryOwned().containsKey(_pub.media.id))
                    Lookout.addHistory("Subjective mention: " + _natural + " in " + _pub.media);
            }
        }
    }

    public void printHistory() {
        System.out.println("History:");
        for (String s : this.history) System.out.println("\n" + s);
    }

    public void printMentions() throws ELOEntityNotFound {
        System.out.println("Mentions:");
        int totalMentions = this.mentions.values().stream().mapToInt(Integer::intValue).sum();

        this.mentions.forEach((_id, _cnt) ->
            System.out.println("\t" + Entity.getEntity(_id) + ": " + (((double) _cnt) / totalMentions) + "%")
        );
    }
}
