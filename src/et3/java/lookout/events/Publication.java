package et3.java.lookout.events;

import et3.java.lookout.entities.Entity;
import et3.java.lookout.entities.Media;
import et3.java.lookout.exceptions.ELOObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Publication implements Event {
    public final Media media;
    public final PublicationType type;
    public final LocalDateTime date;
    public final List<Entity> mentions;

    public Publication(Media media, PublicationType type, List<Entity> mentions) {
        this.media = media;
        this.type = type;
        this.date = LocalDateTime.now();
        this.mentions = mentions;
    }

    @Override
    public void compute() throws ELOObserver {
        // Put code here to compute effect of a publication
        // For now, a publication as no effect, so we only notify observers

        this.media.notify(this);
        for (Entity entity : this.mentions) entity.notify(this);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[media=" + media + ", type=" + this.type + ", date=" + this.date +
                ", mentions=[" +
                this.mentions.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")) +
                "]]";
    }
}
