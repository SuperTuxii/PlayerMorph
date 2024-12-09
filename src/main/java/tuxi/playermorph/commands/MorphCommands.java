package tuxi.playermorph.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import tuxi.playermorph.PlayerMorph;
import tuxi.playermorph.networking.ClientboundMorphPacket;
import tuxi.playermorph.networking.ClientboundUnmorphPacket;

import javax.annotation.Nullable;
import java.util.*;

public class MorphCommands {
    private static final Map<UUID, MorphedPlayer> morphedPlayers = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("morph")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("morphPlayer", EntityArgument.player())
                        .then(Commands.argument("morphIntoTarget", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer morphPlayer = EntityArgument.getPlayer(ctx, "morphPlayer");
                                    ServerPlayer morphIntoTarget = EntityArgument.getPlayer(ctx, "morphIntoTarget");
                                    if (!morphPlayer.equals(morphIntoTarget)) {
                                        morphPlayer(morphPlayer, morphIntoTarget, null);
                                    }else {
                                        unMorphPlayer(morphPlayer, null);
                                    }
                                    return -1;
                                }).then(Commands.argument("key", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            ServerPlayer morphPlayer = EntityArgument.getPlayer(ctx, "morphPlayer");
                                            ServerPlayer morphIntoTarget = EntityArgument.getPlayer(ctx, "morphIntoTarget");
                                            int key = IntegerArgumentType.getInteger(ctx, "key");
                                            if (!morphPlayer.equals(morphIntoTarget)) {
                                                morphPlayer(morphPlayer, morphIntoTarget, key);
                                            }else {
                                                unMorphPlayer(morphPlayer, key);
                                            }
                                            return key;
                                        })
                                )
                        )
                )
        );
        dispatcher.register(Commands.literal("unmorph")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("morphedPlayer", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer morphedPlayer = EntityArgument.getPlayer(ctx, "morphedPlayer");
                            unMorphPlayer(morphedPlayer, null);
                            return -1;
                        }).then(Commands.argument("key", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    ServerPlayer morphedPlayer = EntityArgument.getPlayer(ctx, "morphedPlayer");
                                    int key = IntegerArgumentType.getInteger(ctx, "key");
                                    unMorphPlayer(morphedPlayer, key);
                                    return key;
                                })
                        ).then(Commands.literal("all")
                                .executes(ctx -> {
                                    ServerPlayer morphedPlayer = EntityArgument.getPlayer(ctx, "morphedPlayer");
                                    removePlayer(morphedPlayer.getUUID());
                                    return 0;
                                })
                        )
                )
        );
    }

    public static void morphPlayer(ServerPlayer morphPlayer, ServerPlayer morphIntoTarget, @Nullable Integer key) {
        morphPlayer(morphPlayer.getUUID(), morphIntoTarget.getUUID(), key);
    }

    public static void morphPlayer(UUID morphPlayer, UUID morphIntoTarget, @Nullable Integer key) {
        if (morphedPlayers.containsKey(morphPlayer)) {
            morphedPlayers.get(morphPlayer).addMorph(key, morphIntoTarget);
        }else {
            morphedPlayers.put(morphPlayer, new MorphedPlayer(key, morphIntoTarget));
        }
        PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundMorphPacket(morphPlayer, morphIntoTarget));
    }

    public static void unMorphPlayer(ServerPlayer morphedPlayer, @Nullable Integer key) {
        unMorphPlayer(morphedPlayer.getUUID(), key);
    }

    public static void unMorphPlayer(UUID morphedPlayer, @Nullable Integer key) {
        if (morphedPlayers.containsKey(morphedPlayer)) {
            morphedPlayers.get(morphedPlayer).removeMorph(key);
            if (morphedPlayers.get(morphedPlayer).isEmpty()) {
                removePlayer(morphedPlayer);
            }else {
                PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundMorphPacket(morphedPlayer, morphedPlayers.get(morphedPlayer).getLatestMorph()));
            }
        }
    }

    public static void removePlayer(UUID morphedPlayer) {
        morphedPlayers.remove(morphedPlayer);
        PlayerMorph.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundUnmorphPacket(morphedPlayer));
    }

    public static void sendAllMorphedToPlayer(ServerPlayer player) {
        if (player.getServer() != null) {
            morphedPlayers.keySet().forEach(morphedPlayer -> {
                if (player.getServer().getPlayerList().getPlayer(morphedPlayer) == null) {
                    removePlayer(morphedPlayer);
                }
            });
        }
        morphedPlayers.forEach((morphPlayer, morphIntoTarget) ->
                PlayerMorph.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundMorphPacket(morphPlayer, morphIntoTarget.getLatestMorph())));
    }

    private static class MorphedPlayer {
        protected final Map<Integer, UUID> morphMap = new HashMap<>();
        protected final List<Integer> morphKeyOrder = new ArrayList<>();

        public MorphedPlayer() {}

        public MorphedPlayer(@Nullable Integer startKey, UUID startMorph) {
            morphMap.put(startKey, startMorph);
            morphKeyOrder.add(startKey);
        }

        public void addMorph(@Nullable Integer key, UUID morph) {
            morphMap.put(key, morph);
            morphKeyOrder.remove(key);
            morphKeyOrder.add(key);
        }

        public void removeMorph(@Nullable Integer key) {
            morphMap.remove(key);
            morphKeyOrder.remove(key);
        }

        public boolean isEmpty() {
            return morphKeyOrder.isEmpty() && morphMap.isEmpty();
        }

        public UUID getLatestMorph() {
            return morphMap.get(morphKeyOrder.get(morphKeyOrder.size() - 1));
        }
    }
}
