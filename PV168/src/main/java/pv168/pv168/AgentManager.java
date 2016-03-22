package pv168.pv168;

import java.util.List;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Manages Agent's CRUD operations
 */
public interface AgentManager {
    /**
     * creates new agent
     * @param agent
     */
    void createAgent(Agent agent);

    /**
     * updates existing agent's data
     * @param agent
     */
    void updateAgent(Agent agent);

    /**
     * deletes existing agent
     * @param agent
     */
    void deleteAgent(Agent agent);

    /**
     * finds existing agent by his id
     * @param id
     * @return Agent if he exists, otherwise null
     */
    Agent findAgentById(Long id);

    /**
     * @return all existing Agents
     */
    List<Agent> findAllAgents();
}
