package emu.grasscutter.game.player;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityTeam;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public final class MoonPhaseSystem {
  public static final String MOON_PHASE_LEVEL_KEY = "SGV_MoonPhaseLevel";
  public static final String VERDANT_DEW_KEY = "MoonOvergrowPoint_All";
  public static final int MAX_MOON_PHASE_LEVEL = 2;
  public static final float MAX_VERDANT_DEW = 3f;

 
  public static final boolean FORCE_MAX_VERDANT_DEW = true;

  private static final String MOON_PHASE_TAG = "AVATAR_TAG_MOONPHASE";
  private static volatile Set<Integer> moonPhaseAvatarIds;

  private MoonPhaseSystem() {}

  public static Set<Integer> getMoonPhaseAvatarIds() {
    var cached = moonPhaseAvatarIds;
    if (cached == null) {
      synchronized (MoonPhaseSystem.class) {
        cached = moonPhaseAvatarIds;
        if (cached == null) {
          cached =
              GameData.getAvatarDataMap().values().stream()
                  .filter(Objects::nonNull)
                  .filter(
                      avatar ->
                          avatar.getTags() != null && avatar.getTags().contains(MOON_PHASE_TAG))
                  .map(avatar -> avatar.getId())
                  .collect(Collectors.toCollection(HashSet::new));
          moonPhaseAvatarIds = cached = Collections.unmodifiableSet(cached);
        }
      }
    }
    return cached;
  }

  /** Clears data derived from AvatarExcelConfigData after a resource reload. */
  public static void invalidateAvatarCache() {
    moonPhaseAvatarIds = null;
  }

  public static boolean isMoonPhaseAvatar(EntityAvatar entity) {
    return entity != null
        && entity.getAvatar() != null
        && getMoonPhaseAvatarIds().contains(entity.getAvatar().getAvatarId());
  }

  public static int getMoonPhaseLevel(TeamManager teamManager) {
    if (teamManager == null) return 0;

    long taggedAvatars =
        teamManager.getActiveTeam().stream().filter(MoonPhaseSystem::isMoonPhaseAvatar).count();
    return clampMoonPhaseLevel(taggedAvatars);
  }

  public static int clampMoonPhaseLevel(long value) {
    return (int) Math.max(0, Math.min(MAX_MOON_PHASE_LEVEL, value));
  }

  public static float clampVerdantDew(float value) {
    if (!Float.isFinite(value)) return 0f;
    return Math.max(0f, Math.min(MAX_VERDANT_DEW, value));
  }

  public static float getVerdantDew(EntityTeam teamEntity) {
    if (teamEntity == null) return 0f;
    if (FORCE_MAX_VERDANT_DEW) return MAX_VERDANT_DEW;
    return clampVerdantDew(teamEntity.getGlobalAbilityValues().getOrDefault(VERDANT_DEW_KEY, 0f));
  }

  public static float setVerdantDew(EntityTeam teamEntity, float value) {
    float normalized = FORCE_MAX_VERDANT_DEW ? MAX_VERDANT_DEW : clampVerdantDew(value);
    if (teamEntity != null) {
      teamEntity.getGlobalAbilityValues().put(VERDANT_DEW_KEY, normalized);
    }
    return normalized;
  }

  /** Stores the value that must be visible for the current team state. */
  public static float synchronizeVerdantDew(EntityTeam teamEntity, int moonPhaseLevel) {
    float value = moonPhaseLevel > 0 ? getVerdantDew(teamEntity) : 0f;
    if (teamEntity != null) {
      teamEntity.getGlobalAbilityValues().put(VERDANT_DEW_KEY, value);
    }
    return value;
  }

  /** Explicitly disables dew when no Moonsign character is present. */
  public static void clearVerdantDew(EntityTeam teamEntity) {
    if (teamEntity != null) {
      teamEntity.getGlobalAbilityValues().put(VERDANT_DEW_KEY, 0f);
    }
  }
}
