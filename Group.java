/**
 * Plain data holder for one group slot.
 * All fields are guarded by the owning grouper's lock, so no lock lives here.
 */
public class Group {
    public final int id;
    public int ttl;           // seconds the current party will stay
    public boolean active;    // true while a party occupies this slot
    public int partiesServed; // how many parties this slot has hosted

    public Group(int id) {
        this.id = id;
        this.ttl = 0;
        this.active = false;
        this.partiesServed = 0;
    }
}