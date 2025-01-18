package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum LBRText implements TranslatableTextEnum
{
	LOGIC_GATE_ALGEBRA("Q = %s"),
	LOGIC_GATE_ALGEBRA_AND("A \u2227 B"),
	LOGIC_GATE_ALGEBRA_NAND("A \u2191 B"),
	LOGIC_GATE_ALGEBRA_NOR("A \u2193 B"),
	LOGIC_GATE_ALGEBRA_NOT("\u00ACA"),
	LOGIC_GATE_ALGEBRA_OR("A \u2228 B"),
	LOGIC_GATE_ALGEBRA_XOR("A \u22BB B");
	
	private final String englishText;
	
	LBRText(String englishText)
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
		return "text.%s.%s".formatted(LBR.ID, this.name().toLowerCase());
	}
}
