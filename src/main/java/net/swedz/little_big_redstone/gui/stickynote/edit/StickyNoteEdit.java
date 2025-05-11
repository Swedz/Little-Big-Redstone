package net.swedz.little_big_redstone.gui.stickynote.edit;

public final class StickyNoteEdit
{
	private String text;
	
	public StickyNoteEdit(String text)
	{
		this.text = text;
	}
	
	public String text()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		this.markDirty();
	}
	
	public void markDirty()
	{
	
	}
}
