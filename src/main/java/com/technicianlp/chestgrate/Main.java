package com.technicianlp.chestgrate;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

@Mod(
    modid = "alchgrate",
    name = "Alchemical Grate",
    version = Tags.VERSION,
    dependencies = "required-after:Forge@[10.13.4.1448,);required-after:Thaumcraft@[4.2.3.5,)")
public class Main {

    @Mod.Instance
    public static Main instance;

    public BlockChestGrate block;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.block = new BlockChestGrate();

        GameRegistry.registerBlock(this.block, "alchgrate");
        GameRegistry.registerTileEntity(TileChestGrate.class, "alchgrate.alchgrate");

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ResearchCategories.registerCategory(
            "BASICS",
            new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
            "THAUMATURGY",
            new ResourceLocation("thaumcraft", "textures/misc/r_thaumaturgy.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
            "ALCHEMY",
            new ResourceLocation("thaumcraft", "textures/misc/r_crucible.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        String category = "ARTIFICE";
        ResearchCategories.registerCategory(
            category,
            new ResourceLocation("thaumcraft", "textures/misc/r_artifice.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
            "GOLEMANCY",
            new ResourceLocation("thaumcraft", "textures/misc/r_golemancy.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
        ResearchCategories.registerCategory(
            "ELDRITCH",
            new ResourceLocation("thaumcraft", "textures/misc/r_eldritch.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png"));

        ItemStack dropper;
        if (Loader.isModLoaded("Botania")) {
            dropper = new ItemStack((Item) Item.itemRegistry.getObject("Botania:openCrate"));
        } else {
            dropper = new ItemStack(Blocks.dropper);
        }

        ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe(
            "ALCHGRATE",
            new ItemStack(this.block, 1),
            new AspectList().add(Aspect.ORDER, 50)
                .add(Aspect.ENTROPY, 50)
                .add(Aspect.EARTH, 25),
            "TGT",
            "VSV",
            "TCT",
            'V',
            "plateVoid",
            'T',
            "screwThaumium",
            'G',
            dropper,
            'S',
            new ItemStack(ConfigItems.itemShard, 1, 6),
            'C',
            new ItemStack(ConfigBlocks.blockChestHungry));

        ResearchItem research = new ResearchItem(
            "ALCHGRATE",
            category,
            new AspectList().add(Aspect.VOID, 1)
                .add(Aspect.AURA, 1)
                .add(Aspect.MAGIC, 1)
                .add(Aspect.ORDER, 1)
                .add(Aspect.ENTROPY, 1),
            4,
            -1,
            0,
            new ItemStack(block));

        ResearchPage page1, page2;
        page1 = new ResearchPage("tc.research_page.ALCHGRATE.1");
        page2 = new ResearchPage(recipe);

        research.setPages(page1, page2)
            .setParents("ADVALCHEMYFURNACE", "HUNGRYCHEST", "GRATE")
            .setConcealed()
            .registerResearchItem();

        ResearchCategories.addResearch(research);
    }

}
