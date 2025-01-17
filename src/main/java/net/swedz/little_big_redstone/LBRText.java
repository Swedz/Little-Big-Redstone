package net.swedz.little_big_redstone;

import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

public enum LBRText implements TranslatableTextEnum
{
	LOGIC_GATE_ALGEBRA("Q = %s"),
	LOGIC_GATE_ALGEBRA_AND("A \u2227 B \u2227 C"),
	LOGIC_GATE_ALGEBRA_NAND("A \u2191 B \u2191 C"),
	LOGIC_GATE_ALGEBRA_NOR("A \u2193 B \u2193 C"),
	LOGIC_GATE_ALGEBRA_NOT("\u00ACA"),
	LOGIC_GATE_ALGEBRA_OR("A \u2228 B \u2228 C"),
	LOGIC_GATE_ALGEBRA_XOR("A \u22BB B \u22BB C");
	
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
