package de.mod10.smp.config;

import java.awt.*;

/**
 * Config for Colors in {@link de.mod10.smp.GridView}.
 * Please use rgb(a) values, so Intellij can display a Color Picker
 *
 */
public class Colors {

	/** Block Colors **/
	public static final Color

	// WHITE
	WAYPOINT = new Color(240, 240, 240),

	// LIGHT GREY
	CROSSROAD = new Color(220, 220, 220),

	// ORANGE
	DROP = new Color(255, 200, 0),

	// BLACK
	BLOCK = new Color(0, 0, 0),

	// LIGHT BLUE
	STATION = new Color(155, 212, 255),

	// YELLOW
	BATTERY = new Color(255, 255, 0),

	// MAGENTA
	FILL =  new Color(255, 0, 255);


	/** Robot Colors **/
	public static final Color[] ROBOTS = {

			// RED
			new Color(231, 76, 60),

			// BLUE
			new Color(52, 152, 219),

			// GREEN
			new Color(26, 188, 156),

			// PURPLE
			new Color(155, 89, 182)
	};


	/** Mark Color **/
	public static final Color

	// RED
	LINE = new Color(255, 0, 0),

	// BLACK
	SELECTED = new Color(0, 0, 0),

	// BLACK
	MOUSE_VALID = new Color(0, 0, 0),

	// GREY
	MOUSE_INVALID = new Color(128, 128, 128);


	/** Background **/
	public static final Color

	// BLACK
	BORDER = new Color(0, 0, 0),

	// DARK GREY
	BACKGROUND = new Color(42, 42, 42);
}
