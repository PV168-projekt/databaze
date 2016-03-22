package pv168.pv168;

import java.util.List;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Manages relation between Mission and Agent (1 : N)
 */
public class AgencyManagerImpl implements AgencyManager {

    @Override
    public Mission findMissionWithAgent(Agent agent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Agent> findAgentsOnMission(Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enrollAgentOnMission(Agent agent, Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAgentFromMission(Agent agent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeMissionFromAgents(Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
