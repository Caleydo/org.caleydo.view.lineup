/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.lineup.internal.ui;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.lineup.internal.event.ColorChosenEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Samuel Gratzl
 *
 */
public class ColorChooserButton extends GLButton implements IGLRenderer, GLButton.ISelectionCallback {

	private IColorSelectionCallback callback = DUMMY_CALLBACK;
	private Color color;

	/**
	 *
	 */
	public ColorChooserButton(Color color) {
		this.color = color;
		setRenderer(this);
		setHoverEffect(GLRenderers.drawText("Choose", VAlign.CENTER, new GLPadding(2)));
		setCallback(this);
	}

	/**
	 * @param callback setter, see {@link callback}
	 */
	public ColorChooserButton setCallback(IColorSelectionCallback callback) {
		if (callback == null)
			callback = DUMMY_CALLBACK;
		this.callback = callback;
		return this;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}


	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(color).fillRect(0, 0, w, h);
	}

	@ListenTo(sendToMe = true)
	private void onColorChosen(ColorChosenEvent event) {
		RGB rgb = event.getRgb();
		this.color = new Color(rgb.red, rgb.green, rgb.blue);
		callback.onColorSelectionChanged(this, color);
		repaint();
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				ColorDialog dialog = new ColorDialog(display.getActiveShell());
				int[] rgba = color.getIntRGBA();
				dialog.setRGB(new RGB(rgba[0], rgba[1], rgba[2]));
				dialog.setText("Select color");
				RGB open = dialog.open();
				if (open != null)
					EventPublisher.trigger(new ColorChosenEvent(open).to(ColorChooserButton.this));
			}
		});
	}

	/**
	 * callback interface for selection changes of a button
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IColorSelectionCallback {
		void onColorSelectionChanged(ColorChooserButton button, Color color);
	}

	private static final IColorSelectionCallback DUMMY_CALLBACK = new IColorSelectionCallback() {
		@Override
		public void onColorSelectionChanged(ColorChooserButton button, Color color) {

		}
	};
}
