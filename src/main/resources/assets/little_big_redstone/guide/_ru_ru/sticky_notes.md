---
navigation:
  title: "Стикеры"
  icon: "red_sticky_note"
  position: 5
item_ids:
  - little_big_redstone:white_sticky_note
  - little_big_redstone:light_gray_sticky_note
  - little_big_redstone:gray_sticky_note
  - little_big_redstone:black_sticky_note
  - little_big_redstone:brown_sticky_note
  - little_big_redstone:red_sticky_note
  - little_big_redstone:orange_sticky_note
  - little_big_redstone:yellow_sticky_note
  - little_big_redstone:lime_sticky_note
  - little_big_redstone:green_sticky_note
  - little_big_redstone:cyan_sticky_note
  - little_big_redstone:light_blue_sticky_note
  - little_big_redstone:blue_sticky_note
  - little_big_redstone:purple_sticky_note
  - little_big_redstone:magenta_sticky_note
  - little_big_redstone:pink_sticky_note
---

# Стикеры

<FloatingColumn align="right">
	<PaddedBox left="5">
		<RecipeFor id="red_sticky_note" />
	</PaddedBox>
</FloatingColumn>

<FloatingColumn>
	<PaddedBox left="5" right="10" bottom="5">
		<Row gap="1">
			<ItemImage id="red_sticky_note" />
			<ItemImage id="orange_sticky_note" />
			<ItemImage id="yellow_sticky_note" />
			<ItemImage id="lime_sticky_note" />
		</Row>
		<Row gap="1">
			<ItemImage id="green_sticky_note" />
			<ItemImage id="cyan_sticky_note" />
			<ItemImage id="light_blue_sticky_note" />
			<ItemImage id="blue_sticky_note" />
		</Row>
		<Row gap="1">
			<ItemImage id="purple_sticky_note" />
			<ItemImage id="magenta_sticky_note" />
			<ItemImage id="pink_sticky_note" />
			<ItemImage id="brown_sticky_note" />
		</Row>
		<Row gap="1">
			<ItemImage id="white_sticky_note" />
			<ItemImage id="light_gray_sticky_note" />
			<ItemImage id="gray_sticky_note" />
			<ItemImage id="black_sticky_note" />
		</Row>

		<PaddedBox left="2" top="10">
			<GameScene zoom="3.3" padding="0" background="transparent">
				<Entity id="little_big_redstone:sticky_note" x="0.75" y="0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:' ',TextColor:15,Color:14,Facing:0,AttachedFace:2,Quadrant:0}" />
				<Entity id="little_big_redstone:sticky_note" x="0.25" y="0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:1,Facing:0,AttachedFace:2,Quadrant:1}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.75" y="0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:4,Facing:0,AttachedFace:2,Quadrant:0}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.25" y="0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:5,Facing:0,AttachedFace:2,Quadrant:1}" />

				<Entity id="little_big_redstone:sticky_note" x="0.75" y="0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:13,Facing:0,AttachedFace:2,Quadrant:2}" />
				<Entity id="little_big_redstone:sticky_note" x="0.25" y="0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:' ',TextColor:15,Color:9,Facing:0,AttachedFace:2,Quadrant:3}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.75" y="0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:3,Facing:0,AttachedFace:2,Quadrant:2}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.25" y="0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:11,Facing:0,AttachedFace:2,Quadrant:3}" />
	
				<Entity id="little_big_redstone:sticky_note" x="0.75" y="-0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:10,Facing:0,AttachedFace:2,Quadrant:0}" />
				<Entity id="little_big_redstone:sticky_note" x="0.25" y="-0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:2,Facing:0,AttachedFace:2,Quadrant:1}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.75" y="-0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:' ',TextColor:15,Color:6,Facing:0,AttachedFace:2,Quadrant:0}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.25" y="-0.75" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:12,Facing:0,AttachedFace:2,Quadrant:1}" />
	
				<Entity id="little_big_redstone:sticky_note" x="0.75" y="-0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:0,Facing:0,AttachedFace:2,Quadrant:2}" />
				<Entity id="little_big_redstone:sticky_note" x="0.25" y="-0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:8,Facing:0,AttachedFace:2,Quadrant:3}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.75" y="-0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:'',Color:7,Facing:0,AttachedFace:2,Quadrant:2}" />
				<Entity id="little_big_redstone:sticky_note" x="-0.25" y="-0.25" z="0.96875" rotationY="180" rotationX="0" data="{StickyNote:' ',TextColor:0,Color:15,Facing:0,AttachedFace:2,Quadrant:3}" />
	
				<IsometricCamera yaw="180" pitch="0" />
			</GameScene>
		</PaddedBox>
	</PaddedBox>
</FloatingColumn>

Стикеры размещаются в мире, как рамки для предметов. На каждой грани блока можно поместить четыре стикера. 
Доступны любые сочетания цветов и поворотов.

Также стикеры можно размещать внутри [Микрочипов](microchips.md) таким же образом, как и [Логику](logic/introduction.md).
Таким образом, вы можете использовать стикеры, чтобы лучше организовать вашу логику.

### Чтение / Редактирование

Чтобы просмотреть текст на стикере, используйте **<KeyBind id="key.use" />**. Открыв стикер, нажмите кнопку «Изменить» для редактирования текста. 
Можно также сразу открыть меню редактирования, нажав **<KeyBind id="key.sneak" />** и **<KeyBind id="key.use" />** на стикере. 
Стикеры сохраняют текст и цвета при ломании и повторной установке.

Строки текста отображаются на стикерах в мире только при наличии записи.

### Markdown

Текст на стикерах поддерживает базовые функции Markdown:

\**курсив*\*, \*\***жирный**\*\*, \_\_<Underlined>подчёркнутый</Underlined>\_\_, и \~\~~зачёркнутый~\~\~

### Окрашивание

Текст на стикерах можно окрасить. Для этого нажмите **<KeyBind id="key.use" />**, держа краситель.

Цвет также можно удалить с логического компонента. Используйте для этого ведро с водой или снежки. 
Учтите: снежки расходуются, ведро с водой — нет.

Текст будет окрашен как в мире, так и в меню просмотра/редактирования.
