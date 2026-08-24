package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.MoonPhaseSystem;
import emu.grasscutter.game.player.StellarConductorSystem;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AbilityScalarValueEntryOuterClass.AbilityScalarValueEntry;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.net.proto.AbilitySyncStateInfoOuterClass.AbilitySyncStateInfo;
import emu.grasscutter.net.proto.SyncTeamEntityNotifyOuterClass.SyncTeamEntityNotify;
import emu.grasscutter.net.proto.TeamEntityInfoOuterClass.TeamEntityInfo;
import emu.grasscutter.utils.Utils;

public class PacketSyncTeamEntityNotify extends BasePacket {

    public PacketSyncTeamEntityNotify(Player player) {
        super(PacketOpcodes.SyncTeamEntityNotify);

        SyncTeamEntityNotify.Builder proto =
                SyncTeamEntityNotify.newBuilder().setSceneId(player.getSceneId());

        if (player.getWorld().isMultiplayer()) {
            for (var p : player.getWorld()) {
                // Skip if same player
                if (player == p) {
                    continue;
                }

                // Set info
                TeamEntityInfo info =
                        TeamEntityInfo.newBuilder()
                                .setTeamEntityId(p.getTeamManager().getEntity().getId())
                                .setAuthorityPeerId(p.getPeerId())
                                .setTeamAbilityInfo(buildTeamAbilityInfo(p))
                                .build();

                proto.addTeamEntityInfoList(info);
            }
        }

        this.setData(proto);
    }

    private static AbilitySyncStateInfo buildTeamAbilityInfo(Player player) {
        var teamEntity = player.getTeamManager().getEntity();
        int moonPhaseLevel = MoonPhaseSystem.getMoonPhaseLevel(player.getTeamManager());
        float verdantDew = MoonPhaseSystem.synchronizeVerdantDew(teamEntity, moonPhaseLevel);
        float stellarStacks = StellarConductorSystem.setStacks(
                teamEntity, StellarConductorSystem.getStacks(teamEntity));

        var builder = AbilitySyncStateInfo.newBuilder()
                .addSgvDynamicValueMap(valueEntry(
                        "SGV_PlayerTeam_Phlogiston", player.getPhlogistonValue()))
                .addSgvDynamicValueMap(valueEntry(
                        MoonPhaseSystem.MOON_PHASE_LEVEL_KEY, moonPhaseLevel))
                .addDynamicValueMap(valueEntry(
                        MoonPhaseSystem.MOON_PHASE_LEVEL_KEY, moonPhaseLevel));

        if (moonPhaseLevel > 0) {
            var dewEntry = valueEntry(MoonPhaseSystem.VERDANT_DEW_KEY, verdantDew);
            builder.addDynamicValueMap(dewEntry).addSgvDynamicValueMap(dewEntry);
        }
        for (String key : StellarConductorSystem.getStackKeys()) {
            builder.addDynamicValueMap(valueEntry(key, stellarStacks));
        }
        return builder.build();
    }

    private static AbilityScalarValueEntry valueEntry(String key, float value) {
        return AbilityScalarValueEntry.newBuilder()
                .setKey(AbilityString.newBuilder()
                        .setHash(Utils.abilityHash(key))
                        .setStr(key)
                        .build())
                .setFloatValue(value)
                .build();
    }
}
