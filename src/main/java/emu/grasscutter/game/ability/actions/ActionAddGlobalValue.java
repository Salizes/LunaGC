package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.PredicateEvaluator;
import emu.grasscutter.game.entity.EntityTeam;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.MoonPhaseSystem;
import emu.grasscutter.game.player.StellarConductorSystem;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.List;
import java.util.Map;

@AbilityAction(AbilityModifierAction.Type.AddGlobalValue)
public final class ActionAddGlobalValue extends AbilityActionHandler {
  @Override
  public boolean execute(
      Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
    target = getActionTarget(ability, target, action.target);
    if (target == null) return false;
    if (action.predicates != null && !action.predicates.isEmpty()) {
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> preds = (List<Map<String, Object>>) (List<?>) action.predicates;
      if (!PredicateEvaluator.all(preds, ability, ability.getOwner(), target, action)) return true;
    }
    var owner = ability.getOwner();
    var properties = new Object2FloatOpenHashMap<String>();

    for (var property : FightProperty.values()) {
      var name = property.name();
      var value = owner.getFightProperty(property);
      properties.put(name, value);
    }

    properties.putAll(ability.getAbilitySpecials());
    String valueKey = action.key;
    if (valueKey == null || valueKey.isBlank()) return false;
    float valueToAdd = action.ratio.get(properties, 0f);
    float maxValue = action.maxValue.get(properties, 0f);
    float minValue = action.minValue.get(properties, 0f);

    float currentGlobalValue = target.getGlobalAbilityValues().getOrDefault(valueKey, 0f);
    float newValue =
        addValue(currentGlobalValue, valueToAdd, action.useLimitRange, minValue, maxValue);
    if (MoonPhaseSystem.VERDANT_DEW_KEY.equals(valueKey)) {
      newValue = MoonPhaseSystem.MAX_VERDANT_DEW;
    } else if (StellarConductorSystem.isStackKey(valueKey)) {
      newValue = StellarConductorSystem.clampStacks(newValue);
    }

    if (target instanceof EntityTeam team
        && StellarConductorSystem.isStackKey(valueKey)) {
      StellarConductorSystem.setStacks(team, newValue);
    } else {
      target.getGlobalAbilityValues().put(valueKey, newValue);
    }

    target.onAbilityValueUpdate();
    if (target.getScene() != null && target.getScene().getHost() != null) {
      target
          .getScene()
          .getHost()
          .sendPacket(new PacketServerGlobalValueChangeNotify(target, valueKey, newValue));
    }

    return true;
  }

  static float addValue(float current, float delta, boolean useLimitRange, float min, float max) {
    float value = current + delta;
    if (!useLimitRange) return value;
    return Math.max(min, Math.min(max, value));
  }
}
