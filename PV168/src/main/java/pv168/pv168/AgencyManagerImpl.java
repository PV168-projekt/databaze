package pv168.pv168;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Created by Pavel Morcinek and Lukas Ptosek
 */
/**
 * Manages relation between Mission and Agent (1 : N)
 */
public class AgencyManagerImpl implements AgencyManager {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    private static final Logger logger = Logger.getLogger(
            AgencyManagerImpl.class.getName());

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Mission findMissionWithAgent(Agent agent) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();

        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == 0) {
            throw new IllegalEntityException("agent id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Mission.id, Mission.name, Mission.location "
                    + "FROM Mission JOIN Agent ON Mission.id = Agent.missionId "
                    + "WHERE Agent.id = ?");
            st.setLong(1, agent.getId());
            return MissionManagerImpl.executeQueryForSingleMission(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find mission with agent " + agent;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Agent> findAgentsOnMission(Mission mission) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == 0) {
            throw new IllegalEntityException("mission id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT agent.id, agent.name, agent.salary from agent JOIN mission on mission.id = agent.missionid WHERE mission.id = ?");
            st.setLong(1, mission.getId());
            
            return AgentManagerImpl.executeQueryForMultipleAgents(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find agents in mission " + mission;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void enrollAgentOnMission(Agent agent, Mission mission) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();

        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == 0) {
            throw new IllegalEntityException("misson id is null");
        }
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == 0) {
            throw new IllegalEntityException("agent id is null");
        }
        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            updateSt = conn.prepareStatement(
                    "UPDATE agent SET missionid = ? WHERE id = ? AND missionid IS NULL");
            updateSt.setLong(1, mission.getId());
            updateSt.setLong(2, agent.getId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Agents " + agent + " not found or it is already enrol in some mission");
            }
            DBUtils.checkUpdatesCount(count, agent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting agent into mission";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, updateSt);
        }
    }

    @Override
    public void removeAgentFromMission(Agent agent) {
        validate(agent);
        checkDataSource();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("UPDATE agent SET missionid = NULL "
                        + "WHERE id = ? AND missionid IS NOT NULL")) {
            st.setLong(1, agent.getId());

            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated == 0) {
                throw new IllegalEntityException("Agent " + agent + "not found or is not enrolled on a mission");
            }
        } catch (SQLException e) {
            throw new ServiceFailureException("Error during removing agent " + agent + "from it's mission");
        }
    }

    @Override
    public void removeMissionFromAgents(Mission mission) {
        validate(mission);

        for (Agent agent : findAgentsOnMission(mission)) {
            removeAgentFromMission(agent);
        }
    }

    private void validate(Agent agent) throws IllegalArgumentException {
        if (agent == null) {
            throw new IllegalArgumentException("Agent is null");
        }
        if (agent.getSalary() < 0) {
            throw new IllegalArgumentException("Agent's salary is negative");
        }
    }

    private void validate(Mission mission) throws IllegalArgumentException {
        if (mission == null) {
            throw new IllegalArgumentException("Mission is null");
        }
    }
}
