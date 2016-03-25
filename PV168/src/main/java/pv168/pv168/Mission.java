package pv168.pv168;


/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Contains mission's data
 */
public class Mission {
    private long id;
    private String name;
    private String location;

    /**
     * Constructor with all attributes as arguments
     * @param id
     * @param name
     * @param location
     */
    public Mission(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }
    
    Mission() { //empty contructor        
    }

    /**
     * Copy constructor
     * @param mission
     */
    public Mission(Mission mission){
        this.id = mission.getId();
        this.name = mission.getName();
        this.location = mission.getLocation();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mission mission = (Mission) o;

        return id == mission.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
