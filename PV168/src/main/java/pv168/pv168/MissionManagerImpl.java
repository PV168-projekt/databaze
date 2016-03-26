package pv168.pv168;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Created by Pavel Morcinek and Lukas Ptosek
 * 
 * MELO BY BYT HOTOVE KDYZ TAK JESTE NEJAKE UPRAVY
 */

public class MissionManagerImpl implements MissionManager {

    private DataSource dataSource;

    private static final Logger logger = Logger.getLogger(
            MissionManagerImpl.class.getName());

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    private void validate(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException();
        }
        if (mission.getName() == null) {
            throw new ValidationException();
        }
        if (mission.getLocation() == null) {
            throw new ValidationException();
        }
    }

    @Override
    public void createMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        validate(mission);

        if (mission.getId() != 0) {
            throw new IllegalEntityException("Mission id is already set");
        }
        
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Mission (name,location) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, mission.getName());
            st.setString(2, mission.getLocation());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            mission.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting mission into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        validate(mission);

        if (mission.getId() == 0) {
            throw new IllegalEntityException("Mission id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Mission SET name = ?, location = ? WHERE id = ?");
            st.setString(1, mission.getName());
            st.setString(2, mission.getLocation());
            st.setLong(3, mission.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating mission in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        if (mission == null) {
            throw new IllegalArgumentException("Mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("Mission id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Mission WHERE id = ?");
            st.setLong(1, mission.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, false);
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

    @Override
    public Mission findMissionById(Long id) throws ServiceFailureException {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, location FROM Mission WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleMission(st);

        } catch (SQLException ex) {
            String msg = "Error when getting mission with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Mission executeQueryForSingleMission(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Mission result = rowToMission(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more mission with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public List<Mission> findAllMissions() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;

        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, location FROM Mission");
            return executeQueryForMultipleMission(st);

        } catch (SQLException ex) {
            String msg = "Error when getting all mission from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<Mission> executeQueryForMultipleMission(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Mission> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToMission(rs));
        }
        return result;
    }

    static private Mission rowToMission(ResultSet rs) throws SQLException {
        Mission result = new Mission();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setLocation((rs.getString("location")));
        return result;
    }
}
