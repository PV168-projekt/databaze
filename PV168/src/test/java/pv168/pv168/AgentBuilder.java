/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pv168.pv168;

/**
 *
 * @author Morcin
 */
public class AgentBuilder {
    private long id;
    private String name;
    private int salary;

    public AgentBuilder id(long id) {
        this.id = id;
        return this;
    }

    public AgentBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AgentBuilder salary(int salary) {
        this.salary = salary;
        return this;
    }
    
    public Agent build(){
        return new Agent(id, name, salary);
    }
}
