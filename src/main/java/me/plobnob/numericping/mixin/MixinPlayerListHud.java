package me.plobnob.numericping.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud extends DrawableHelper {

	private static final float scale = 0.6F;
	private static final float width = MinecraftClient.getInstance().textRenderer.getWidth("1000");

	@SuppressWarnings("resource")
	@Inject(at = @At("HEAD"), method = "renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V", cancellable = true)
	protected void injectRenderLatencyIcon(MatrixStack matrixStack, int i, int j, int k, PlayerListEntry playerListEntry, CallbackInfo info) {
		// Get ping
		int ping = playerListEntry.getLatency();
		// Return if over 999 ping
		if (ping > 999) {
			info.cancel();
			return;
		}
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int rgb = 11141120;
		if (ping < 0) {
			rgb = 255;
		} else if (ping < 80) {
			rgb = 65280;
		} else if (ping < 150) {
			rgb = 43520;
		} else if (ping < 300) {
			rgb = 16776960;
		} else if (ping < 500) {
			rgb = 16711680;
		}
		
		// Get text renderer instance
		TextRenderer tr = MinecraftClient.getInstance().textRenderer;
		// Get ping value as string
		String text = Integer.toString(ping);
		float offset = (width - tr.getWidth(text)) * scale;
		
		// Draw the new ping value
		matrixStack.push();
		matrixStack.scale(scale, scale, scale);
		tr.drawWithShadow(matrixStack, text, (float) ((j + i - 11 + offset) / scale) - 6, (float) (k / scale) + 3, rgb);
		matrixStack.pop();

		info.cancel();
	}

}
