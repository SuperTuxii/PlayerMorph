package tuxi.playermorph.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import tuxi.playermorph.mixininterface.GlowEntity;

import java.util.Collection;

public class GlowCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("glow")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("add")
                                .then(Commands.argument("viewers", EntityArgument.players())
                                        .executes(ctx -> {
                                            GlowEntity target = (GlowEntity) EntityArgument.getEntity(ctx, "target");
                                            Collection<ServerPlayer> viewers = EntityArgument.getPlayers(ctx, "viewers");
                                            for (ServerPlayer viewer : viewers) {
                                                target.playermorph$setGlow(viewer, true);
                                            }
                                            return 0;
                                        })
                                )
                        ).then(Commands.literal("remove")
                                .then(Commands.argument("viewers", EntityArgument.players())
                                        .executes(ctx -> {
                                            GlowEntity target = (GlowEntity) EntityArgument.getEntity(ctx, "target");
                                            Collection<ServerPlayer> viewers = EntityArgument.getPlayers(ctx, "viewers");
                                            for (ServerPlayer viewer : viewers) {
                                                target.playermorph$setGlow(viewer, false);
                                            }
                                            return 0;
                                        })
                                )
                        ).then(Commands.literal("clear")
                                .executes(ctx -> {
                                    GlowEntity target = (GlowEntity) EntityArgument.getEntity(ctx, "target");
                                    target.playermorph$clearGlow();
                                    return 0;
                                })
                        )
                )
        );
    }
}
