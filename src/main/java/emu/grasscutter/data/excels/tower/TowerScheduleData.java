package emu.grasscutter.data.excels.tower;

import emu.grasscutter.data.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ResourceType(name = "TowerScheduleExcelConfigData.json")
public class TowerScheduleData extends GameResource {
    private int scheduleId;
    private List<Integer> entranceFloorId = new ArrayList<>();
    private List<ScheduleDetail> schedules = new ArrayList<>();
    private int monthlyLevelConfigId;

    @Override
    public int getId() {
        return scheduleId;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        try {
            if (this.schedules != null && !this.schedules.isEmpty()) {
                this.schedules = this.schedules.stream()
                        .filter(item -> item != null && item.getFloorList() != null && !item.getFloorList().isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            emu.grasscutter.Grasscutter.getLogger()
                    .error("Error cleaning TowerScheduleData schedules", e);
        }
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public List<Integer> getEntranceFloorId() {
        return entranceFloorId;
    }

    public List<ScheduleDetail> getSchedules() {
        return schedules;
    }

    public int getMonthlyLevelConfigId() {
        return monthlyLevelConfigId;
    }

    public static class ScheduleDetail {
        private List<Integer> floorList = new ArrayList<>();

        public List<Integer> getFloorList() {
            return floorList;
        }
    }
}
