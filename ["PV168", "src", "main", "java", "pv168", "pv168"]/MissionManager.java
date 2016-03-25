package pv168.pv168;

import java.util.List;

/**
 * Created by Pavel Morcinek on 27.2.2016.
 */

/**
 * Manages mission's CRUD operations
 */
public interface MissionManager {

    /**
     * creates new mission
     * @param mission
     */
    void createMission(Mission mission);

    /**
     * updates existing mission's data
     * @param mission
     */
    void updateMission(Mission mission);

    /**
     * deletes existing mission
     * @param mission
     */
    void deleteMission(Mission mission);

    /**
     * find existing mission by its id
     * @param id
     * @return Mission if it exists, otherwise null
     */
    Mission findMissionById(Long id);

    /**
     * @return all existing missions
     */
    List<Mission> findAllMissions();
}
