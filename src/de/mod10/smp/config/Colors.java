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

	// GREY
	CROSSROAD = new Color(107, 107, 107),

	// LIGHT GREY
	WAYPOINT = new Color(170, 170, 170),

	// DARK GREY
	DROP = new Color(50, 50, 50),

	// COOL ORANGE
	BLOCK = new Color(224, 152, 40),

	// LIGHT BLUE
	STATION = new Color(205, 201, 203),

	// YELLOW
	BATTERY = new Color(255, 232, 119),

	// LIGHT LIGHT ORANGE
	FILL =  new Color(255, 202, 123);


	/** Robot Colors **/
	public static final Color[] ROBOTS = {

			// WARM ORANGE
			new Color(255, 143, 0),

			// BLUE
			//new Color(52, 152, 219),

			// GREEN
			//new Color(26, 188, 156),

			// PURPLE
			//new Color(155, 89, 182)
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
