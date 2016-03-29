package pv168.pv168;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgencyManagerImplTest {

    private AgencyManagerImpl manager;
    private AgentManagerImpl agentManager; 
    private MissionManagerImpl missionManager;
    private DataSource ds;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    //--------------------------------------------------------------------------
    // Test initialization
    //--------------------------------------------------------------------------
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:database-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, MissionManager.class.getResource("createTables.sql"));
        manager = new AgencyManagerImpl();
        manager.setDataSource(ds);
        agentManager = new AgentManagerImpl();
        agentManager.setDataSource(ds);
        missionManager = new MissionManagerImpl();
        missionManager.setDataSource(ds);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, MissionManager.class.getResource("dropTables.sql"));
    }

    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------
    private Mission m1, m2, m3, missionWithNullId, missionNotInDB;
    private Agent a1, a2, a3, a4, a5, agentWithNullId, agentNotInDB;

    private void prepareTestData() {

        m1 = new MissionBuilder().name("Mission 1").location("Lokace 1").build();
        m2 = new MissionBuilder().name("Mission 2").location("Lokace 2").build();
        m3 = new MissionBuilder().name("Mission 3").location("Lokace 3").build();

        a1 = new AgentBuilder().name("Agent 1").salary(100).build();
        a2 = new AgentBuilder().name("Agent 2").salary(200).build();
        a3 = new AgentBuilder().name("Agent 3").salary(300).build();
        a4 = new AgentBuilder().name("Agent 4").salary(400).build();
        a5 = new AgentBuilder().name("Agent 5").salary(500).build();

        agentManager.createAgent(a1);
        agentManager.createAgent(a2);
        agentManager.createAgent(a3);
        agentManager.createAgent(a4);
        agentManager.createAgent(a5);

        missionManager.createMission(m1);
        missionManager.createMission(m2);
        missionManager.createMission(m3);

        missionWithNullId = new MissionBuilder().id(0).build();
        missionNotInDB = new MissionBuilder().id(m3.getId() + 100).build();
        assertThat(missionManager.findMissionById(missionNotInDB.getId())).isNull();

        agentWithNullId = new AgentBuilder().name("Agent with null id").salary(100).id(0).build();
        agentNotInDB = new AgentBuilder().name("Agent not in DB").salary(200).id(a5.getId() + 100).build();
        assertThat(agentManager.findAgentById(agentNotInDB.getId())).isNull();
    }

    @Test
    public void findMissionWithAgent() {

        assertThat(manager.findMissionWithAgent(a1)).isNull();
        assertThat(manager.findMissionWithAgent(a2)).isNull();
        assertThat(manager.findMissionWithAgent(a3)).isNull();
        assertThat(manager.findMissionWithAgent(a4)).isNull();
        assertThat(manager.findMissionWithAgent(a5)).isNull();

        manager.enrollAgentOnMission(a1, m3);

        assertThat(manager.findMissionWithAgent(a1))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a2)).isNull();
        assertThat(manager.findMissionWithAgent(a3)).isNull();
        assertThat(manager.findMissionWithAgent(a4)).isNull();
        assertThat(manager.findMissionWithAgent(a5)).isNull();
    }

    //--------------------------------------------------------------------------
    // Tests for find* operations
    //--------------------------------------------------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void findMissionWithNullAgent() {
        manager.findMissionWithAgent(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void findMissionWithAgentHavingNullId() {
        manager.findMissionWithAgent(agentWithNullId);
    }

    @Test
    public void findAgentInMission() {
        manager.findAgentsOnMission(m1);

        assertThat(manager.findAgentsOnMission(m1)).isEmpty();
        assertThat(manager.findAgentsOnMission(m2)).isEmpty();
        assertThat(manager.findAgentsOnMission(m3)).isEmpty();

        manager.enrollAgentOnMission(a2, m3);
        manager.enrollAgentOnMission(a3, m2);
        manager.enrollAgentOnMission(a4, m3);
        manager.enrollAgentOnMission(a5, m2);

        assertThat(manager.findAgentsOnMission(m1))
                .isEmpty();
        assertThat(manager.findAgentsOnMission(m2))
                .usingFieldByFieldElementComparator()
                .containsOnly(a3, a5);
        assertThat(manager.findAgentsOnMission(m3))
                .usingFieldByFieldElementComparator()
                .containsOnly(a2, a4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAgentInNullMission() {
        manager.findAgentsOnMission(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void findMissionInMissionHavingNullId() {
        manager.findAgentsOnMission(missionWithNullId);
    }

    //--------------------------------------------------------------------------
    // Tests for CemeteryManager.putBodyIntoGrave(Body,Grave) operation
    //--------------------------------------------------------------------------
    @Test
    public void putAgentIntoMission() {

        assertThat(manager.findMissionWithAgent(a1)).isNull();
        assertThat(manager.findMissionWithAgent(a2)).isNull();
        assertThat(manager.findMissionWithAgent(a3)).isNull();
        assertThat(manager.findMissionWithAgent(a4)).isNull();
        assertThat(manager.findMissionWithAgent(a5)).isNull();

        manager.enrollAgentOnMission(a1, m3);
        manager.enrollAgentOnMission(a5, m1);
        manager.enrollAgentOnMission(a3, m3);

        assertThat(manager.findAgentsOnMission(m1))
                .usingFieldByFieldElementComparator()
                .containsOnly(a5);
        assertThat(manager.findAgentsOnMission(m2))
                .isEmpty();
        assertThat(manager.findAgentsOnMission(m3))
                .usingFieldByFieldElementComparator()
                .containsOnly(a1, a3);

        assertThat(manager.findMissionWithAgent(a1))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a2))
                .isNull();
        assertThat(manager.findMissionWithAgent(a3))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a4))
                .isNull();
        assertThat(manager.findMissionWithAgent(a5))
                .isEqualToComparingFieldByField(m1);
    }

    @Test
    public void putAgentIntoMissionMultipleTime() {

        manager.enrollAgentOnMission(a1, m3);
        manager.enrollAgentOnMission(a5, m1);
        manager.enrollAgentOnMission(a3, m3);

        assertThatThrownBy(() -> manager.enrollAgentOnMission(a1, m3))
                .isInstanceOf(IllegalEntityException.class);

        assertThat(manager.findAgentsOnMission(m1))
                .usingFieldByFieldElementComparator()
                .containsOnly(a5);
        assertThat(manager.findAgentsOnMission(m2))
                .isEmpty();
        assertThat(manager.findAgentsOnMission(m3))
                .usingFieldByFieldElementComparator()
                .containsOnly(a1, a3);
    }

    @Test
    public void putAgentIntoMultipleMission() {

        manager.enrollAgentOnMission(a1, m3);
        manager.enrollAgentOnMission(a5, m1);
        manager.enrollAgentOnMission(a3, m3);

        assertThatThrownBy(() -> manager.enrollAgentOnMission(a1, m2))
                .isInstanceOf(IllegalEntityException.class);

        // verify that failure was atomic and no data was changed
        assertThat(manager.findAgentsOnMission(m1))
                .usingFieldByFieldElementComparator()
                .containsOnly(a5);
        assertThat(manager.findAgentsOnMission(m2))
                .isEmpty();
        assertThat(manager.findAgentsOnMission(m3))
                .usingFieldByFieldElementComparator()
                .containsOnly(a1, a3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullAgentsIntoMission() {
        manager.enrollAgentOnMission(null, m2);
    }

    @Test(expected = IllegalEntityException.class)
    public void putAgentWithNullIdIntoMission() {
        manager.enrollAgentOnMission(agentWithNullId, m2);
    }

    @Test(expected = IllegalEntityException.class)
    public void putAgentNotInDBIntoMission() {
        manager.enrollAgentOnMission(agentNotInDB, m2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putAgentIntoNullMission() {
        manager.enrollAgentOnMission(a2, null);
    }

    @Test(expected = IllegalEntityException.class)
    public void putAgentIntoMissionWithNullId() {
        manager.enrollAgentOnMission(a2, missionWithNullId);
    }

//    @Test(expected = IllegalEntityException.class)
//    public void putAgentIntoMissionNotInDB() {
//        manager.enrollAgentOnMission(a2, missionNotInDB);
//    }

    //--------------------------------------------------------------------------
    // Tests for CemeteryManager.removeBodyFromGrave(Body,Grave) operation
    //--------------------------------------------------------------------------
    @Test
    public void removeAgentFromMission() {

        manager.enrollAgentOnMission(a1, m3);
        manager.enrollAgentOnMission(a3, m3);
        manager.enrollAgentOnMission(a4, m3);
        manager.enrollAgentOnMission(a5, m1);

        assertThat(manager.findMissionWithAgent(a1))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a2))
                .isNull();
        assertThat(manager.findMissionWithAgent(a3))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a4))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a5))
                .isEqualToComparingFieldByField(m1);

        manager.removeAgentFromMission(a3);

        assertThat(manager.findAgentsOnMission(m1))
                .usingFieldByFieldElementComparator()
                .containsOnly(a5);
        assertThat(manager.findAgentsOnMission(m2))
                .isEmpty();
        assertThat(manager.findAgentsOnMission(m3))
                .usingFieldByFieldElementComparator()
                .containsOnly(a1, a4);

        assertThat(manager.findMissionWithAgent(a1))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a2))
                .isNull();
        assertThat(manager.findMissionWithAgent(a3))
                .isNull();
        assertThat(manager.findMissionWithAgent(a4))
                .isEqualToComparingFieldByField(m3);
        assertThat(manager.findMissionWithAgent(a5))
                .isEqualToComparingFieldByField(m1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullAgentFromMission() {
        manager.removeAgentFromMission(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void removeAgentWithNullIdFromMission() {
        manager.removeAgentFromMission(agentWithNullId);
    }

    @Test(expected = IllegalEntityException.class)
    public void removeAgentNotInDBFromMission() {
        manager.removeAgentFromMission(agentNotInDB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeAgentFromNullMission() {
        manager.removeMissionFromAgents(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void removeAgentFromMissionWithNullId() {
        manager.removeMissionFromAgents(missionWithNullId);
    }

//    @Test(expected = IllegalEntityException.class)
//    public void removeAgentFromMissionNotInDB() {
//        manager.removeMissionFromAgents(missionNotInDB);
//    }

    //--------------------------------------------------------------------------
    // Tests if GraveManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------
    @FunctionalInterface
    private static interface Operation<T> {

        void callOn(T subjectOfOperation);
    }

    private void testExpectedServiceFailureException(Operation<AgencyManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void findAgentsInMissionWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((manager) -> manager.findAgentsOnMission(m1));
    }

    @Test
    public void findMissionWithAgentWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((manager) -> manager.findMissionWithAgent(a1));
    }

    @Test
    public void putAgentIntoMissionWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((manager) -> manager.enrollAgentOnMission(a1, m1));
    }

//    @Test
//    public void removeAgentIntoMissionWithSqlExceptionThrown() throws SQLException {
//        testExpectedServiceFailureException((manager) -> manager.removeAgentFromMission(a1));
//    }

}
