package et3.java.lookout.entities;

import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.observers.Observable;

import java.util.*;

public class Entity extends Observable {
    private static int NEXT_ID = 0;
    private static final Map<Integer, Entity> entities = new HashMap<>();

    public final int id;
    private String name;
    private String comment;

    public Entity(String name, String comment) {
        this.id = Entity.NEXT_ID++;
        this.name = name;
        this.comment = comment;

        Entity.entities.put(this.id, this);
    }

    public static Collection<Entity> getEntities() { return Entity.entities.values(); }
    public static Entity getEntity(int id) throws ELOEntityNotFound {
        if (!Entity.entities.containsKey(id)) throw new ELOEntityNotFound("Can't find in entity list");
        return Entity.entities.get(id);
    }
    public static Entity getEntity(String name) throws ELOEntityNotFound {
        for (Entity e : entities.values()) if (Objects.equals(e.name, name)) return e;
        throw new ELOEntityNotFound("Can't find in entity list");
    }

    public String getName() { return name; }
    public void setName(String newName) { this.name = newName; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + this.id + ", name=\"" + this.name +
                "\", comment=\"" + this.comment + "\"]";
    }
}
