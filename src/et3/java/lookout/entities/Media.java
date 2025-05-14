package et3.java.lookout.entities;

import et3.java.lookout.exceptions.ELOEntity;
import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.exceptions.ELOEntityWrongType;
import et3.java.lookout.exceptions.ELOPercentage;
import et3.java.lookout.utils.Percentage;
import et3.java.lookout.utils.PercentageType;

import java.util.HashMap;
import java.util.Map;

public class Media extends Entity implements Purchasable {
    private MediaType type;
    private MediaPeriodicity periodicity;
    private String scale;
    private boolean isPaid;
    private boolean isGone;
    private final Map<Integer, Percentage> ownerList = new HashMap<>();

    public Media(String name, String comment, MediaType type, MediaPeriodicity periodicity, String scale, boolean isPaid, boolean isGone) throws ELOPercentage {
        super(name, comment);
        this.type = type;
        this.periodicity = periodicity;
        this.scale = scale;
        this.isPaid = isPaid;
        this.isGone = isGone;
        this.addOwner(PersonNatural.ADMIN, new Percentage(1, PercentageType.EQUALS));
    }

    public MediaType getType() { return this.type; }
    public MediaPeriodicity getPeriodicity() { return this.periodicity; }
    public String getScale() { return this.scale; }
    public boolean isPaid() { return this.isPaid; }
    public boolean isGone() { return this.isGone; }

    public void setType(MediaType type) { this.type = type; }
    public void setPeriodicity(MediaPeriodicity periodicity) { this.periodicity = periodicity; }
    public void setScale(String scale) { this.scale = scale; }
    public void setPaid(boolean isPaid) { this.isPaid = isPaid; }
    public void setGone(boolean isGone) { this.isGone = isGone; }

    @Override
    public Percentage getOwner(Person owner) throws ELOEntityNotFound {
        if (!this.ownerList.containsKey(owner.id))
            throw new ELOEntityNotFound("Can't find in owner list");
        return this.ownerList.get(owner.id).clone();
    }
    @Override
    public void addOwner(Person owner, Percentage percentage) { this.ownerList.put(owner.id, percentage); }
    @Override
    public void changeOwner(Person owner, Percentage percentage) throws ELOEntityNotFound {
        if (!this.ownerList.containsKey(owner.id))
            throw new ELOEntityNotFound("Can't find in owner list");
        this.ownerList.replace(owner.id, percentage);
    }
    @Override
    public void removeOwner(Person owner) { this.ownerList.remove(owner.id); }

    @Override
    public Map<Integer, Percentage> getOwnerList() throws ELOPercentage {
        Map<Integer, Percentage> result = new HashMap<>();
        for (Map.Entry<Integer, Percentage> o : this.ownerList.entrySet())
            result.put(o.getKey(), o.getValue().clone());
        return result;
    }

    @Override
    public Map<Integer, Percentage> getEveryOwners() throws ELOEntityNotFound, ELOPercentage {
        Map<Integer, Percentage> result = this.getOwnerList();

        for (Map.Entry<Integer, Percentage> o : this.getOwnerList().entrySet()) {
            if (Entity.getEntity(o.getKey()) instanceof PersonLegal _legal) {
                Map<Integer, Percentage> subRes = _legal.getEveryOwners();

                for (Map.Entry<Integer, Percentage> so : subRes.entrySet()) {
                    Percentage p = so.getValue().clone();
                    p.factor(o.getValue());
                    if (result.containsKey(so.getKey())) result.get(so.getKey()).add(p);
                    else result.put(so.getKey(), p);
                }
            }
        }

        return result;
    }

    @Override
    public void printEveryOwners() throws ELOEntity, ELOPercentage {
        System.out.println("Owners:");
        for (Map.Entry<Integer, Percentage> o : this.getEveryOwners().entrySet()) {
            try {
                Person owner = (Person) Entity.getEntity(o.getKey());
                String type;
                try {
                    Percentage percentage = this.getOwner(owner);
                    type = percentage.equals(o.getValue()) ? "Direct" : "Direct/Indirect";
                } catch (ELOEntityNotFound e) { type = "Indirect"; }
                System.out.println("\t" + owner + "(" + type + "): " + o.getValue());
            } catch (ClassCastException e) { throw new ELOEntityWrongType("Can't cast to Person"); }
        }
    }

    @Override
    public String toString() {
        return super.toString().replace("]", ", type=" + this.type +
                ", periodicity=" + this.periodicity + ", scale=\"" + this.scale +
                "\", isPaid=" + this.isPaid + ", isGone=" + this.isGone + "]");
    }
}
