package net.swedz.little_big_redstone.microchip.tag;

import com.google.common.collect.Maps;

import java.util.Map;

public final class TagEmitTicketGroup
{
	private final Map<TagEmitTicket, Integer> tickets = Maps.newConcurrentMap();
	
	private int signal = 0;
	
	public TagEmitTicketGroup()
	{
	}
	
	public int size()
	{
		return tickets.size();
	}
	
	public boolean isEmpty()
	{
		return this.size() == 0;
	}
	
	public void add(TagEmitTicket ticket, int signal)
	{
		tickets.put(ticket, signal);
		
		if(signal > this.signal)
		{
			this.signal = signal;
		}
	}
	
	public void remove(TagEmitTicket ticket)
	{
		tickets.remove(ticket);
		
		int strongestSignal = 0;
		for(int signal : tickets.values())
		{
			if(signal > strongestSignal)
			{
				strongestSignal = signal;
			}
		}
		signal = strongestSignal;
	}
	
	public int getSignal()
	{
		return signal;
	}
}
