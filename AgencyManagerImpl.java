package pv168.pv168;

import java.sql.*;
import java.util.List;
import javax.sql.DataSource;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Manages relation between Mission and Agent (1 : N)
 */
public class AgencyManagerImpl implements AgencyManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }    

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    @Override
    public Mission findMissionWithAgent(Agent agent) {
        validate(agent);
        
        try(Connection connection = dataSource.getConnection(); 
                PreparedStatement st = connection.prepareStatement(
                        "SELECT mission.id, name, location "
                        + "FROM mission JOIN agent ON mission.id = agent.missionid"
                        + "WHERE agent.id = ?"))
        {
            st.setLong(1, agent.getId());
            return MissionManagerImpl.executeQueryForSingleAgent(st);
        }
        catch(SQLException e){
            
        }
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
  
    /**
     * checks if agent isn't null or his salary is not negative
     *
     * @param agent
     * @throws IllegalArgumentException
     */
    private void validate(Agent agent) throws IllegalArgumentException {
        if (agent == null) {
            throw new IllegalArgumentException("Agent is null");
        }
        if (agent.getSalary() < 0) {
            throw new IllegalArgumentException("Agent's salary is negative");
        }
    }
    
    /**
     * checks if mission isn't null
     * 
     * @param mission
     * @throws IllegalArgumentException 
     */
    private void validate(Mission mission) throws IllegalArgumentException {
        if (mission == null) {
            throw new IllegalArgumentException("Mission is null");
        }
    }
}
