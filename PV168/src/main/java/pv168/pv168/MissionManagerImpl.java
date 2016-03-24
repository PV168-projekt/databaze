package pv168.pv168;
    
import java.util.List;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */
/**
 * Manages mission's CRUD operations
 */
public class MissionManagerImpl implements MissionManager {

//    private static DataSource prepareDataSource() throws SQLException {
//        EmbeddedDataSource ds = new EmbeddedDataSource();
//        // we will use in memory database
//        ds.setDatabaseName("memory:gravemgr-test");
//        // database is created automatically if it does not exist yet
//        ds.setCreateDatabase("create");
//        return ds;
//    }

    @Override
    public void createMission(Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateMission(Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMission(Mission mission) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Mission findMissionById(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Mission> findAllMissions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
