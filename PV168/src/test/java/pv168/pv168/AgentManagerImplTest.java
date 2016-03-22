package pv168.pv168;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author xptosek1
 */
public class AgentManagerImplTest {
    
    public AgentManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createAgent method, of class AgentManagerImpl.
     */
    @Test
    public void testCreateAgent() {
        System.out.println("createAgent");
        Agent agent = null;
        AgentManagerImpl instance = new AgentManagerImpl();
        instance.createAgent(agent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateAgent method, of class AgentManagerImpl.
     */
    @Test
    public void testUpdateAgent() {
        System.out.println("updateAgent");
        Agent agent = null;
        AgentManagerImpl instance = new AgentManagerImpl();
        instance.updateAgent(agent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteAgent method, of class AgentManagerImpl.
     */
    @Test
    public void testDeleteAgent() {
        System.out.println("deleteAgent");
        Agent agent = null;
        AgentManagerImpl instance = new AgentManagerImpl();
        instance.deleteAgent(agent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAgentById method, of class AgentManagerImpl.
     */
    @Test
    public void testFindAgentById() {
        System.out.println("findAgentById");
        Long id = null;
        AgentManagerImpl instance = new AgentManagerImpl();
        Agent expResult = null;
        Agent result = instance.findAgentById(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllAgents method, of class AgentManagerImpl.
     */
    @Test
    public void testFindAllAgents() {
        System.out.println("findAllAgents");
        AgentManagerImpl instance = new AgentManagerImpl();
        List<Agent> expResult = null;
        List<Agent> result = instance.findAllAgents();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
