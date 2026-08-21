package emu.grasscutter.game.player;

import static org.junit.jupiter.api.Assertions.assertTrue;

import emu.grasscutter.GameConstants;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class TeamAbilityDefaultsTest {
  @Test
  void includesStellarSuperconductorClientAbility() {
    assertTrue(
        Arrays.asList(GameConstants.DEFAULT_TEAM_ABILITY_STRINGS)
            .contains(StellarConductorSystem.TEAM_ABILITY));
  }
}
