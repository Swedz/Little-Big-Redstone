package net.swedz.little_big_redstone.microchip.object.logic.reader;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.swedz.tesseract.api.Assert;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;

public record LogicReaderThreshold(
		String value,
		Float percentage,
		Long number
)
{
	public static int MAX_LENGTH = 19;
	
	private static final Pattern PATTERN = Pattern.compile("^(?<number>\\d*(\\.\\d{0,2})?)%?$");
	
	public static final Codec<LogicReaderThreshold> CODEC = Codec.withAlternative(
			Codec.STRING.xmap(
					LogicReaderThreshold::from,
					LogicReaderThreshold::value
			),
			Codec.FLOAT.xmap(
					(percentage) -> new LogicReaderThreshold(
							String.format("%.2f%%", percentage * 100f),
							percentage,
							null
					),
					LogicReaderThreshold::percentage
			)
	);
	
	public static final StreamCodec<ByteBuf, LogicReaderThreshold> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
			LogicReaderThreshold::from,
			LogicReaderThreshold::value
	);
	
	public static final LogicReaderThreshold DEFAULT = from("50%");
	
	public static boolean isValid(String value)
	{
		return PATTERN.matcher(value).find();
	}
	
	public static LogicReaderThreshold from(String value)
	{
		var matcher = PATTERN.matcher(value);
		if(matcher.find())
		{
			var matched = matcher.group("number");
			if(value.endsWith("%"))
			{
				float percentage = 0;
				try
				{
					percentage = Float.parseFloat(matched) / 100f;
					if(percentage > 1)
					{
						percentage = 1;
						value = "100%";
					}
				}
				catch(NumberFormatException ignored)
				{
					value = "0%";
				}
				return new LogicReaderThreshold(value, percentage, null);
			}
			else
			{
				long number = 0;
				try
				{
					number = Long.parseLong(matched.replaceAll("\\..*", ""));
				}
				catch(NumberFormatException ignored)
				{
					value = "0";
				}
				return new LogicReaderThreshold(value, null, number);
			}
		}
		throw new InvalidParameterException("LogicReaderThreshold value \"" + value + "\" does not match pattern");
	}
	
	public LogicReaderThreshold
	{
		Assert.that(isValid(value));
	}
	
	public boolean isPercentage()
	{
		return percentage != null;
	}
	
	public boolean isNumber()
	{
		return number != null;
	}
}
