package com.khrux.argent.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jspecify.annotations.Nullable;

public class ReflectionTexture extends AbstractTexture {
	private @Nullable GpuTexture depthTexture;
	private @Nullable GpuTextureView depthTextureView;

	public void resize(final int width, final int height) {
		if (this.texture != null && this.texture.getWidth(0) == width && this.texture.getHeight(0) == height) {
			return;
		}

		this.releaseTextures();
		GpuDevice device = RenderSystem.getDevice();
		this.texture = device.createTexture(
			() -> "Mirror reflection", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.RGBA8_UNORM, width, height, 1, 1
		);
		this.textureView = device.createTextureView(this.texture);
		this.depthTexture = device.createTexture(() -> "Mirror reflection depth", GpuTexture.USAGE_RENDER_ATTACHMENT, GpuFormat.D32_FLOAT, width, height, 1, 1);
		this.depthTextureView = device.createTextureView(this.depthTexture);
	}

	public GpuTextureView getDepthTextureView() {
		if (this.depthTextureView == null) {
			throw new IllegalStateException("Reflection depth texture does not exist before resize");
		}

		return this.depthTextureView;
	}

	@Override
	protected void releaseTextures() {
		super.releaseTextures();
		if (this.depthTexture != null) {
			this.depthTexture.close();
			this.depthTexture = null;
		}

		if (this.depthTextureView != null) {
			this.depthTextureView.close();
			this.depthTextureView = null;
		}
	}
}
