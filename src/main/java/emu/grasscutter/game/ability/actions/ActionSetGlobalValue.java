package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.player.MoonPhaseSystem;
import emu.grasscutter.game.player.StellarConductorSystem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

@AbilityAction(AbilityModifierAction.Type.SetGlobalValue)
public final class ActionSetGlobalValue extends AbilityActionHandler {

  @Override
  public boolean execute(
      Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
    target = getActionTarget(ability, target, action.target);
    if (target == null || action.key == null || action.key.isBlank()) return false;

    var owner = ability.getOwner();
    var properties = new Object2FloatOpenHashMap<String>();

    for (var property : FightProperty.values()) {
      var name = property.name();
      var value = owner.getFightProperty(property);
      properties.put(name, value);
    }

    properties.putAll(ability.getAbilitySpecials());

    var valueKey = action.key;
    float computedValue = action.ratio.get(properties, 0f);
    if (MoonPhaseSystem.VERDANT_DEW_KEY.equals(valueKey)) {
      computedValue = MoonPhaseSystem.MAX_VERDANT_DEW;
    } else if (StellarConductorSystem.isStackKey(valueKey)) {
      computedValue = StellarConductorSystem.clampStacks(computedValue);
    }
    if (target instanceof EntityTeam team
        && StellarConductorSystem.isStackKey(valueKey)) {
      StellarConductorSystem.setStacks(team, computedValue);
    } else {
      target.getGlobalAbilityValues().put(valueKey, computedValue);
    }
    target.onAbilityValueUpdate();

    if (target.getScene() != null && target.getScene().getHost() != null) {
      target
          .getScene()
          .getHost()
          .sendPacket(new PacketServerGlobalValueChangeNotify(target, valueKey, computedValue));
    }
    return true;
  }
}
