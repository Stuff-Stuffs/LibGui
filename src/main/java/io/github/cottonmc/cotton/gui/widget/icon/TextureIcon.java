package io.github.cottonmc.cotton.gui.widget.icon;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * An icon that draws a texture.
 *
 * @since 2.2.0
 */
public class TextureIcon implements Icon {
	private final Identifier texture;
	private float opacity = 1f;
	private int color = 0xFF_FFFFFF;

	/**
	 * Constructs a new texture icon.
	 *
	 * @param texture the identifier of the icon texture
	 */
	public TextureIcon(Identifier texture) {
		this.texture = texture;
	}

	/**
	 * Gets the opacity of the texture.
	 *
	 * @return the opacity
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity of the texture.
	 *
	 * @param opacity the new opacity between 0 (fully transparent) and 1 (fully opaque)
	 * @return this icon
	 */
	public TextureIcon setOpacity(float opacity) {
		this.opacity = opacity;
		return this;
	}

	/**
	 * Gets the color tint of the texture.
	 *
	 * @return the color tint
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the color tint of the texture.
	 *
	 * @param color the new color tint
	 * @return this icon
	 */
	public TextureIcon setColor(int color) {
		this.color = color;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int size) {
		ScreenDrawing.texturedRect(x, y, size, size, texture, color, opacity);
	}
}
