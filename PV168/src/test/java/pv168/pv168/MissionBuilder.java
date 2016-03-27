package pv168.pv168;

/**
 *
 * @author 
 */
public class MissionBuilder {
    private long id;
    private String name;
    private String location;

    public MissionBuilder id(long id) {
        this.id = id;
        return this;
    }

    public MissionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MissionBuilder location(String location) {
        this.location = location;
        return this;
    }
    
    public Mission build(){
        return new Mission(id, name, location);
    }
}
