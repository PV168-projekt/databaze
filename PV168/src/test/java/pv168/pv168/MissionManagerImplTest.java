package pv168.pv168;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MissionManagerImplTest {

    private MissionManagerImpl manager;
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
        manager = new MissionManagerImpl();
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, MissionManager.class.getResource("dropTables.sql"));
    }

    private Mission sampleTestMission1() {
        Mission mission = new Mission();
        mission.setName("Janosik");
        mission.setLocation("Slovensko");
        return mission;
    }

    private Mission sampleTestMission2() {
        Mission mission = new Mission();
        mission.setName("Zephyr");
        mission.setLocation("Taiwan");
        return mission;
    }

    @Test
    public void createMission() {
        Mission mission = sampleTestMission1();
        manager.createMission(mission);

        Long missionId = mission.getId();
        assertThat(missionId).isNotNull();

        assertThat(manager.findMissionById(missionId))
                .isNotSameAs(mission)
                .isEqualToComparingFieldByField(mission);
    }

    @Test
    public void findAllMission() {

        assertThat(manager.findAllMissions()).isEmpty();

        Mission m1 = sampleTestMission1();
        Mission m2 = sampleTestMission2();

        manager.createMission(m1);
        manager.createMission(m2);

        assertThat(manager.findAllMissions())
                .usingFieldByFieldElementComparator()
                .containsOnly(m1, m2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullMission() {
        manager.createMission(null);
    }
    
    @Test
    public void createMissionWithExistingId() {
        Mission mission = sampleTestMission1();
        mission.setId(1L);
        
        expectedException.expect(IllegalEntityException.class);
        manager.createMission(mission);
    }

    @Test
    public void createMissionWithNullName() {
        Mission mission = sampleTestMission1();
        mission.setName(null);

        expectedException.expect(ValidationException.class);
        manager.createMission(mission);

    }

    @Test
    public void createMissionWithNullLocation() {
        Mission mission = sampleTestMission1();
        mission.setLocation(null);

        expectedException.expect(ValidationException.class);
        manager.createMission(mission);
    }

    //--------------------------------------------------------------------------
    // Tests for update operation
    //--------------------------------------------------------------------------
    @FunctionalInterface
    private static interface Operation<T> {

        void callOn(T subjectOfOperation);
    }

    private void testUpdateMission(Operation<Mission> updateOperation) {
        Mission sourceMission = sampleTestMission1();
        Mission anotherMission = sampleTestMission2();

        manager.createMission(sourceMission);
        manager.createMission(anotherMission);

        updateOperation.callOn(sourceMission);

        manager.updateMission(sourceMission);

        assertThat(manager.findMissionById(sourceMission.getId()))
                .isEqualToComparingFieldByField(sourceMission);

        assertThat(manager.findMissionById(anotherMission.getId()))
                .isEqualToComparingFieldByField(anotherMission);
    }

    @Test
    public void updateMissionLocation() {
        testUpdateMission((mission) -> mission.setLocation("Rusko"));
    }

    @Test(expected = ValidationException.class)
    public void updateMissionLocationToNull() {
        testUpdateMission((mission) -> mission.setLocation(null));
    }

    @Test
    public void updateMissionName() {
        testUpdateMission((mission) -> mission.setName("Mirek"));
    }

    @Test(expected = ValidationException.class)
    public void updateMissionNameToNull() {
        testUpdateMission((mission) -> mission.setLocation(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullMission() {
        manager.updateMission(null);
    }

    @Test
    public void updateMissionWithNonExisitingId() {
        Mission mission = sampleTestMission1();
        mission.setId(1L);
        expectedException.expect(IllegalEntityException.class);
        manager.updateMission(mission);
    }

    //--------------------------------------------------------------------------
    // Tests for delete operation
    //--------------------------------------------------------------------------
    @Test
    public void deleteMission() {
        Mission m1 = sampleTestMission1();
        Mission m2 = sampleTestMission2();

        manager.createMission(m1);
        manager.createMission(m2);

        assertThat(manager.findMissionById(m1.getId())).isNotNull();
        assertThat(manager.findMissionById(m2.getId())).isNotNull();

        manager.deleteMission(m1);

        assertThat(manager.findMissionById(m1.getId())).isNull();
        assertThat(manager.findMissionById(m2.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullMission() {
        manager.deleteMission(null);
    }

    @Test
    public void deleteMissionWithNonExisitingId() {
        Mission mission = sampleTestMission1();
        mission.setId(1L);

        expectedException.expect(IllegalEntityException.class);
        manager.deleteMission(mission);
    }

    //--------------------------------------------------------------------------
    // Tests about SQL exception
    //--------------------------------------------------------------------------
    @Test
    public void createMissionWithSqlExceptionThrown() throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);

        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);

        Mission mission = sampleTestMission1();

        assertThatThrownBy(() -> manager.createMission(mission))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    private void testExpectedServiceFailureException(Operation<MissionManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void updateMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleTestMission1();
        manager.createMission(mission);
        testExpectedServiceFailureException((MissionManager) -> MissionManager.updateMission(mission));
    }

    @Test
    public void getMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleTestMission1();
        manager.createMission(mission);
        testExpectedServiceFailureException((MissionManager) -> MissionManager.findMissionById(mission.getId()));
    }

    @Test
    public void deleteMissionWithSqlExceptionThrown() throws SQLException {
        Mission mission = sampleTestMission1();
        manager.createMission(mission);
        testExpectedServiceFailureException((MissionManager) -> MissionManager.deleteMission(mission));
    }

    @Test
    public void findAllMissionWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((MissionManager) -> MissionManager.findAllMissions());
    }
}
