package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityTeam;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.MoonPhaseSystem;
import emu.grasscutter.game.player.StellarConductorSystem;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;

@AbilityAction(AbilityModifierAction.Type.ClearGlobalValue)
public final class ActionClearGlobalValue extends AbilityActionHandler {
  @Override
  public boolean execute(
      Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
    target = getActionTarget(ability, target, action.target);
    if (target == null) return false;
    var valueKey = action.key;
    if (valueKey == null || valueKey.isEmpty()) {
      return false; // Invalid key, abort execution.
    }

    if (MoonPhaseSystem.VERDANT_DEW_KEY.equals(valueKey)) {
      target.getGlobalAbilityValues().put(valueKey, MoonPhaseSystem.MAX_VERDANT_DEW);
      target.onAbilityValueUpdate();
      if (target.getScene() != null && target.getScene().getHost() != null) {
        target
            .getScene()
            .getHost()
            .sendPacket(
                new PacketServerGlobalValueChangeNotify(
                    target, valueKey, MoonPhaseSystem.MAX_VERDANT_DEW));
      }
      return true;
    }

    if (StellarConductorSystem.isStackKey(valueKey) && target instanceof EntityTeam team) {
      StellarConductorSystem.setStacks(team, 0f);
      target.onAbilityValueUpdate();
      if (target.getScene() != null && target.getScene().getHost() != null) {
        for (String key : StellarConductorSystem.getStackKeys()) {
          target
              .getScene()
              .getHost()
              .sendPacket(new PacketServerGlobalValueChangeNotify(target, key, 0f));
        }
      }
      return true;
    }

    // Remove the global value.
    var globalValues = target.getGlobalAbilityValues();
    if (globalValues.containsKey(valueKey)) {
      globalValues.remove(valueKey);

      // Notify the target of the update.
      target.onAbilityValueUpdate();

      // Send a value update packet to the client.
      if (target.getScene() != null && target.getScene().getHost() != null) {
        target
            .getScene()
            .getHost()
            .sendPacket(new PacketServerGlobalValueChangeNotify(target, valueKey, 0.0f));
      }

      return true;
    }

    // Key not found, nothing to clear.
    return false;
  }
}
