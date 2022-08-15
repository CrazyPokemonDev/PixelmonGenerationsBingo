package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import de.crazypokemondev.pixelmongenerations.bingo.client.gui.GuiResources;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig.Tasks.CraftItem.items;

public class CraftItemTask extends BingoTask {
    public static final String ID = "craft_item";

    private final Item item;

    public CraftItemTask(Status status, String[] params) {
        super(status);
        if (params.length < 4) throw new IndexOutOfBoundsException("Missing parameter for task of type " + ID);
        this.item = Item.REGISTRY.getObject(new ResourceLocation(params[2], params[3]));
    }

    public Item getItem() {
        return item;
    }

    public CraftItemTask(Item item) {
        if (item.getRegistryName() == null) {
            throw new IllegalArgumentException("Tried to create CraftItemTask with unregistered item.");
        }
        this.item = item;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
        return getStatus().toString() + PARAM_SEPARATOR + ID + PARAM_SEPARATOR
                + registryName.getNamespace() + PARAM_SEPARATOR + registryName.getPath();
    }

    @Override
    public void drawIcon(GuiScreen screen, int x, int y, int w, int h, float zLevel) {
        RenderItem renderItem = screen.mc.getRenderItem();
        int offsetX = (w - 16) / 2;
        int offsetY = (h - 16) / 2;
        renderItem.renderItemIntoGUI(new ItemStack(item), x + offsetX, y + offsetY);
        screen.mc.getTextureManager().bindTexture(GuiResources.guiIcons);
        switch (getStatus()) {
            case Completed:
                drawCheckMarkIcon(screen, x, y, w, h);
                break;
            case Open:
            default:
                screen.drawTexturedModalRect(
                        x + w - GuiResources.CRAFT_ITEM_ICON_WIDTH, y + h - GuiResources.CRAFT_ITEM_ICON_HEIGHT,
                        GuiResources.CRAFT_ITEM_ICON_X, GuiResources.CRAFT_ITEM_ICON_Y,
                        GuiResources.CRAFT_ITEM_ICON_WIDTH, GuiResources.CRAFT_ITEM_ICON_HEIGHT);
                break;
        }
    }

    public static CraftItemTask getRandomTask(World world) {
        int index = world.rand.nextInt(items.size());
        return new CraftItemTask(items.get(index));
    }

    @NotNull
    @Override
    public Optional<List<String>> getToolTip() {
        String translateKey = getTranslateKey();
        String formattedString = I18n.format(translateKey, I18n.format(item.getTranslationKey() + ".name"));
        return Optional.of(Collections.singletonList(formattedString));
    }

    @Override
    public void completeTask(EntityPlayerMP player) {
        super.completeTask(player);
        player.sendMessage(new TextComponentTranslation(
                "messages.pixelmongenerationsbingo.tasks." + ID + ".completed",
                new TextComponentTranslation(item.getTranslationKey() + ".name")));
    }
}
