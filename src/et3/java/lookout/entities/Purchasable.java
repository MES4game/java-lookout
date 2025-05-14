package et3.java.lookout.entities;

import et3.java.lookout.exceptions.ELOEntityNotFound;
import et3.java.lookout.exceptions.ELOPercentage;
import et3.java.lookout.utils.Percentage;

import java.util.Map;

public interface Purchasable {
    Percentage getOwner(Person owner) throws ELOEntityNotFound;
    void addOwner(Person owner, Percentage percentage);
    void changeOwner(Person owner, Percentage percentage) throws ELOEntityNotFound;
    void removeOwner(Person owner);

    Map<Integer, Percentage> getOwnerList() throws ELOPercentage;

    Map<Integer, Percentage> getEveryOwners() throws ELOEntityNotFound, ELOPercentage;

    void printEveryOwners() throws ELOEntityNotFound, ELOPercentage;
}
