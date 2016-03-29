package pv168.pv168;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.assertj.core.api.Assertions.*;
import org.junit.*;
import org.junit.rules.ExpectedException;


/**
 *
 * @author xptosek1
 */
public class AgentManagerImplTest {
    
    private DataSource dataSource;
    private AgentManagerImpl manager;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:database-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, MissionManager.class.getResource("createTables.sql"));
        manager = new AgentManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, MissionManager.class.getResource("dropTables.sql"));
    }

    private AgentBuilder sampleAgent1Builder() {
        return new AgentBuilder()
                .name("Mata Hari")
                .salary(35000);
    }
    
    private AgentBuilder sampleAgent2Builder() {
        return new AgentBuilder()
                .name("Alexander Valterovich Litvinenko")
                .salary(20000);
    }
    
    private AgentBuilder sampleAgent3Builder() {
        return new AgentBuilder()
                .name("Giacomo Casanova")
                .salary(0);
    }
    
    private AgentBuilder sampleInvalidAgentBuilder() {
        return new AgentBuilder()
                .name("Fero")
                .salary(-5000);
    }
    
    @Test
    public void testCreateValidAgent() {
        Agent agent = sampleAgent1Builder().build();
        manager.createAgent(agent);
        
        long id = agent.getId();
        
        assertThat(manager.findAgentById(id))
                .isNotSameAs(agent)
                .isEqualToComparingFieldByField(agent);
    }
    
    @Test
    public void testCreateInvalidAgent() {
        Agent agent = sampleInvalidAgentBuilder().build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createAgent(agent);
    }
    
    @Test
    public void testUpdateAgent() {
        Agent agentToBeUpdated = sampleAgent1Builder().build();
        Agent agentToStaySame = sampleAgent2Builder().build();
        
        manager.createAgent(agentToBeUpdated);
        manager.createAgent(agentToStaySame);
        
        agentToBeUpdated.setName("Different name");
        manager.updateAgent(agentToBeUpdated);
        
        assertThat(manager.findAgentById(agentToBeUpdated.getId()))
                .isEqualToComparingFieldByField(agentToBeUpdated);
        
        assertThat(manager.findAgentById(agentToStaySame.getId()))
                .isEqualToComparingFieldByField(agentToStaySame);
        
        expectedException.expect(IllegalArgumentException.class);
        manager.updateAgent(null);
    }

    @Test
    public void testDeleteAgent() {
        Agent agentToBeDeleted = sampleAgent1Builder().build();
        Agent agentToRemain = sampleAgent2Builder().build();
        manager.createAgent(agentToBeDeleted);
        manager.createAgent(agentToRemain);
        
        manager.deleteAgent(agentToBeDeleted);
        
        assertThat(manager.findAgentById(agentToBeDeleted.getId())) .isNull();
        assertThat(manager.findAgentById(agentToRemain.getId())).isNotNull();
    }
    
    @Test
    public void testDeleteNonexistingAgent() {
        Agent agentToBeDeleted = sampleAgent1Builder().build();
        agentToBeDeleted.setId(1L);

        expectedException.expect(IllegalEntityException.class);
        manager.deleteAgent(agentToBeDeleted);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNull() {
        manager.deleteAgent(null);
    }

    @Test
    public void testFindAgentById() {
        Agent agent = sampleAgent1Builder().build();
        manager.createAgent(agent);
        
        assertThat(manager.findAgentById(agent.getId())).isEqualTo(agent);
        assertThat(manager.findAgentById(agent.getId() + 1)).isNull();
    }

    @Test
    public void testFindAllAgents() {
        assertThat(manager.findAllAgents()).isEmpty();
        
        Agent agent1 = sampleAgent1Builder().build();
        Agent agent2 = sampleAgent2Builder().build();
        Agent agent3 = sampleAgent3Builder().build();
        
        manager.createAgent(agent1);
        manager.createAgent(agent2);
        manager.createAgent(agent3);
        
        assertThat(manager.findAllAgents())
                .usingFieldByFieldElementComparator()
                .containsOnly(agent1, agent2, agent3);
    }
    
}
