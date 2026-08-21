package emu.grasscutter.game.player;

import emu.grasscutter.game.entity.EntityTeam;
import java.util.List;

/** Shared state for the 7.0 Stellar Superconductor team ability and its UI stacks. */
public final class StellarConductorSystem {
  public static final String TEAM_ABILITY = "TeamAbility_StarSuperconductor";
  public static final String STACK_KEY = "_StarSuperconductor_AllCountGV";
  public static final String UI_STACK_KEY = "_StarSuperconductor_AllCountGV_OnUI";
  public static final String AVATAR_STACK_KEY = "_StarSuperconductor_AllCountGV_OnAvatar";
  public static final float MAX_STACKS = 12f;

  private static final List<String> STACK_KEYS =
      List.of(STACK_KEY, UI_STACK_KEY, AVATAR_STACK_KEY);

  private StellarConductorSystem() {}

  public static List<String> getStackKeys() {
    return STACK_KEYS;
  }

  public static boolean isStackKey(String key) {
    return key != null && STACK_KEYS.contains(key);
  }

  public static float clampStacks(float value) {
    if (!Float.isFinite(value)) return 0f;
    return Math.max(0f, Math.min(MAX_STACKS, value));
  }

  public static float getStacks(EntityTeam teamEntity) {
    if (teamEntity == null) return 0f;
    var values = teamEntity.getGlobalAbilityValues();
    float value = values.getOrDefault(UI_STACK_KEY, values.getOrDefault(STACK_KEY, 0f));
    return clampStacks(value);
  }

  /** Mirrors the official UI/team/avatar counters so reconnects and character swaps retain them. */
  public static float setStacks(EntityTeam teamEntity, float value) {
    float normalized = clampStacks(value);
    if (teamEntity != null) {
      for (String key : STACK_KEYS) {
        teamEntity.getGlobalAbilityValues().put(key, normalized);
      }
    }
    return normalized;
  }
}
