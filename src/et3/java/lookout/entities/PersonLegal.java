package et3.java.lookout.entities;

import et3.java.lookout.exceptions.ELOEntity;
import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.exceptions.ELOEntityWrongType;
import et3.java.lookout.exceptions.ELOPercentage;
import et3.java.lookout.utils.Percentage;
import et3.java.lookout.utils.PercentageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonLegal extends Person implements Purchasable {
    private final Map<Integer, Percentage> ownerList = new HashMap<>();

    public PersonLegal(String name, String comment) throws ELOPercentage {
        super(name, comment);
        this.addOwner(PersonNatural.ADMIN, new Percentage(1, PercentageType.EQUALS));
    }

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
        return this.getEveryOwners(this.id);
    }
    public Map<Integer, Percentage> getEveryOwners(Integer start) throws ELOEntityNotFound, ELOPercentage {
        Map<Integer, Percentage> result = this.getOwnerList();

        for (Map.Entry<Integer, Percentage> o : this.getOwnerList().entrySet()) {
            if (Objects.equals(o.getKey(), start)) continue;

            if (Entity.getEntity(o.getKey()) instanceof PersonLegal _legal) {
                Map<Integer, Percentage> subRes = _legal.getEveryOwners(start);

                for (Map.Entry<Integer, Percentage> so : subRes.entrySet()) {
                    Percentage p = so.getValue().clone();
                    p.factor(o.getValue());
                    if (result.containsKey(so.getKey())) result.get(so.getKey()).add(p);
                    else result.put(so.getKey(), p);
                }
            }
        }

        result.remove(start);

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
}
