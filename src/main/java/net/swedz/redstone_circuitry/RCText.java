package net.swedz.redstone_circuitry;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum RCText implements TranslatableTextEnum
{
	LOGIC_GATE("Type: %s");
	
	private final String englishText;
	
	RCText(String englishText)
	{
		this.englishText = englishText;
	}
	
	@Override
	public String englishText()
	{
		return englishText;
	}
	
	@Override
	public String getTranslationKey()
	{
		return "text.%s.%s".formatted(RedstoneCircuitry.ID, this.name().toLowerCase());
	}
}
