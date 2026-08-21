package emu.grasscutter.game.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class StellarConductorSystemTest {
  @Test
  void recognizesAllOfficialStackCounters() {
    assertTrue(StellarConductorSystem.isStackKey(StellarConductorSystem.STACK_KEY));
    assertTrue(StellarConductorSystem.isStackKey(StellarConductorSystem.UI_STACK_KEY));
    assertTrue(StellarConductorSystem.isStackKey(StellarConductorSystem.AVATAR_STACK_KEY));
    assertFalse(StellarConductorSystem.isStackKey("_not_a_stellar_stack"));
  }

  @Test
  void clampsStacksToResourceLimit() {
    assertEquals(0f, StellarConductorSystem.clampStacks(Float.NaN));
    assertEquals(0f, StellarConductorSystem.clampStacks(-1f));
    assertEquals(6f, StellarConductorSystem.clampStacks(6f));
    assertEquals(12f, StellarConductorSystem.clampStacks(99f));
  }
}
