package tuxi.playermorph.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tuxi.playermorph.mixininterface.MorphPlayerInfo;

import java.util.Map;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin implements MorphPlayerInfo {
    @Shadow @Final private GameProfile profile;
    @Shadow private int latency;
    @Shadow @Nullable private String skinModel;
    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;
    @Shadow @Nullable private Component tabListDisplayName;

    @Shadow protected abstract void registerTextures();

    @Unique
    private MorphPlayerInfo wolfBugs$morphedPlayerInfo = null;

    @Override
    public MorphPlayerInfo playermorph$getMorphedPlayerInfo() {
        return wolfBugs$morphedPlayerInfo;
    }

    @Override
    public boolean playermorph$isMorphed() {
        return wolfBugs$morphedPlayerInfo != null;
    }

    @Override
    public void playermorph$morph(MorphPlayerInfo morphIntoPlayerInfo) {
        wolfBugs$morphedPlayerInfo = morphIntoPlayerInfo;
    }

    @Override
    public void playermorph$unmorph() {
        wolfBugs$morphedPlayerInfo = null;
    }


    @Override
    public GameProfile playermorph$getTrueProfile() {
        return this.profile;
    }
    @Override
    public int playermorph$getTrueLatency() {
        return this.latency;
    }
    @Override
    public boolean playermorph$isCapeTrulyLoaded() {
        return playermorph$getTrueCapeLocation() != null;
    }
    @Override
    public boolean playermorph$isSkinTrulyLoaded() {
        return playermorph$getTrueSkinLocation() != null;
    }
    @Override
    public String playermorph$getTrueModelName() {
        return this.skinModel == null ? DefaultPlayerSkin.getSkinModelName(this.profile.getId()) : this.skinModel;
    }
    @Override
    public ResourceLocation playermorph$getTrueSkinLocation() {
        this.registerTextures();
        return MoreObjects.firstNonNull(this.textureLocations.get(MinecraftProfileTexture.Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
    }
    @Override @Nullable
    public ResourceLocation playermorph$getTrueCapeLocation() {
        this.registerTextures();
        return this.textureLocations.get(MinecraftProfileTexture.Type.CAPE);
    }
    @Override @Nullable
    public ResourceLocation playermorph$getTrueElytraLocation() {
        this.registerTextures();
        return this.textureLocations.get(MinecraftProfileTexture.Type.ELYTRA);
    }
    @Override @Nullable
    public PlayerTeam playermorph$getTrueTeam() {
        return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.profile.getName());
    }
    @Override @Nullable
    public Component playermorph$getTrueTabListDisplayName() {
        return this.tabListDisplayName;
    }

    @Inject(method = "getProfile", at = @At("HEAD"), cancellable = true)
    private void overrideProfile(CallbackInfoReturnable<GameProfile> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueProfile());
        }
    }
    @Inject(method = "getLatency", at = @At("HEAD"), cancellable = true)
    private void overrideLatency(CallbackInfoReturnable<Integer> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueLatency());
        }
    }
    @Inject(method = "isCapeLoaded", at = @At("HEAD"), cancellable = true)
    private void overrideCapeLoaded(CallbackInfoReturnable<Boolean> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$isCapeTrulyLoaded());
        }
    }
    @Inject(method = "isSkinLoaded", at = @At("HEAD"), cancellable = true)
    private void overrideSkinLoaded(CallbackInfoReturnable<Boolean> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$isSkinTrulyLoaded());
        }
    }
    @Inject(method = "getModelName", at = @At("HEAD"), cancellable = true)
    private void overrideModelName(CallbackInfoReturnable<String> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueModelName());
        }
    }
    @Inject(method = "getSkinLocation", at = @At("HEAD"), cancellable = true)
    private void overrideSkinLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueSkinLocation());
        }
    }
    @Inject(method = "getCapeLocation", at = @At("HEAD"), cancellable = true)
    private void overrideCapeLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueCapeLocation());
        }
    }
    @Inject(method = "getElytraLocation", at = @At("HEAD"), cancellable = true)
    private void overrideElytraLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueElytraLocation());
        }
    }
    @Inject(method = "getTeam", at = @At("HEAD"), cancellable = true)
    private void overrideTeam(CallbackInfoReturnable<PlayerTeam> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueTeam());
        }
    }
    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    private void overrideTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        if (playermorph$isMorphed()) {
            cir.setReturnValue(this.playermorph$getMorphedPlayerInfo().playermorph$getTrueTabListDisplayName());
        }
    }
}
