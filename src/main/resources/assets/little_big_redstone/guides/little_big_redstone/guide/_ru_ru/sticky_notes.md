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
Таким образом, вы можете использовать стикеры, чтобы лучше организовать вашу логику. Стикеры, размещённые внутри микрочипов, можно редактировать, нажав ПКМ.

### Чтение / Редактирование

Чтобы просмотреть текст на стикере, используйте **<KeyBind id="key.use" />**. Открыв стикер, нажмите кнопку «Изменить» для редактирования текста. 
Можно также сразу открыть меню редактирования, нажав **<KeyBind id="key.sneak" />** и **<KeyBind id="key.use" />** на стикере. 
Стикеры сохраняют текст и цвета при ломании и повторной установке.

Строки текста отображаются на стикерах в мире только при наличии записи. 

Стикеры можно натирать сотами, как таблички. Запечатанный стикер больше нельзя редактировать. 
Это можно сделать либо через создание стикера вместе с сотами, либо нажав **<KeyBind id="key.sneak" />** и **<KeyBind id="key.use" />** по размещённому в мире стикеру, держа соты.

### Markdown

Текст на стикерах поддерживает базовые функции Markdown:

\**курсив*\*, \*\***жирный**\*\*, \_\_<Underlined>подчёркнутый</Underlined>\_\_ и \~\~~зачёркнутый~\~\~

Значки логических компонентов можно вводить в стикеры через заполнители. Просто напишите идентификатор нужного логического компонента между знаками «меньше» и «больше». 
Например: \<io\> \<not_gate\> \<and_gate\>

Также можно создавать списки, начав строку с дефиса (\-) или звёздочки (\*) и пробела. В начале строки можно оставить любое количество пробелов, чтобы сделать вложенные пункты. 
Кроме того, после символа списка и пробела можно поставить \[ \] или \[x\], чтобы добавить пустой или отмеченный флажок. Например:

\- Обычный пункт списка.

\- \[ \] Пункт с пустым флажком.

\- \[x\] Пункт с отмеченным флажком.

### Окрашивание

Текст на стикерах можно окрасить, нажав **<KeyBind id="key.sneak" />** и **<KeyBind id="key.use" />**, держа краситель.

Окраску текста также можно убрать со стикера ведром с водой или снежками. 
Учтите: снежки расходуются, ведро с водой — нет.

Текст будет окрашен как в мире, так и в меню просмотра/редактирования.

### Отображение предмета

Предметы можно помещать на стикеры с помощью **<KeyBind id="key.use" />** в мире. Предмет будет отображаться на стикере вместо строк текста. 
Предмет будет подкрашен в цвет текста на стикере, если цвет текста не является цветом по умолчанию для стикера этого цвета.

### Блокнот

Блокнот - это меню, где можно настроить место отображения стикеров из мира на экране. Там же можно закреплять стикеры на экране. Откройте блокнот клавишей «Открыть блокнот» (**<KeyBind id="key.little_big_redstone.open_note_board" />**). 
В блокноте можно выбрать стикеры из инвентаря и закрепить их где угодно на экране. Закреплённые стикеры также можно редактировать через ПКМ. 
Стикеры в блокноте сохраняются отдельно для каждого мира и игрока. При смерти стикеры из блокнота не удаляются.