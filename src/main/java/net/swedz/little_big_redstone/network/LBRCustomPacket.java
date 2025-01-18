package net.swedz.little_big_redstone.network;

import net.swedz.tesseract.neoforge.packet.CustomPacket;

public interface LBRCustomPacket extends CustomPacket
{
	@Override
	default Type<LBRCustomPacket> type()
	{
		return LBRPackets.getType(this.getClass());
	}
}
