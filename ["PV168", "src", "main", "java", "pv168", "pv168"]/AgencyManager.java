package pv168.pv168;

import java.util.List;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Manages 1:N relation between missions and it's agents
 */
public interface AgencyManager {
    /**
     * @param agent
     * @return  Mission which is the agent on
     */
    Mission findMissionWithAgent(Agent agent);

    /**
     * @param mission
     * @return Agents working on the mission
     */
    List<Agent> findAgentsOnMission(Mission mission);

    /**
     *  Creates record of enrolling the agent on the mission
     * @param agent
     * @param mission
     */
    void enrollAgentOnMission(Agent agent, Mission mission);

    /**
     *  Removes the agent from his mission
     * @param agent
     */
    void removeAgentFromMission(Agent agent);

    /**
     * Removes relations with the mission
     * @param mission
     */
    void removeMissionFromAgents(Mission mission);
}
