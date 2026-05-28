package net.swedz.little_big_redstone.guide.tags.block;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.document.block.AlignItems;
import guideme.document.block.LytBlock;
import guideme.document.block.LytBlockContainer;
import guideme.document.block.LytHBox;
import guideme.document.block.LytList;
import guideme.document.block.LytListItem;
import guideme.document.block.LytParagraph;
import guideme.document.flow.LytFlowLink;
import guideme.indices.CategoryIndex;
import guideme.internal.util.NavigationUtil;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import guideme.navigation.NavigationNode;
import guideme.scene.LytItemImage;

import java.util.Comparator;
import java.util.Set;

/**
 * This is essentially {@link guideme.compiler.tags.CategoryIndexCompiler} however it is specifically for the
 * <code>logic</code> category. The main reason this was made because the normal category index compiler does not have
 * an option to display icons. Additionally, the sub pages compiler creates the icons much too large and with no
 * margins so the text is directly next to it which does not look appealing.
 */
public final class LogicIndexTagCompiler extends BlockTagCompiler
{
	private static final Comparator<NavigationNode> ALPHABETICAL_COMPARATOR = Comparator
			.comparing(NavigationNode::title);
	
	@Override
	public Set<String> getTagNames()
	{
		return Set.of("LogicIndex");
	}
	
	@Override
	protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el)
	{
		var categories = compiler.getIndex(CategoryIndex.class).get("logic");
		
		var list = new LytList(false, 0);
		for(var pageAnchor : categories)
		{
			var page = compiler.getPageCollection().getParsedPage(pageAnchor.pageId());
			
			var listItem = new LytListItem();
			var listItemPar = new LytParagraph();
			LytBlock listItemBlock = listItemPar;
			if(page == null)
			{
				listItemPar.appendText("Unknown page id: " + pageAnchor.pageId());
			}
			else
			{
				var link = new LytFlowLink();
				link.setClickCallback((screen) -> screen.navigateTo(pageAnchor));
				link.appendText(page.getFrontmatter().navigationEntry().title());
				listItemPar.append(link);
				
				var itemIcon = NavigationUtil.createNavigationIcon(page);
				if(itemIcon != null)
				{
					var lytHBox = new LytHBox();
					
					var icon = new LytItemImage();
					icon.setItem(itemIcon.create());
					icon.setScale(0.5f);
					icon.setMarginRight(2);
					lytHBox.append(icon);
					lytHBox.append(listItemPar);
					lytHBox.setAlignItems(AlignItems.CENTER);
					listItemBlock = lytHBox;
				}
			}
			listItem.append(listItemBlock);
			list.append(listItem);
		}
		parent.append(list);
	}
}
