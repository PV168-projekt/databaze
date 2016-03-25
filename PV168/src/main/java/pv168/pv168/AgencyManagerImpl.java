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
    public Mission findMissionWithAgent(Agent agent) throws ServiceFailureException, IllegalArgumentException{
        validate(agent);
        
        try(Connection connection = dataSource.getConnection(); 
                PreparedStatement st = connection.prepareStatement(
                        "SELECT mission.id, name, location "
                        + "FROM mission JOIN agent ON mission.id = agent.missionid "
                        + "WHERE agent.id = ?"))
        {
            st.setLong(1, agent.getId());
            return MissionManagerImpl.executeQueryForSingleMission(st);
        }
        catch(SQLException e){
            throw new ServiceFailureException("Error when trying to find mission with agent " + agent, e);
        }
    }

    @Override
    public List<Agent> findAgentsOnMission(Mission mission) throws ServiceFailureException, IllegalArgumentException {
        validate(mission);
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
              PreparedStatement st = connection.prepareStatement("SELECT agent.id, name, salary "
                      + "FROM agent JOIN mission ON mission.id = agent.missionid "
                      + "WHERE mission.id = ?"))
        {
            st.setLong(1, mission.getId());
            return AgentManagerImpl.executeQueryForMultipleAgents(st);
        }
        catch(SQLException e){
            throw new ServiceFailureException("Error when trying to agents on mission " + mission, e);
        }
    }

    @Override
    public void enrollAgentOnMission(Agent agent, Mission mission) throws ServiceFailureException, IllegalArgumentException {
        validate(agent);
        validate(mission);
        checkDataSource();
        
        if(new MissionManagerImpl().findMissionById(mission.getId()) == null){
            throw new IllegalArgumentException("Mission does not exist");
        }
        
        try(Connection connection = dataSource.getConnection();
              PreparedStatement st = connection.prepareStatement("UPDATE agent SET missionid = ? WHERE id = ? AND missionid IS NULL"))
        {
            int rowsUpdated = st.executeUpdate();
            if(rowsUpdated == 0){
                throw new IllegalArgumentException("Agent " + agent + "not found or is already enrolled on a mission");
            }
        }
        catch(SQLException e){
            throw new ServiceFailureException("Error when enrolling agent " + agent + "on mission " + mission, e);
        }
    }

    @Override
    public void removeAgentFromMission(Agent agent) {
        validate(agent);
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
              PreparedStatement st = connection.prepareStatement("UPDATE agent SET missionid = NULL "
                      + "WHERE id = ? AND missionid IS NOT NULL"))
        {
            st.setLong(1, agent.getId());
            
            int rowsUpdated = st.executeUpdate();
            if(rowsUpdated == 0){
                throw new IllegalArgumentException("Agent " + agent + "not found or is not enrolled on a mission");
            }
        }
        catch(SQLException e){
            throw new ServiceFailureException("Error during removing agent " + agent + "from it's mission");
        }
    }

    @Override
    public void removeMissionFromAgents(Mission mission) {
        validate(mission);
        
        for(Agent agent : findAgentsOnMission(mission)){
            removeAgentFromMission(agent);
        }
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
