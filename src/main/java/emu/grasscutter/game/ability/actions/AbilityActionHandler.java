package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.GameEntity;

public abstract class AbilityActionHandler {
  public abstract boolean execute(
      Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target);

  protected AbilityManager abilityManager;

  public AbilityActionHandler setManager(AbilityManager mgr) {
    this.abilityManager = mgr;
    return this;
  }
  /**
   * Returns the target entity.
   *
   * @param ability The ability being invoked.
   * @param entity The entity invoking the ability.
   * @param target The target entity type.
   * @return The target entity.
   */
  protected GameEntity getTarget(Ability ability, GameEntity entity, String target) {
    return switch (target) {
      default -> throw new RuntimeException("Unknown target type: " + target);
      case "Self" -> entity;
      case "Team" -> ability.getPlayerOwner().getTeamManager().getEntity();
      case "OriginOwner" -> ability.getPlayerOwner().getTeamManager().getCurrentAvatarEntity();
      case "Owner" -> ability.getOwner();
      case "Applier" -> entity; // TODO: Validate.
      case "CurLocalAvatar" -> ability
          .getPlayerOwner()
          .getTeamManager()
          .getCurrentAvatarEntity(); // TODO: Validate.
      // Single-target handlers use the active avatar; handlers that can fan out process this target
      // explicitly (for example AttachModifier and CopyGlobalValue).
      case "CurTeamAvatars", "AllPlayerAvatars" ->
          ability.getPlayerOwner().getTeamManager().getCurrentAvatarEntity();
      case "CasterOriginOwner" -> null; // TODO: Figure out.
    };
  }

  /** Resolves action targets while retaining the invoke target for client-only target types. */
  protected GameEntity getActionTarget(Ability ability, GameEntity entity, String target) {
    if (target == null || target.isBlank() || "Self".equals(target) || "Target".equals(target)) {
      return entity;
    }
    try {
      var resolved = getTarget(ability, entity, target);
      return resolved != null ? resolved : entity;
    } catch (RuntimeException ignored) {
      return entity;
    }
  }
}
