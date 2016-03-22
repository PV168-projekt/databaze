package pv168.pv168;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Morcinek und Lukas Ptosek on 27.2.2016.
 */
/**
 * Manages agent's CRUD operations
 */
public class AgentManagerImpl implements AgentManager {

    private final DataSource dataSource;

    public AgentManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * creates new agent
     *
     * @param agent
     */
    @Override
    public void createAgent(Agent agent) throws ServiceFailureException {
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
        validate(agent);

        if (agent.getId() == null) {
            throw new IllegalArgumentException("Agent ID is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE agent SET name = ?, salary = ? = ? WHERE id = ?")) {

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

    /**
     * deletes existing agent
     *
     * @param agent
     */
    @Override
    public void deleteAgent(Agent agent) throws ServiceFailureException {
        if (agent == null) {
            throw new IllegalArgumentException("Agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalArgumentException("Agent id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM agent WHERE id = ?")) {

            st.setLong(1, agent.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Agent " + agent + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating grave " + agent, ex);
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
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT * FROM agent WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Agent agent = resultSetToAgent(rs);

                if (rs.next()) {
                    throw new ServiceFailureException("Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + agent + " and "
                            + resultSetToAgent(rs));
                }

                return agent;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ServiceFailureException("Error when retrieving agent with id " + id, e);
        }
    }

    /**
     * @return all existing Agents
     */
    @Override
    public List<Agent> findAllAgents() throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement("SELECT * FROM agent")) {
            ResultSet rs = st.executeQuery();

            List<Agent> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToAgent(rs));
            }
            return result;
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
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert agent " + agent
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert agent " + agent
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert agent " + agent
                    + " - no key found");
        }
    }

    private Agent resultSetToAgent(ResultSet rs) throws SQLException {
        Agent agent = new Agent(rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("salary"));
        return agent;
    }

    AgentManagerImpl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
