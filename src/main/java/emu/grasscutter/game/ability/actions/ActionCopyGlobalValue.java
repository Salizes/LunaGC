package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.EntityTeam;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.StellarConductorSystem;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;

@AbilityAction(AbilityModifierAction.Type.CopyGlobalValue)
public final class ActionCopyGlobalValue extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity entity) {
        // Get the entities referred to.
        var source = this.getActionTarget(ability, entity, action.srcTarget);
        // Check the entities.
        if (source == null) {
            Grasscutter.getLogger().debug("ActionCopyGlobalValue: source is null");
            return false;
        }

        // Get the global value.
        var value = source.getGlobalAbilityValues().get(action.srcKey);
        if (value == null) {
            Grasscutter.getLogger().debug("ActionCopyGlobalValue: source value is null");
            return false;
        }

        if ("CurTeamAvatars".equals(action.dstTarget)
                || "AllPlayerAvatars".equals(action.dstTarget)) {
            boolean copied = false;
            for (var avatar : ability.getPlayerOwner().getTeamManager().getActiveTeam()) {
                copied |= copyValue(avatar, action.dstKey, value);
            }
            return copied;
        }

        var destination = this.getActionTarget(ability, entity, action.dstTarget);
        if (destination == null) {
            Grasscutter.getLogger().debug("ActionCopyGlobalValue: destination is null");
            return false;
        }
        return copyValue(destination, action.dstKey, value);
    }

    private boolean copyValue(GameEntity destination, String key, float value) {
        float normalized = StellarConductorSystem.isStackKey(key)
                ? StellarConductorSystem.clampStacks(value)
                : value;
        if (destination instanceof EntityTeam team && StellarConductorSystem.isStackKey(key)) {
            StellarConductorSystem.setStacks(team, normalized);
        } else {
            destination.getGlobalAbilityValues().put(key, normalized);
        }
        destination.onAbilityValueUpdate();
        if (destination.getScene() != null && destination.getScene().getHost() != null) {
            destination
                    .getScene()
                    .getHost()
                    .sendPacket(new PacketServerGlobalValueChangeNotify(destination, key, normalized));
        }
        return true;
    }
}
