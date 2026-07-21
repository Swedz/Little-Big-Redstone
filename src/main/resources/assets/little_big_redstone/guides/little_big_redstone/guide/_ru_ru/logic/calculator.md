---
navigation:
  title: "Калькулятор"
  icon: "calculator"
  parent: little_big_redstone:logic.md
  position: 24
categories:
  - logic
item_ids:
  - little_big_redstone:calculator
---

# Калькулятор

<FloatingColumn width="100" align="right">
	### Аналоговый
	Выходной сигнал калькулятора равен итоговому рассчитанному значению всех входов. 
    Сигнал не выходит за пределы диапазона от 0 до 15.
</FloatingColumn>

<RecipeFor id="calculator" />

Калькулятор может принимать от 2 до 10 входов. 
Входные сигналы складываются или вычитаются в зависимости от выбранного режима. 
Выходной сигнал равен итоговому результату вычисления.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="4" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="16" y="12" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="calculator" x="48" y="8" type="calculator" />
			<Logic name="output" x="80" y="8" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="calculator" toPort="0" />
			<Wire from="b" fromPort="0" to="calculator" toPort="1" />
			<Wire from="calculator" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="east" strength="0" />
			<RedstoneSignal step="0" direction="west" strength="0" />

			<RedstoneSignal step="1" direction="east" strength="1" />
			<RedstoneSignal step="1" direction="west" strength="1" />

			<RedstoneSignal step="2" direction="east" strength="0" />
			<RedstoneSignal step="2" direction="west" strength="0" />

			<RedstoneSignal step="3" direction="east" strength="10" />
			<RedstoneSignal step="3" direction="west" strength="5" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="4" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="16" y="12" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="calculator" x="48" y="8" type="calculator" data="{config:{mode:'subtraction'}}" />
			<Logic name="output" x="80" y="8" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="calculator" toPort="0" />
			<Wire from="b" fromPort="0" to="calculator" toPort="1" />
			<Wire from="calculator" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="east" strength="0" />
			<RedstoneSignal step="0" direction="west" strength="0" />

			<RedstoneSignal step="1" direction="east" strength="2" />
			<RedstoneSignal step="1" direction="west" strength="1" />

			<RedstoneSignal step="2" direction="east" strength="0" />
			<RedstoneSignal step="2" direction="west" strength="0" />

			<RedstoneSignal step="3" direction="east" strength="10" />
			<RedstoneSignal step="3" direction="west" strength="5" />
		</MicrochipScene>
	</Column>
</Row>

Обратите внимание, что порядок входов учитывается. Это значит, что в режиме вычитания, если сначала подать меньшее значение, а затем большее, на выходе будет значение 0. 
Это происходит потому, что сигнал не может принимать отрицательное значение. 
Например, если у калькулятора в режиме вычитания вход A равен 5, а вход B равен 10, рассчитанное значение будет -5. 
Однако выходной сигнал будет равен 0, то есть будет выключен.