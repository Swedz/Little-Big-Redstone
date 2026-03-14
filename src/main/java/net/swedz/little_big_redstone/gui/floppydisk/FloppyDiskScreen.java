package net.swedz.little_big_redstone.gui.floppydisk;

import net.minecraft.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRTags;
import net.swedz.little_big_redstone.item.floppydisk.FloppyDiskProgramName;
import net.swedz.little_big_redstone.microchip.Microchip;
import net.swedz.little_big_redstone.network.packet.FloppyDiskLoadPacket;
import net.swedz.little_big_redstone.network.packet.FloppyDiskSavePacket;
import net.swedz.tesseract.neoforge.api.Assert;
import net.swedz.tesseract.neoforge.gui.widget.AutoFillEditBox;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class FloppyDiskScreen extends Screen
{
	private final InteractionHand hand;
	
	private int leftPos, topPos;
	private int uiWidth, uiHeight;
	
	private List<String> existingFiles = List.of();
	
	private AutoFillEditBox input;
	private Button          save, load;
	
	public FloppyDiskScreen(InteractionHand hand)
	{
		super(LBR.text().floppyDisk());
		
		this.hand = hand;
		
		uiWidth = 180;
		uiHeight = 214;
	}
	
	@Override
	protected void init()
	{
		leftPos = width / 2 - uiWidth / 2;
		topPos = height / 2 - uiHeight / 2;
		
		existingFiles = getExistingFiles();
		
		var stack = minecraft.player.getItemInHand(hand);
		boolean hasData = stack.has(LBRComponents.FLOPPY_DISK);
		boolean hasInput = input != null && !input.getValue().isEmpty();
		
		save = this.addRenderableWidget(Button.builder(LBR.text().floppyDiskButtonSave(), (b) -> this.save()).bounds(leftPos, topPos + uiHeight - 40 - 3, uiWidth / 2 - 3, 20).build());
		save.active = hasData && hasInput;
		
		load = this.addRenderableWidget(Button.builder(LBR.text().floppyDiskButtonLoad(), (b) -> this.load()).bounds(leftPos + uiWidth / 2 + 3, topPos + uiHeight - 40 - 3, uiWidth / 2 - 3, 20).build());
		load.active = hasInput;
		this.addRenderableWidget(Button.builder(LBR.text().floppyDiskButtonClose(), (b) -> this.close()).bounds(leftPos, topPos + uiHeight - 20, uiWidth, 20).build());
		
		input = new AutoFillEditBox(minecraft.font, leftPos, topPos + uiHeight / 2 - 20, uiWidth, 20, Component.empty(), () -> existingFiles, 3);
		input.setMaxLength(FloppyDiskProgramName.MAX_LENGTH);
		input.setFilter((s) -> s.matches(FloppyDiskProgramName.PATTERN));
		input.setResponder((s) ->
		{
			load.active = existingFiles.contains(s);
			if(hasData)
			{
				save.active = !s.isEmpty();
			}
		});
		this.addRenderableWidget(input);
	}
	
	private static boolean isValidItem(ItemStack stack)
	{
		return !stack.isEmpty() && stack.is(LBRTags.Items.FLOPPY_DISKS);
	}
	
	private static Path path()
	{
		return FMLPaths.GAMEDIR.get()
				.resolve(LBR.ID)
				.resolve("microchips");
	}
	
	public static void createPath()
	{
		try
		{
			Files.createDirectories(path());
		}
		catch(IOException ex)
		{
			LBR.LOGGER.error("Failed to create microchips data file path");
			throw new RuntimeException(ex);
		}
	}
	
	private static Path path(String name) throws IOException
	{
		Assert.notNull(name);
		var folder = path();
		Files.createDirectories(folder);
		return FileUtil.createPathToResource(folder, name, ".snbt");
	}
	
	private static List<String> getExistingFiles()
	{
		var path = path();
		if(!Files.exists(path))
		{
			return List.of();
		}
		try(var files = Files.list(path))
		{
			return files
					.filter(Files::isRegularFile)
					.filter((p) -> p.getFileName().toString().endsWith(".snbt"))
					.map((p) ->
					{
						String fileName = p.getFileName().toString();
						return fileName.substring(0, fileName.lastIndexOf('.'));
					})
					.toList();
		}
		catch(IOException ex)
		{
			LBR.LOGGER.warn("Failed to read files", ex);
			return List.of();
		}
	}
	
	private static boolean saveToFile(String name, Microchip.Immutable microchip)
	{
		try
		{
			var path = path(name);
			var tag = Microchip.Immutable.CODEC.encodeStart(NbtOps.INSTANCE, microchip).getOrThrow();
			try(OutputStream outputStream = Files.newOutputStream(path))
			{
				outputStream.write(new SnbtPrinterTagVisitor().visit(tag).getBytes(StandardCharsets.UTF_8));
			}
			return true;
		}
		catch(Exception ex)
		{
			LBR.LOGGER.error("Failed to save microchip {} to file", name, ex);
			return false;
		}
	}
	
	private static boolean fileExists(String name)
	{
		try
		{
			return Files.exists(path(name));
		}
		catch(IOException ex)
		{
			LBR.LOGGER.error("Failed to build microchip directory", ex);
			return false;
		}
	}
	
	private static Optional<Microchip.Immutable> loadFromFile(String name)
	{
		try
		{
			var path = path(name);
			if(Files.exists(path))
			{
				try(var inputStream = new FastBufferedInputStream(Files.newInputStream(path)))
				{
					var tag = TagParser.parseTag(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
					var microchip = Microchip.Immutable.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
					return Optional.of(microchip);
				}
			}
		}
		catch(Exception ex)
		{
			LBR.LOGGER.error("Failed to read microchip {} from file", name, ex);
		}
		return Optional.empty();
	}
	
	private void close()
	{
		minecraft.setScreen(null);
	}
	
	private void save()
	{
		this.close();
		
		var player = minecraft.player;
		var stack = player.getItemInHand(hand);
		if(!isValidItem(stack))
		{
			LBR.LOGGER.warn("Failed to save floppy disk because it is no longer in hand or is missing data.");
			return;
		}
		
		var microchip = stack.get(LBRComponents.FLOPPY_DISK);
		
		var name = input.getValue();
		if(name.isEmpty() || !FloppyDiskProgramName.isValid(name))
		{
			return;
		}
		
		new FloppyDiskSavePacket(hand, new FloppyDiskProgramName(name)).sendToServer();
		
		boolean saved = saveToFile(name, microchip);
		player.sendSystemMessage(saved ?
				LBR.text().floppyDiskFileSaved(name) :
				LBR.text().floppyDiskFileFailedToSave());
	}
	
	private void load()
	{
		this.close();
		
		var player = minecraft.player;
		var stack = player.getItemInHand(hand);
		if(!isValidItem(stack))
		{
			LBR.LOGGER.warn("Failed to load floppy disk because it is no longer in hand.");
			return;
		}
		
		var name = input.getValue();
		if(fileExists(name) && FloppyDiskProgramName.isValid(name))
		{
			var microchip = loadFromFile(name);
			if(microchip.isPresent())
			{
				new FloppyDiskLoadPacket(hand, new FloppyDiskProgramName(name), microchip.get()).sendToServer();
			}
			else
			{
				player.sendSystemMessage(LBR.text().floppyDiskFileFailedToLoad());
			}
		}
		else
		{
			player.sendSystemMessage(LBR.text().floppyDiskFileDoesntExist(name));
		}
	}
	
	@Override
	protected void setInitialFocus()
	{
		this.setInitialFocus(input);
	}
	
	@Override
	public void resize(Minecraft minecraft, int width, int height)
	{
		String inputText = input.getValue();
		super.resize(minecraft, width, height);
		input.setValue(inputText);
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	@Override
	public void render(GuiGraphics vanilla, int mouseX, int mouseY, float partialTick)
	{
		super.render(vanilla, mouseX, mouseY, partialTick);
		
		var graphics = new TesseractGuiGraphics(vanilla);
		
		graphics.drawString(title, leftPos + uiWidth / 2 - minecraft.font.width(title) / 2, topPos);
		
		graphics.drawString(LBR.text().floppyDiskInputProgramName(), input.getX(), input.getY() - minecraft.font.lineHeight);
	}
	
	@Override
	public void tick()
	{
		var stack = minecraft.player.getItemInHand(hand);
		if(!isValidItem(stack))
		{
			this.close();
		}
	}
}
