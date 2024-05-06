package net.op.render.screens;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.scgi.Display;

import net.op.OpenCraft;
import net.op.render.textures.Assets;
import net.op.util.OCFont;
import net.op.util.Resource;

public class Loadscreen extends Screen {

	public static final Resource RESOURCE = Resource.format("opencraft:screens.loadscreen");

	private static final int TIMEOUT = 3250;
	private static int I_MAX = 480 / 2;
	private static BufferedImage star_background = null;

	public static Loadscreen instance = create();

	private float i = 480 - 1;
	private long start = -1;

	private Loadscreen() {
		super(RESOURCE);
	}

	public static Loadscreen create() {
		return new Loadscreen();
	}

	@Override
	public void render(Graphics g, Assets assets) {
		animatedLS((Graphics2D) g, true, assets);
	}

	public void animatedLS(Graphics2D g2d, boolean slideUp, Assets assets) {
		drawStars(g2d, 100);

		// Draw OpenCraft Text
		g2d.setColor(Color.MAGENTA);
		g2d.setFont(OCFont.getSystemFont("SF Transrobotics").deriveFont(Font.BOLD, 26));
		g2d.drawString(OpenCraft.TECHNICAL_NAME.toUpperCase(), (854 - 350) / 2,
				(480 + 135) / 2);

		// Draw Rectangle
		g2d.setColor(Color.GREEN);
		g2d.drawRoundRect((854 - 451) / 2, (480 - 164) / 2, 421, 112, 15, 15);

		// Draw OpenCraft Text
		g2d.setColor(Color.RED);
		g2d.setFont(g2d.getFont().deriveFont(Font.ITALIC, 70));
		g2d.drawString("OpenCraft", (854 - 376) / 2, (int) i);

		/* OpenCraft Text Slide Up */
		if (slideUp) {
			if (i <= I_MAX) {
				attemptToChange(g2d, assets);
			} else {
				i -= 2;
			}
		}

	}

	private void drawStars(Graphics2D g2d, int stars) {
		BufferedImage oldStar_background = star_background;
		star_background = new BufferedImage(854, 480, BufferedImage.TYPE_INT_RGB);
		Graphics g = star_background.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 854, 480);

		if (oldStar_background != null) {
			g.drawImage(oldStar_background, 0, (int) (System.currentTimeMillis() / 1000 % 100) * -1, null);
		}
		g.dispose();

		Random random = new Random();
		for (int star = 0; star < stars; star++) {
			if ((random.nextBoolean() && random.nextBoolean()) == false) {
				continue;
			}

			int star_x = random.nextInt(854);
			int star_y = random.nextInt(480);

			star_background.setRGB(star_x, star_y, 0xFFFFFF);
		}

		g2d.drawImage(star_background, 0, 0, null);
	}

	private void attemptToChange(Graphics g, Assets assets) {
		final long current = System.currentTimeMillis();

		if (start == -1) {
			start = current;
		}
		
		if (current - start <= TIMEOUT) {
			double alpha = 1 - (current - start) / (double) TIMEOUT;

			BufferedImage bi = new BufferedImage(854, 480, BufferedImage.TYPE_INT_ARGB);

			Graphics gbi = bi.getGraphics();
			MenuScreen.getInstance().render(g, assets);

			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
			((Graphics2D) gbi).setComposite(ac);
			animatedLS((Graphics2D) gbi, false, null);

			g.drawImage(bi, 0, 0, null);
			return;
		}
		
		instance = null;
		System.gc();
		Display.setResizable(true);
		Screen.setCurrent(MenuScreen.class);
	}

	public static Loadscreen getInstance() {
		return instance;
	}

}
