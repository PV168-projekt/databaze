package pv168.pv168;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pavel Morcinek und Lukas Ptosek on 27.2.2016.
 */
/**
 * Manages agent's CRUD operations
 */
public class AgentManagerImpl implements AgentManager {

    private DataSource dataSource;
    
    private static final Logger logger = Logger.getLogger(
            AgencyManagerImpl.class.getName());

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    /**
     * creates new agent
     *
     * @param agent
     */
    @Override
    public void createAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        validate(agent);

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("INSERT INTO AGENT (name,salary) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, agent.getName());
            st.setInt(2, agent.getSalary());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: Number of added rows (" + addedRows + ") "
                        + "is not equal to 1 when trying to insert agent" + agent);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            agent.setId(getKey(keyRS, agent));
        } catch (SQLException e) {
            throw new ServiceFailureException("Error when inserting agent " + agent, e);
        }
    }

    /**
     * updates existing agent's data
     *
     * @param agent
     */
    @Override
    public void updateAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        validate(agent);

        if (agent.getId() == null) {
            throw new IllegalArgumentException("Agent ID is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE Agent SET name = ?, salary = ? WHERE id = ?")) {

            st.setString(1, agent.getName());
            st.setInt(2, agent.getSalary());
            st.setLong(3, agent.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Agent " + agent + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating agent " + agent, ex);
        }
    }

    @Override
    public void deleteAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        if (agent == null) {
            throw new IllegalArgumentException("Agent is null");
        }

        if (agent.getId() == null) {
            throw new IllegalEntityException("Agent id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Agent WHERE id = ?");
            st.setLong(1, agent.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, agent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting Mission from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    /**
     * finds existing agent by his id
     *
     * @param id
     * @return Agent if he exists, otherwise null
     */
    @Override
    public Agent findAgentById(Long id) throws ServiceFailureException {
        checkDataSource();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT id, name, salary FROM agent WHERE id = ?")) {

            st.setLong(1, id);

            return executeQueryForSingleAgent(st);
        } catch (SQLException e) {
            throw new ServiceFailureException("Error when retrieving agent with id " + id, e);
        }
    }

    /**
     * @return all existing Agents
     */
    @Override
    public List<Agent> findAllAgents() throws ServiceFailureException {
        checkDataSource();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT id, name, salary FROM agent")) {

            return executeQueryForMultipleAgents(st);
        } catch (SQLException e) {
            throw new ServiceFailureException("Error when retrieving all agents");
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

    private long getKey(ResultSet keyRS, Agent agent) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key "
                        + "retrieving failed when trying to insert agent " + agent
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key "
                        + "retrieving failed when trying to insert agent " + agent
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key "
                    + "retrieving failed when trying to insert agent " + agent
                    + " - no key found");
        }
    }

    private static Agent resultSetToAgent(ResultSet rs) throws SQLException {
        Agent agent = new Agent(rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("salary"));
        return agent;
    }

    static Agent executeQueryForSingleAgent(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Agent result = resultSetToAgent(rs);

            if (rs.next()) {
                throw new ServiceFailureException("Internal error: More entities with the same id found ");
            }

            return result;
        } else {
            return null;
        }
    }

    static List<Agent> executeQueryForMultipleAgents(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();

        List<Agent> result = new ArrayList<>();
        while (rs.next()) {
            result.add(resultSetToAgent(rs));
        }
        return result;
    }
}
