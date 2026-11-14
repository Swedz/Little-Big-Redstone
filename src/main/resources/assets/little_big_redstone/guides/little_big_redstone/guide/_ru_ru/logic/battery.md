---
navigation:
  title: "Источник сигнала"
  icon: "battery"
  parent: little_big_redstone:logic.md
  position: 13
categories:
  - logic
item_ids:
  - little_big_redstone:battery
---

# Источник сигнала

<FloatingColumn width="100" align="right">
	### Аналоговый
	Источник выдаёт сигнал заданной силы. Значение сигнала находится в диапазоне от 0 до 15.
</FloatingColumn>

<Row>
	<Column>
		<RecipeFor id="battery" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/battery.snbt" />
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

Источник сигнала — очень простой логический компонент.
Сила его выходного сигнала всегда равна заданному значению уровня сигнала.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="battery" x="0" y="0" type="battery" />
	<Logic name="output" x="32" y="0" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

	<Wire from="battery" fromPort="0" to="output" toPort="0" />
</MicrochipScene>