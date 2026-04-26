package com.groundlooticons;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.EnumSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Text;

@Singleton
public class GroundLootIconOverlay extends Overlay
{
	private static final Set<MenuAction> GROUND_ITEM_ACTIONS = EnumSet.of(
		MenuAction.GROUND_ITEM_FIRST_OPTION,
		MenuAction.GROUND_ITEM_SECOND_OPTION,
		MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION,
		MenuAction.GROUND_ITEM_FIFTH_OPTION,
		MenuAction.EXAMINE_ITEM_GROUND);

	private static final int HEADER_HEIGHT = 19;
	private static final int ROW_HEIGHT = 15;
	private static final int LEFT_PADDING = 4;
	private static final int ICON_X_OFFSET = 4;
	private static final int ICON_WIDTH = 17;
	private static final int ICON_HEIGHT = 15;

	private final Client client;
	private final ItemManager itemManager;

	@Inject
	GroundLootIconOverlay(Client client, ItemManager itemManager)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!this.client.isMenuOpen())
		{
			return null;
		}

		Menu menu = this.client.getMenu();
		MenuEntry[] entries = menu.getMenuEntries();
		int menuX = menu.getMenuX();
		int menuY = menu.getMenuY();

		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		for (int i = 0; i < entries.length; i++)
		{
			MenuEntry entry = entries[i];

			boolean isGroundItemAction = GroundLootIconOverlay.GROUND_ITEM_ACTIONS.contains(entry.getType());
			if (!isGroundItemAction)
			{
				continue;
			}

			String plainOption = Text.removeTags(entry.getOption());
			boolean isTakeOption = "Take".equals(plainOption);
			if (!isTakeOption)
			{
				continue;
			}

			int itemId = entry.getIdentifier();
			AsyncBufferedImage icon = this.itemManager.getImage(itemId, Integer.MAX_VALUE, false);

			FontMetrics metrics = graphics.getFontMetrics(FontManager.getRunescapeFont());
			int optionWidth = metrics.stringWidth(plainOption + " ");

			int rowIndexFromTop = entries.length - 1 - i;
			int drawX = menuX + GroundLootIconOverlay.LEFT_PADDING + optionWidth + GroundLootIconOverlay.ICON_X_OFFSET;
			int drawY = menuY + GroundLootIconOverlay.HEADER_HEIGHT + rowIndexFromTop * GroundLootIconOverlay.ROW_HEIGHT;

			graphics.drawImage(icon, drawX, drawY, GroundLootIconOverlay.ICON_WIDTH, GroundLootIconOverlay.ICON_HEIGHT, null);
		}

		return null;
	}
}
