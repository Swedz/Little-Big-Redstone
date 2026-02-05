---
navigation:
  title: "Ограничитель импульсов"
  icon: "pulse_throttler"
  parent: little_big_redstone:logic.md
  position: 20
categories:
  - logic
item_ids:
  - little_big_redstone:pulse_throttler
---

# Ограничитель импульсов

<Row>
	<Column>
		<RecipeFor id="pulse_throttler" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/pulse_throttler.snbt" />
			<BlockAnnotation x="2" y="0" z="0" color="#FFFFFF">
				Ограничитель импульсов гораздо гибче стандартного укорочителя импульсов, но по концепции схож.
			</BlockAnnotation>
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

> **Примечание:** В Minecraft 20 тиков соответствует одной секунде реального времени.
>
Ограничитель импульсов — это логический компонент, который позволяет задать длительность сигнала ВКЛ. 
По умолчанию ограничитель преобразует любой входящий сигнал в одиночный импульс длительностью в один тик.

<PaddedBox left="5" top="5">
	<Row>
		<Column>
			<MicrochipScene color="red" includeToolbar={true} padding="0">
				<Logic name="input" x="0" y="0" type="io" hide={true} />
				<Logic name="pulse_throttler" x="32" y="0" type="pulse_throttler" />
				<Logic name="output" x="64" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

				<Wire from="input" fromPort="0" to="pulse_throttler" toPort="0" />
				<Wire from="pulse_throttler" fromPort="0" to="output" toPort="0" />

				<RedstoneSignal step="0" direction="north" strength="15" />
				<RedstoneSignal step="1" direction="north" strength="0" />
			</MicrochipScene>
		</Column>

		<Column>
            <MicrochipScene color="red" includeToolbar={true} padding="0">
                <Logic name="input" x="-26" y="0" type="io" hide={true} />
                <Logic name="pulse_throttler" x="6" y="0" type="pulse_throttler" data="{config:{duration:10}}" />
                <Logic name="output" x="38" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

                <Wire from="input" fromPort="0" to="pulse_throttler" toPort="0" />
                <Wire from="pulse_throttler" fromPort="0" to="output" toPort="0" />

                <RedstoneSignal step="0" direction="north" strength="15" />
                <RedstoneSignal step="1" direction="north" strength="0" />
            </MicrochipScene>
        </Column>

        <Column>
            <MicrochipScene color="red" includeToolbar={true} padding="0">
                <Logic name="input" x="-26" y="0" type="io" hide={true} />
                <Logic name="pulse_throttler" x="6" y="0" type="pulse_throttler" data="{config:{duration:40}}" />
                <Logic name="output" x="38" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

                <Wire from="input" fromPort="0" to="pulse_throttler" toPort="0" />
                <Wire from="pulse_throttler" fromPort="0" to="output" toPort="0" />

                <RedstoneSignal step="0" direction="north" strength="15" />
                <RedstoneSignal step="1" direction="north" strength="0" />
            </MicrochipScene>
        </Column>
	</Row>
</PaddedBox>