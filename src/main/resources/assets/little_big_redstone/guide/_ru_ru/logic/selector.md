---
navigation:
  title: "Селектор"
  icon: "selector"
  parent: little_big_redstone:logic.md
  position: 21
categories:
  - logic
item_ids:
  - little_big_redstone:selector
---

# Селектор

<RecipeFor id="selector" />

Селектор может иметь от 2 до 10 выходов, и только один из выходов будет включён в любой момент времени. Существует два режима работы селектора: Счётчик и Установщик (подробнее смотрите в разделах ниже).

### Режим "Счётчик"

В режиме "Счётчик" селектор будет иметь два входа. При включении верхний вход переместит включённый выход на порт выше него, или перейдёт на самый нижний порт, если текущий включённый выход является верхним. 
Нижний вход будет делать противоположное, то есть перемещать включённый выход на порт ниже него, или переходить на самый верхний порт, если текущий включённый выход является нижним.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="up" x="0" y="0" type="io" data="{config:{direction:'up'}}" hide={true} />
			<Logic name="up_throttle" x="32" y="0" type="pulse_throttler" hide={true} />
			<Logic name="down" x="0" y="16" type="io" data="{config:{direction:'down'}}" hide={true} />
			<Logic name="down_throttle" x="32" y="16" type="pulse_throttler" hide={true} />
			<Logic name="selector" x="64" y="0" type="selector" data="{config:{outputs:5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="up" fromPort="0" to="up_throttle" toPort="0" />
			<Wire from="up_throttle" fromPort="0" to="selector" toPort="0" />
			<Wire from="down" fromPort="0" to="down_throttle" toPort="0" />
			<Wire from="down_throttle" fromPort="0" to="selector" toPort="1" />
			<Wire from="selector" fromPort="0" to="output" toPort="0" />
			<Wire from="selector" fromPort="1" to="output" toPort="1" />
			<Wire from="selector" fromPort="2" to="output" toPort="2" />
			<Wire from="selector" fromPort="3" to="output" toPort="3" />
			<Wire from="selector" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="up" strength="15" />
			<RedstoneSignal step="1" direction="up" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="up" x="0" y="0" type="io" data="{config:{direction:'up'}}" hide={true} />
			<Logic name="up_throttle" x="32" y="0" type="pulse_throttler" hide={true} />
			<Logic name="down" x="0" y="16" type="io" data="{config:{direction:'down'}}" hide={true} />
			<Logic name="down_throttle" x="32" y="16" type="pulse_throttler" hide={true} />
			<Logic name="selector" x="64" y="0" type="selector" data="{config:{outputs:5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />
		
			<Wire from="up" fromPort="0" to="up_throttle" toPort="0" />
			<Wire from="up_throttle" fromPort="0" to="selector" toPort="0" />
			<Wire from="down" fromPort="0" to="down_throttle" toPort="0" />
			<Wire from="down_throttle" fromPort="0" to="selector" toPort="1" />
			<Wire from="selector" fromPort="0" to="output" toPort="0" />
			<Wire from="selector" fromPort="1" to="output" toPort="1" />
			<Wire from="selector" fromPort="2" to="output" toPort="2" />
			<Wire from="selector" fromPort="3" to="output" toPort="3" />
			<Wire from="selector" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="down" strength="15" />
			<RedstoneSignal step="1" direction="down" strength="0" />
		</MicrochipScene>
	</Column>
</Row>

### Режим "Установщик"

В режиме "Установщик" селектор будет иметь столько же входов, сколько и выходов. 
Каждый вход, будучи включённым, сделает соответствующий выход включённым. 
Если несколько входов включены одновременно, выбирается самый нижний выход.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="north" x="0" y="0" type="io" data="{config:{direction:'north'}}" hide={true} />
			<Logic name="south" x="0" y="6" type="io" data="{config:{direction:'south'}}" hide={true} />
			<Logic name="east" x="0" y="12" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="west" x="0" y="18" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="up" x="0" y="24" type="io" data="{config:{direction:'up'}}" hide={true} />
			<Logic name="selector" x="32" y="4" type="selector" data="{config:{outputs:5,mode:'setter'}}" />
			<Logic name="output" x="64" y="4" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="north" fromPort="0" to="selector" toPort="0" />
			<Wire from="south" fromPort="0" to="selector" toPort="1" />
			<Wire from="east" fromPort="0" to="selector" toPort="2" />
			<Wire from="west" fromPort="0" to="selector" toPort="3" />
			<Wire from="up" fromPort="0" to="selector" toPort="4" />
			<Wire from="selector" fromPort="0" to="output" toPort="0" />
			<Wire from="selector" fromPort="1" to="output" toPort="1" />
			<Wire from="selector" fromPort="2" to="output" toPort="2" />
			<Wire from="selector" fromPort="3" to="output" toPort="3" />
			<Wire from="selector" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="south" strength="15" />
			<RedstoneSignal step="2" direction="east" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="15" />
			<RedstoneSignal step="4" direction="up" strength="15" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="north" x="0" y="0" type="io" data="{config:{direction:'north'}}" hide={true} />
			<Logic name="south" x="0" y="6" type="io" data="{config:{direction:'south'}}" hide={true} />
			<Logic name="east" x="0" y="12" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="west" x="0" y="18" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="up" x="0" y="24" type="io" data="{config:{direction:'up'}}" hide={true} />
			<Logic name="selector" x="32" y="4" type="selector" data="{config:{outputs:5,mode:'setter'}}" />
			<Logic name="output" x="64" y="4" type="or_gate" data="{config:{input_count:5}}" hide={true} />
		
			<Wire from="north" fromPort="0" to="selector" toPort="0" />
			<Wire from="south" fromPort="0" to="selector" toPort="1" />
			<Wire from="east" fromPort="0" to="selector" toPort="2" />
			<Wire from="west" fromPort="0" to="selector" toPort="3" />
			<Wire from="up" fromPort="0" to="selector" toPort="4" />
			<Wire from="selector" fromPort="0" to="output" toPort="0" />
			<Wire from="selector" fromPort="1" to="output" toPort="1" />
			<Wire from="selector" fromPort="2" to="output" toPort="2" />
			<Wire from="selector" fromPort="3" to="output" toPort="3" />
			<Wire from="selector" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="0" direction="south" strength="15" />
			<RedstoneSignal step="1" direction="south" strength="15" />
			<RedstoneSignal step="1" direction="east" strength="15" />
			<RedstoneSignal step="2" direction="east" strength="15" />
			<RedstoneSignal step="2" direction="west" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="15" />
			<RedstoneSignal step="3" direction="up" strength="15" />
			<RedstoneSignal step="4" direction="up" strength="15" />
			<RedstoneSignal step="4" direction="north" strength="15" />
		</MicrochipScene>
	</Column>
</Row>