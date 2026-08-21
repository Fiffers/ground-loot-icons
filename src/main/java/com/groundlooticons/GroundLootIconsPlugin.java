package com.groundlooticons;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Ground Loot Icons",
	description = "Adds item icons next to items in ground loot context menus",
	tags = {"loot", "ground", "items", "icon", "icons", "pickup", "right", "click", "menu"}
)
public class GroundLootIconsPlugin extends Plugin
{
	private static final int PLACEHOLDER_WIDTH = 17;
	private static final int PLACEHOLDER_HEIGHT = 15;
	private int placeholderInternalId = -1;

	private static final Set<MenuAction> GROUND_ITEM_ACTIONS = EnumSet.of(
		MenuAction.GROUND_ITEM_FIRST_OPTION,
		MenuAction.GROUND_ITEM_SECOND_OPTION,
		MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION,
		MenuAction.GROUND_ITEM_FIFTH_OPTION);

	@Inject
	private ChatIconManager chatIconManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GroundLootIconOverlay groundLootIconOverlay;

	@Override
	protected void startUp() throws Exception
	{
		if (this.placeholderInternalId == -1)
		{
			BufferedImage placeholder = new BufferedImage(
					GroundLootIconsPlugin.PLACEHOLDER_WIDTH,
					GroundLootIconsPlugin.PLACEHOLDER_HEIGHT,
					BufferedImage.TYPE_INT_ARGB);

			this.placeholderInternalId = this.chatIconManager.registerChatIcon(placeholder);
		}

		this.overlayManager.add(this.groundLootIconOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.overlayManager.remove(this.groundLootIconOverlay);
	}

	static boolean isGroundItemTake(MenuEntry entry)
	{
		return GROUND_ITEM_ACTIONS.contains(entry.getType())
				&& "Take".equals(Text.removeTags(entry.getOption()));
	}

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened)
	{
		int spriteIndex = this.chatIconManager.chatIconIndex(this.placeholderInternalId);
		boolean placeholderReady = spriteIndex != -1;
		if (!placeholderReady)
		{
			return;
		}

		MenuEntry[] entries = menuOpened.getMenuEntries();
		for (int i = 0; i < entries.length; i++)
		{
			MenuEntry entry = entries[i];

			if (!isGroundItemTake(entry))
			{
				continue;
			}

			entry.setTarget("<img=" + spriteIndex + ">" + entry.getTarget());
		}
	}
}
