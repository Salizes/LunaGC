package emu.grasscutter.data.excels.activity;

import emu.grasscutter.data.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@ResourceType(
        name = "NewActivityExcelConfigData.json",
        loadPriority = ResourceType.LoadPriority.LOW)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityData extends GameResource {
    int activityId;
    String activityType;

    // Инициализация списков по умолчанию
    List<Integer> condGroupId = new ArrayList<>();
    List<Integer> watcherId = new ArrayList<>();
    List<ActivityWatcherData> watcherDataList = new ArrayList<>();

    @Override
    public int getId() {
        return this.activityId;
    }

    @Override
    public void onLoad() {
        try {
            if (watcherId != null && !watcherId.isEmpty()) {
                this.watcherDataList = watcherId.stream()
                        .map(item -> GameData.getActivityWatcherDataMap().get(item))
                        .filter(Objects::nonNull)
                        .toList();
            } else {
                this.watcherDataList = new ArrayList<>();
            }
        } catch (Exception e) {
            emu.grasscutter.Grasscutter.getLogger()
                    .error("Error processing ActivityData.onLoad", e);
            this.watcherDataList = new ArrayList<>();
        }
    }
}
