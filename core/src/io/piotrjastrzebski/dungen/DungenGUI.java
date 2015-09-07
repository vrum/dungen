/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package io.piotrjastrzebski.dungen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import java.text.DecimalFormat;

public class DungenGUI extends VisWindow {
	GenSettings settings;
	VisSlider grid;
	VisSlider count;
	VisSlider sWidth;
	VisSlider sHeight;
	VisSlider rWidth;
	VisSlider rHeight;
	VisSlider mainScale;
	VisSlider reconChance;

	public DungenGUI (final Restarter restarter) {
		super("Settings");
		settings = new GenSettings();
		VisTable c = new VisTable(true);

		VisTextButton restart = new VisTextButton("Restart");
		restart.addListener(new ClickListener() {
			@Override public void clicked (InputEvent event, float x, float y) {
				restarter.restart(settings);
			}
		});
		c.add(restart);
		c.row();
		c.add(new VisLabel("Hover on labels for tooltips")).colspan(3);
		c.row();

		grid = slider(c, "Grid Size", "Size of the grid in units\n1u=32px at 720p", 0.1f, 1.f, 0.01f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setGridSize(value);
			}
		});
		c.row();
		count = slider(c, "Room Count", "Number of rooms to be spawned", 50f, 500f, 10f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setCount((int)value);
			}
		});
		c.row();
		sWidth = slider(c, "Spawn Width", "Width of the ellipse the rooms will be spawned in\nin grid units", 5f, 40f, 5f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setSpawnWidth(value);
			}
		});
		c.row();
		sHeight = slider(c, "Spawn Height", "Height of the ellipse the rooms will be spawned in\nin grid units", 5f, 40f, 5f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setSpawnHeight(value);
			}
		});
		c.row();
		rWidth = slider(c, "Room Width", "Mean room width in grid units", 1f, 10f, 1f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setRoomWidth(value);
			}
		});
		c.row();
		rHeight = slider(c, "Room Height", "Mean room height in grid units", 1f, 10f, 1f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setRoomHeight(value);
			}
		});
		c.row();
		mainScale = slider(c, "Main Scale", "Percent of the mean size for a room to be marked as main", 0.5f, 2.f, 0.05f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setMainRoomScale(value);
			}
		});
		c.row();
		reconChance = slider(c, "Reconnect %", "Chance to reconnect the path after\nminimum spanning tree is created",
			0.0f, 1.f, 0.05f, new SliderAction() {
			@Override public void setValue (float value) {
				settings.setReconnectChance(value);
			}
		});
		c.row();
		add(c);
		pack();
	}

	private abstract class SliderAction {
		public abstract void setValue (float value);
	}

	private VisSlider slider(Table container, String text, String tooltip, float min, float max, float step, final SliderAction action) {
		VisLabel label = new VisLabel(text);
		final VisSlider slider = new VisSlider(min, max, step, false);
		container.add(label).left();
		new Tooltip(label, tooltip);
		float scale = VisUI.getSizes().scaleFactor;
		container.add(slider).prefWidth(120 * scale);
		final VisLabel val = new VisLabel("000.00");
		slider.addListener(new ChangeListener() {
			@Override public void changed (ChangeEvent event, Actor actor) {
				val.setText(toStr(slider.getValue()));
				action.setValue(slider.getValue());
			}
		});
		val.setAlignment(Align.right);
		container.add(val).right().width(45 * scale);
		return slider;
	}

	/*
	Simple String.format() replacements as it doesnt work on GWT
	 */
	StringBuilder sb = new StringBuilder();
	private String toStr(float value) {
		sb.setLength(0);
		int d = Math.round(value);
		sb.append(d);
		sb.append(".");
		String s = Float.toString((value - d) * 100);
		if (s.length() > 2) {
			sb.append(s.charAt(0));
			char c = s.charAt(1);
			if (c != '.') sb.append(c);
		} else {
			sb.append(s);
		}
		return sb.toString();
	}

	public void setDefaults(GenSettings settings) {
		this.settings.copy(settings);
		grid.setValue(settings.getGridSize());
		count.setValue(settings.getCount());
		sWidth.setValue(settings.getRawSpawnWidth());
		sHeight.setValue(settings.getRawSpawnHeight());
		rWidth.setValue(settings.getRawRoomWidth());
		rHeight.setValue(settings.getRawRoomHeight());
		mainScale.setValue(settings.getMainRoomScale());
		reconChance.setValue(settings.getReconnectChance());
		pack();
	}

	public GenSettings getSettings () {
		return settings;
	}

	public interface Restarter {
		public void restart(GenSettings settings);
	}
}