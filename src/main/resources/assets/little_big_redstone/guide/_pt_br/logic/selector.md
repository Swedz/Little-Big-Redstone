---
navigation:
  title: "Seletor"
  icon: "selector"
  parent: little_big_redstone:logic.md
  position: 20
categories:
  - logic
item_ids:
  - little_big_redstone:selector
---

# Seletor

<RecipeFor id="selector" />

O seletor pode ter de 2 a 10 saídas, e apenas uma das saídas estará LIGADA em um dado momento. Existem
dois modos para o seletor: Contador e Definidor (veja as seções abaixo para mais informações).

### Modo Contador

Quando no modo contador, o seletor terá duas entradas. Quando LIGADA, a entrada superior moverá a saída LIGADA para a porta
acima dela, ou saltará para a porta inferior se a saída LIGADA atual for a superior. A entrada inferior fará o oposto,
o que significa que moverá a saída LIGADA para a porta abaixo dela, ou saltará para a porta superior se a saída LIGADA atual for a
inferior.

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

### Modo Definidor

Quando no modo definidor, o seletor terá tantas entradas quanto saídas. Cada entrada, quando LIGADA, definirá a
saída correspondente para ser a saída LIGADA. Quando múltiplas entradas estão LIGADAS ao mesmo tempo, a saída mais baixa é selecionada.

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
			<Wire from="selector" fromPort="0" to="output" to="0" />
			<Wire from="selector" fromPort="1" to="output" to="1" />
			<Wire from="selector" fromPort="2" to="output" to="2" />
			<Wire from="selector" fromPort="3" to="output" to="3" />
			<Wire from="selector" fromPort="4" to="output" to="4" />
		
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