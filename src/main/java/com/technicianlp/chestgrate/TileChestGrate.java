package com.technicianlp.chestgrate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;

public class TileChestGrate extends TileEntity implements IInventory {

    private final ItemStack[] contents = new ItemStack[5];
    public String customName = null;

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) return;
        TileEntity below = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        if (below instanceof TileAlchemyFurnaceAdvanced furnace) {
            for (int i = 0; i < this.contents.length; i++) {
                if (this.contents[i] == null) continue;
                if (furnace.process(this.contents[i])) {
                    this.decrStackSize(i, 1);
                    this.worldObj.playSoundEffect(
                        this.xCoord,
                        this.yCoord - 1,
                        this.zCoord,
                        "thaumcraft:bubble",
                        0.2F,
                        1.0F + this.worldObj.rand.nextFloat() * 0.4F);
                    break;
                }
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= this.getSizeInventory()) return null;
        return this.contents[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.contents[index] != null) {
            ItemStack itemstack;
            if (this.contents[index].stackSize <= count) {
                itemstack = this.contents[index];
                this.contents[index] = null;
            } else {
                itemstack = this.contents[index].splitStack(count);
                if (this.contents[index].stackSize == 0) {
                    this.contents[index] = null;
                }
            }
            this.markDirty();
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack stack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= this.getSizeInventory()) return;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();

        if (stack != null && stack.stackSize == 0) stack = null;

        this.contents[index] = stack;
        this.markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null) {
            return false;
        }

        AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
        al = ThaumcraftCraftingManager.getBonusTags(stack, al);
        return al.size() != 0;
    }

    @Override
    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.customName : "container.alchgrate";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.equals("");
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.xCoord, this.yCoord, this.zCoord) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) != null) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setByte("Slot", (byte) i);
                this.getStackInSlot(i)
                    .writeToNBT(stackTag);
                list.appendTag(stackTag);
            }
        }
        nbt.setTag("Items", list);

        if (this.hasCustomInventoryName()) {
            nbt.setString("CustomName", this.customName);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        NBTTagList list = nbt.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getByte("Slot") & 255;
            this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
        }

        if (nbt.hasKey("CustomName", 8)) {
            this.customName = nbt.getString("CustomName");
        }
    }
}
