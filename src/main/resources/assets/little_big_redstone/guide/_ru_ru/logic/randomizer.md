---
navigation:
  title: "Рандомизатор"
  icon: "randomizer"
  parent: little_big_redstone:logic.md
  position: 21
categories:
  - logic
item_ids:
  - little_big_redstone:randomizer
---

# Рандомизатор

<RecipeFor id="randomizer" />

Рандомизатор может иметь от 1 до 10 выходов, но в каждый момент времени включён может быть только один.  
Когда входной сигнал ВКЛ, каждое тиковое обновление случайный выход будет включаться с заданной вероятностью.  
По умолчанию шанс равен 100 %, но вы можете изменить его на любое значение. Например, при 50 % шансах  
в половине тиков сигнал ВКЛ приведёт к включению случайного выхода. Каждый выход имеет равные шансы.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="throttler" x="32" y="8" type="pulse_throttler" hide={true} />
			<Logic name="randomizer" x="64" y="0" type="randomizer" data="{config:{outputs:5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="input" fromPort="0" to="throttler" toPort="0" />
			<Wire from="throttler" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="throttler" x="32" y="8" type="pulse_throttler" hide={true} />
			<Logic name="randomizer" x="64" y="0" type="randomizer" data="{config:{outputs:5,chance:0.5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="input" fromPort="0" to="throttler" toPort="0" />
			<Wire from="throttler" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>

Приведённые примеры имеют вход, сокращённый до одного тика. Если этого не делать, каждый тик выбор выхода будет разным. 
Ниже показан пример без укорочения:

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="randomizer" x="32" y="0" type="randomizer" data="{config:{outputs:5}}" />
			<Logic name="output" x="64" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="input" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="randomizer" x="32" y="0" type="randomizer" data="{config:{outputs:5,chance:0.5}}" />
			<Logic name="output" x="64" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="input" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>