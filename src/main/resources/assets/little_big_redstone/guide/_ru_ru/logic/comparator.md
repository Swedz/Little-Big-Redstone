---
navigation:
  title: "Компаратор"
  icon: "comparator"
  parent: little_big_redstone:logic.md
  position: 23
categories:
  - logic
item_ids:
  - little_big_redstone:comparator
---

# Компаратор

<FloatingColumn width="100" align="right">
	### Аналоговый
    Компараторы выдают выходной сигнал той же силы, что и сравниваемое значение. 
    Это может быть либо постоянное значение, заданное в настройках, либо значение первого входа, если компаратор установлен в сквозной режим.
</FloatingColumn>

<RecipeFor id="comparator" />

Компаратор - это логический компонент, который позволяет сравнивать силу сигнала от 1 до 10 входов (B₁-B₁₀) с другой силой сигнала (A). 
Настройка режима компаратора определяет, должны ли все входы или хотя бы один вход соответствовать сравнению, чтобы выход был включён. 
Когда выход включён, сила выходного сигнала будет равна силе сигнала входа A.

Компаратор можно настроить так, чтобы у него был входной порт для входа A, установив силу сигнала в сквозной режим. 
Также его можно настроить на использование постоянного сигнала для сравнения. В этом случае у него не будет входного порта для входа A.

Как и другие логические компоненты, которые сравнивают силу сигнала, компаратор можно настроить так, 
чтобы значения должны были быть больше или равны входу A, равны ему либо меньше или равны ему.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="4" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="16" y="12" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="comparator" x="48" y="8" type="comparator" />
			<Logic name="output" x="80" y="8" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="comparator" toPort="0" />
			<Wire from="b" fromPort="0" to="comparator" toPort="1" />
			<Wire from="comparator" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="east" strength="0" />
			<RedstoneSignal step="0" direction="west" strength="0" />

			<RedstoneSignal step="1" direction="east" strength="0" />
			<RedstoneSignal step="1" direction="west" strength="15" />

			<RedstoneSignal step="2" direction="east" strength="15" />
			<RedstoneSignal step="2" direction="west" strength="0" />

			<RedstoneSignal step="3" direction="east" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="15" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="comparator" x="32" y="0" type="comparator" data="{config:{signal_strength:15}}" />
			<Logic name="output" x="64" y="0" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="comparator" toPort="0" />
			<Wire from="comparator" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="east" strength="0" />

			<RedstoneSignal step="1" direction="east" strength="10" />

			<RedstoneSignal step="2" direction="east" strength="15" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="-5" type="io" data="{config:{direction:'north'}}" hide={true} />
			<Logic name="b" x="0" y="0" type="io" data="{config:{direction:'south'}}" hide={true} />
			<Logic name="c" x="0" y="5" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="comparator" x="32" y="0" type="comparator" data="{config:{signal_strength:15,inputs:3}}" />
			<Logic name="output" x="64" y="0" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="comparator" toPort="0" />
			<Wire from="b" fromPort="0" to="comparator" toPort="1" />
			<Wire from="c" fromPort="0" to="comparator" toPort="2" />
			<Wire from="comparator" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="north" strength="10" />
			<RedstoneSignal step="0" direction="south" strength="0" />
			<RedstoneSignal step="0" direction="east" strength="0" />

			<RedstoneSignal step="1" direction="north" strength="10" />
			<RedstoneSignal step="1" direction="south" strength="0" />
			<RedstoneSignal step="1" direction="east" strength="10" />

			<RedstoneSignal step="2" direction="north" strength="10" />
			<RedstoneSignal step="2" direction="south" strength="15" />
			<RedstoneSignal step="2" direction="east" strength="15" />

			<RedstoneSignal step="3" direction="north" strength="15" />
			<RedstoneSignal step="3" direction="south" strength="15" />
			<RedstoneSignal step="3" direction="east" strength="15" />
		</MicrochipScene>
	</Column>
</Row>