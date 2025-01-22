package net.swedz.little_big_redstone;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.swedz.tesseract.neoforge.tooltip.BiParser;
import net.swedz.tesseract.neoforge.tooltip.Parsable;
import net.swedz.tesseract.neoforge.tooltip.Parser;
import net.swedz.tesseract.neoforge.tooltip.TextLine;
import net.swedz.tesseract.neoforge.tooltip.TranslatableTextEnum;

import static net.swedz.little_big_redstone.LBRTooltips.*;

public final class LBRTextLine extends TextLine implements Parsable
{
	public static LBRTextLine line(TranslatableTextEnum text, Style style)
	{
		return new LBRTextLine(text, style);
	}
	
	public static LBRTextLine line(TranslatableTextEnum text)
	{
		return new LBRTextLine(text, DEFAULT_STYLE);
	}
	
	public LBRTextLine(TranslatableTextEnum text, Style style)
	{
		super(text, style);
	}
	
	public LBRTextLine(TranslatableTextEnum text)
	{
		super(text);
	}
	
	@Override
	public <T> LBRTextLine arg(T arg, Parser<T> parser)
	{
		super.arg(arg, parser);
		return this;
	}
	
	@Override
	public <A, B> LBRTextLine arg(A a, B b, BiParser<A, B> parser)
	{
		super.arg(a, b, parser);
		return this;
	}
	
	@Override
	public LBRTextLine arg(Object arg)
	{
		return arg instanceof Component c && !c.getStyle().isEmpty() ?
				this.arg(c, Parser.COMPONENT) :
				this.arg(arg, DEFAULT_PARSER);
	}
	
	@Override
	public LBRTextLine withStyle(Style style)
	{
		LBRTextLine line = new LBRTextLine(text, style);
		line.arguments.addAll(arguments);
		return line;
	}
}
