package emu.grasscutter.data.excels;

import emu.grasscutter.data.*;
import emu.grasscutter.data.common.ItemParamData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ResourceType(name = "CombineExcelConfigData.json")
public class CombineData extends GameResource {

    private int combineId;
    private int playerLevel;
    private boolean isDefaultShow;
    private int combineType;
    private int subCombineType;
    private int resultItemId;
    private int resultItemCount;
    private int scoinCost;

    // Инициализация списков по умолчанию, чтобы они не были null
    private List<ItemParamData> randomItems = new ArrayList<>();
    private List<ItemParamData> materialItems = new ArrayList<>();

    private String recipeType;

    @Override
    public int getId() {
        return this.combineId;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        try {
            if (randomItems != null && !randomItems.isEmpty()) {
                randomItems = randomItems.stream()
                        .filter(item -> item != null && item.getId() > 0)
                        .collect(Collectors.toList());
            }

            if (materialItems != null && !materialItems.isEmpty()) {
                materialItems = materialItems.stream()
                        .filter(item -> item != null && item.getId() > 0)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            emu.grasscutter.Grasscutter.getLogger()
                    .error("Error cleaning CombineData items", e);
        }
    }

    public int getCombineId() {
        return combineId;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public boolean isDefaultShow() {
        return isDefaultShow;
    }

    public int getCombineType() {
        return combineType;
    }

    public int getSubCombineType() {
        return subCombineType;
    }

    public int getResultItemId() {
        return resultItemId;
    }

    public int getResultItemCount() {
        return resultItemCount;
    }

    public int getScoinCost() {
        return scoinCost;
    }

    public List<ItemParamData> getRandomItems() {
        return randomItems;
    }

    public List<ItemParamData> getMaterialItems() {
        return materialItems;
    }

    public String getRecipeType() {
        return recipeType;
    }
}
