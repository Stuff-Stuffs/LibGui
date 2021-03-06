package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.math.Quaternion;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

import javax.annotation.Nullable;

/**
 * A vanilla-style labeled slider widget.
 *
 * <p>In addition to the standard slider listeners,
 * labeled sliders also support "label updaters" that can update the label
 * when the value is changed.
 *
 * @see WAbstractSlider for more information about listeners
 */
public class WLabeledSlider extends WAbstractSlider {
	private static final Quaternion ROTATION_Z_270 = Vector3f.POSITIVE_X.getDegreesQuaternion(270);

	@Nullable private StringRenderable label = null;
	@Nullable private LabelUpdater labelUpdater = null;
	private HorizontalAlignment labelAlignment = HorizontalAlignment.CENTER;

	/**
	 * Constructs a horizontal slider with no default label.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public WLabeledSlider(int min, int max) {
		this(min, max, Axis.HORIZONTAL);
	}

	/**
	 * Constructs a slider with no default label.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param axis the slider axis
	 */
	public WLabeledSlider(int min, int max, Axis axis) {
		super(min, max, axis);
	}

	/**
	 * Constructs a slider.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param axis the slider axis
	 * @param label the slider label (can be null)
	 */
	public WLabeledSlider(int min, int max, Axis axis, @Nullable StringRenderable label) {
		this(min, max, axis);
		this.label = label;
	}

	/**
	 * Constructs a horizontal slider.
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param label the slider label (can be null)
	 */
	public WLabeledSlider(int min, int max, @Nullable StringRenderable label) {
		this(min, max);
		this.label = label;
	}

	@Override
	public void setSize(int x, int y) {
		if (axis == Axis.HORIZONTAL) {
			super.setSize(x, 20);
		} else {
			super.setSize(20, y);
		}
	}

	/**
	 * Gets the current label of this slider.
	 *
	 * @return the label
	 */
	@Nullable
	public StringRenderable getLabel() {
		return label;
	}

	/**
	 * Sets the label of this slider.
	 *
	 * @param label the new label
	 */
	public void setLabel(@Nullable StringRenderable label) {
		this.label = label;
	}

	@Override
	protected void onValueChanged(int value) {
		super.onValueChanged(value);
		if (labelUpdater != null) {
			label = labelUpdater.updateLabel(value);
		}
	}

	/**
	 * Gets the text alignment of this slider's label.
	 *
	 * @return the alignment
	 */
	public HorizontalAlignment getLabelAlignment() {
		return labelAlignment;
	}

	/**
	 * Sets the text alignment of this slider's label.
	 *
	 * @param labelAlignment the new alignment
	 */
	public void setLabelAlignment(HorizontalAlignment labelAlignment) {
		this.labelAlignment = labelAlignment;
	}

	/**
	 * Gets the {@link LabelUpdater} of this slider.
	 *
	 * @return the label updater
	 */
	@Nullable
	public LabelUpdater getLabelUpdater() {
		return labelUpdater;
	}

	/**
	 * Sets the {@link LabelUpdater} of this slider.
	 *
	 * @param labelUpdater the new label updater
	 */
	public void setLabelUpdater(@Nullable LabelUpdater labelUpdater) {
		this.labelUpdater = labelUpdater;
	}

	@Override
	protected int getThumbWidth() {
		return 8;
	}

	@Override
	protected boolean isMouseInsideBounds(int x, int y) {
		return x >= 0 && x <= width && y >= 0 && y <= height;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		int aWidth = axis == Axis.HORIZONTAL ? width : height;
		int aHeight = axis == Axis.HORIZONTAL ? height : width;
		int rotMouseX = axis == Axis.HORIZONTAL
				? (direction == Direction.LEFT ? width - mouseX : mouseX)
				: (direction == Direction.UP ? height - mouseY : mouseY);
		int rotMouseY = axis == Axis.HORIZONTAL ? mouseY : mouseX;

		matrices.push();
		matrices.translate(x, y, 0);
		if (axis == Axis.VERTICAL) {
			matrices.translate(0, height, 0);
			matrices.multiply(ROTATION_Z_270);
		}
		drawButton(0, 0, 0, aWidth);

		// 1: regular, 2: hovered, 0: disabled/dragging
		int thumbX = Math.round(coordToValueRatio * (value - min));
		int thumbY = 0;
		int thumbWidth = getThumbWidth();
		int thumbHeight = aHeight;
		boolean hovering = rotMouseX >= thumbX && rotMouseX <= thumbX + thumbWidth && rotMouseY >= thumbY && rotMouseY <= thumbY + thumbHeight;
		int thumbState = dragging || hovering ? 2 : 1;

		drawButton(thumbX, thumbY, thumbState, thumbWidth);

		if (thumbState == 1 && isFocused()) {
			float px = 1 / 32f;
			ScreenDrawing.texturedRect(thumbX, thumbY, thumbWidth, thumbHeight, WSlider.LIGHT_TEXTURE, 24*px, 0*px, 32*px, 20*px, 0xFFFFFFFF);
		}

		if (label != null) {
			int color = isMouseInsideBounds(mouseX, mouseY) ? 0xFFFFA0 : 0xE0E0E0;
			ScreenDrawing.drawStringWithShadow(matrices, label, labelAlignment, 2, aHeight / 2 - 4, aWidth - 4, color);
		}
		matrices.pop();
	}

	// state = 1: regular, 2: hovered, 0: disabled/dragging
	@Environment(EnvType.CLIENT)
	private void drawButton(int x, int y, int state, int width) {
		float px = 1 / 256f;
		float buttonLeft = 0 * px;
		float buttonTop = (46 + (state * 20)) * px;
		int halfWidth = width / 2;
		if (halfWidth > 198) halfWidth = 198;
		float buttonWidth = halfWidth * px;
		float buttonHeight = 20 * px;
		float buttonEndLeft = (200 - halfWidth) * px;

		ScreenDrawing.texturedRect(x, y, halfWidth, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonHeight, 0xFFFFFFFF);
		ScreenDrawing.texturedRect(x + halfWidth, y, halfWidth, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonEndLeft, buttonTop, 200 * px, buttonTop + buttonHeight, 0xFFFFFFFF);
	}

	/**
	 * A label updater updates the label of a slider based on the current value.
	 *
	 * <p>Useful for situations when you want to have display values on the slider.
	 */
	@FunctionalInterface
	public interface LabelUpdater {
		/**
		 * Gets the updated label for the new slider value.
		 *
		 * @param value the slider value
		 * @return the label
		 */
		StringRenderable updateLabel(int value);
	}
}
