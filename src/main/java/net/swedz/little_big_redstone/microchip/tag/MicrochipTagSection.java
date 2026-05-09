package net.swedz.little_big_redstone.microchip.tag;

import com.google.common.collect.Maps;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.Map;

final class MicrochipTagSection
{
	private final Map<TagOwnerKey, TagEmitTicketGroup> tickets = Maps.newConcurrentMap();
	
	public MicrochipTagSection()
	{
	}
	
	public int getSignal(TagOwnerKey owner, int threshold)
	{
		var group = tickets.get(owner);
		return group != null && group.size() >= threshold ? group.getSignal() : 0;
	}
	
	private void add(TagOwnerKey owner, TagEmitTicket ticket, int signal)
	{
		tickets.computeIfAbsent(owner, (__) -> new TagEmitTicketGroup()).add(ticket, signal);
	}
	
	private void remove(TagOwnerKey owner, TagEmitTicket ticket)
	{
		var group = tickets.get(owner);
		if(group != null)
		{
			group.remove(ticket);
			if(group.isEmpty())
			{
				tickets.remove(owner);
			}
		}
	}
	
	public void add(TagOwnerKey owner, WorldPos pos, int signal)
	{
		var ticket = new TagEmitTicket(pos);
		this.add(owner, ticket, signal);
		if(!owner.equals(TagOwnerKey.GLOBAL))
		{
			this.add(TagOwnerKey.GLOBAL, ticket, signal);
		}
	}
	
	public void remove(TagOwnerKey owner, WorldPos pos)
	{
		var ticket = new TagEmitTicket(pos);
		this.remove(owner, ticket);
		if(!owner.equals(TagOwnerKey.GLOBAL))
		{
			this.remove(TagOwnerKey.GLOBAL, ticket);
		}
	}
	
	public boolean isEmpty()
	{
		return tickets.isEmpty();
	}
}
