package emu.grasscutter.game.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class MoonPhaseSystemTest {
  @Test
  void clampsMoonPhaseLevelToProtocolRange() {
    assertEquals(0, MoonPhaseSystem.clampMoonPhaseLevel(-1));
    assertEquals(0, MoonPhaseSystem.clampMoonPhaseLevel(0));
    assertEquals(1, MoonPhaseSystem.clampMoonPhaseLevel(1));
    assertEquals(2, MoonPhaseSystem.clampMoonPhaseLevel(2));
    assertEquals(2, MoonPhaseSystem.clampMoonPhaseLevel(4));
  }

  @Test
  void clampsVerdantDewToThreeStacks() {
    assertEquals(0f, MoonPhaseSystem.clampVerdantDew(-1f));
    assertEquals(0f, MoonPhaseSystem.clampVerdantDew(Float.NaN));
    assertEquals(0f, MoonPhaseSystem.clampVerdantDew(Float.POSITIVE_INFINITY));
    assertEquals(1f, MoonPhaseSystem.clampVerdantDew(1f));
    assertEquals(3f, MoonPhaseSystem.clampVerdantDew(3f));
    assertEquals(3f, MoonPhaseSystem.clampVerdantDew(50f));
  }

  @Test
  void compatibilityFallbackUsesTheSupportedMaximum() {
    assertEquals(true, MoonPhaseSystem.FORCE_MAX_VERDANT_DEW);
    assertEquals(3f, MoonPhaseSystem.MAX_VERDANT_DEW);
  }
}
