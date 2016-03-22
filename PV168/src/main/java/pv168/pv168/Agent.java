package pv168.pv168;

/**
 * Contains agent's personal data
 */
public class Agent {
    private long id;
    private String name;
    private int salary;

    /**
     * Constructor with all attributes as arguments
     * @param id
     * @param name
     * @param salary
     */
    public Agent(Long id, String name, int salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    /**
     * Copy constructor
     * @param agent
     */
    public Agent(Agent agent){
        this.id = agent.getId();
        this.name = agent.getName();
        this.salary = agent.getSalary();
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

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
