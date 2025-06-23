---
navigation:
  title: "Микрочипы"
  icon: "red_microchip"
  position: 1
item_ids:
  - little_big_redstone:white_microchip
  - little_big_redstone:light_gray_microchip
  - little_big_redstone:gray_microchip
  - little_big_redstone:black_microchip
  - little_big_redstone:brown_microchip
  - little_big_redstone:red_microchip
  - little_big_redstone:orange_microchip
  - little_big_redstone:yellow_microchip
  - little_big_redstone:lime_microchip
  - little_big_redstone:green_microchip
  - little_big_redstone:cyan_microchip
  - little_big_redstone:light_blue_microchip
  - little_big_redstone:blue_microchip
  - little_big_redstone:purple_microchip
  - little_big_redstone:magenta_microchip
  - little_big_redstone:pink_microchip
---

# Микрочипы

<FloatingColumn align="right">
	<PaddedBox left="5">
		<RecipeFor id="red_microchip" />
	</PaddedBox>
</FloatingColumn>

<FloatingColumn>
	<PaddedBox left="5" right="-5" bottom="5">
		<GameScene zoom="1.5" padding="0" background="transparent">
			<ImportStructure src="assets/structures/microchips.snbt" />
			<IsometricCamera yaw="135" pitch="30" />
		</GameScene>
	</PaddedBox>
</FloatingColumn>

Микрочипы — это блоки, в которые можно помещать [логику](logic/introduction.md) для создания сложных систем. 
[Провода](redstone_bits.md) можно размещать между логическими компонентами, чтобы сигналы передавались от одного логического компонента к другому.

### Направления

При использовании логических компонентов, которые принимают или выводят сигналы из/в мир, вам нужно будет установить направление, которое они используют. 
Микрочип использует основные направления, то есть <Color color="#4CFF00">север</Color>, <Color color="#0094FF">юг</Color>, <Color color="#FF0000">восток</Color> и <Color color="#FF6A00">запад</Color>, а также <Color color="#FFFFFF">вверх</Color> и <Color color="#FFD800">вниз</Color>. 
Цвет направления можно увидеть на стороне микрочипа, нажав **<KeyBind id="key.sneak" />** и посмотрев на него.

### Окрашивание

Микрочипы можно окрасить любым из 16 стандартных цветов красителя, и меню будет отображать этот цвет. Логические компоненты можно окрашивать отдельно от микрочипа, но по умолчанию они будут наследовать цвет микрочипа, в который они помещены. 
Чтобы окрасить логические компоненты, вы можете либо сделать это в сетке крафта, как обычно, либо щелкнуть правой кнопкой мыши по логике в меню с красителем, чтобы применить его.

Аналогично, вы можете использовать ведро с водой или снежки, чтобы удалить цвет с логического компонента. Обратите внимание, что снежки будут расходоваться, тогда как вёдра с водой — нет.