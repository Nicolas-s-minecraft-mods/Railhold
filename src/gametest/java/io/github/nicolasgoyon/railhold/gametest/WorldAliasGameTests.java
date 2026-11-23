package io.github.nicolasgoyon.railhold.gametest;

import io.github.nicolasgoyon.railhold.Railhold;
import io.github.nicolasgoyon.railhold.rail.CartLinks;
import io.github.nicolasgoyon.railhold.rail.FurnaceLocomotive;
import io.github.nicolasgoyon.railhold.registry.ModEntities;
import io.github.nicolasgoyon.railhold.registry.ModItems;
import io.github.nicolasgoyon.railhold.registry.WorldAliases;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * In-game tests for the ids of the worlds of Folkhold Balance ({@link WorldAliases}). The tests read saved data with
 * the namespace {@code folkhold_balance}. The tests use the template {@code railhold:bench}.
 */
@GameTestHolder(Railhold.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldAliasGameTests {
    private WorldAliasGameTests() {
    }

    /** A saved item of each type with the namespace {@code folkhold_balance} loads as the item of Railhold. */
    @GameTest(template = "bench", batch = "railhold_aliases")
    public static void oldItemIdsLoadAsRailholdItems(GameTestHelper helper) {
        List<DeferredItem<? extends Item>> items = List.of(ModItems.FURNACE_LOCOMOTIVE, ModItems.HEAVY_LOCOMOTIVE,
                ModItems.DOUBLE_CHEST_MINECART, ModItems.SHULKER_BOX_MINECART, ModItems.ENDER_CHEST_MINECART);
        for (DeferredItem<? extends Item> item : items) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", WorldAliases.FOLKHOLD_BALANCE + ":" + item.getId().getPath());
            tag.putInt("count", 1);
            ItemStack stack = ItemStack.parseOptional(helper.getLevel().registryAccess(), tag);
            helper.assertTrue(stack.is(item.get()), "The saved item " + tag.getString("id") + " loads as " + stack);
        }
        helper.succeed();
    }

    /**
     * A saved furnace locomotive with the namespace {@code folkhold_balance} loads as the furnace locomotive of
     * Railhold, and keeps its chain link.
     */
    @GameTest(template = "bench", batch = "railhold_aliases")
    public static void oldLocomotiveLoadsWithItsLink(GameTestHelper helper) {
        UUID back = UUID.randomUUID();
        CompoundTag link = new CompoundTag();
        link.put("back", NbtUtils.createUUID(back));
        CompoundTag attachments = new CompoundTag();
        attachments.put(WorldAliases.FOLKHOLD_BALANCE + ":cart_link", link);
        CompoundTag tag = new CompoundTag();
        tag.putString("id", WorldAliases.FOLKHOLD_BALANCE + ":" + ModEntities.FURNACE_LOCOMOTIVE.getId().getPath());
        tag.put(AttachmentHolder.ATTACHMENTS_NBT_KEY, attachments);
        Optional<Entity> entity = EntityType.create(tag, helper.getLevel());
        helper.assertTrue(entity.isPresent(), "The saved locomotive does not load");
        helper.assertTrue(entity.get() instanceof FurnaceLocomotive,
                "The saved locomotive loads as " + entity.get().getType());
        helper.assertTrue(CartLinks.get((AbstractMinecart) entity.get()).back().equals(Optional.of(back)),
                "The saved locomotive loses its chain link");
        entity.get().discard();
        helper.succeed();
    }
}
