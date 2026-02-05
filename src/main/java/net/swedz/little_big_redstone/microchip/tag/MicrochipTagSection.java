package net.swedz.little_big_redstone.microchip.tag;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.Map;
import java.util.Set;

final class MicrochipTagSection
{
	private final Map<TagOwnerKey, Set<TagEmitTicket>> tickets = Maps.newConcurrentMap();
	
	public MicrochipTagSection()
	{
	}
	
	public boolean contains(TagOwnerKey owner, int threshold)
	{
		var activeTickets = tickets.get(owner);
		return activeTickets != null && activeTickets.size() >= threshold;
	}
	
	private void add(TagOwnerKey owner, TagEmitTicket ticket)
	{
		tickets.computeIfAbsent(owner, (__) -> Sets.newConcurrentHashSet()).add(ticket);
	}
	
	private void remove(TagOwnerKey owner, TagEmitTicket ticket)
	{
		var activeTickets = tickets.get(owner);
		if(activeTickets != null)
		{
			activeTickets.remove(ticket);
			if(activeTickets.isEmpty())
			{
				tickets.remove(owner);
			}
		}
	}
	
	public void add(TagOwnerKey owner, WorldPos pos)
	{
		var ticket = new TagEmitTicket(pos);
		this.add(owner, ticket);
		if(!owner.equals(TagOwnerKey.GLOBAL))
		{
			this.add(TagOwnerKey.GLOBAL, ticket);
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
