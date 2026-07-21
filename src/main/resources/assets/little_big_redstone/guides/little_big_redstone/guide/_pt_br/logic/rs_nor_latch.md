---
navigation:
  title: "Trava RS NOR"
  icon: "rs_nor_latch"
  parent: little_big_redstone:logic.md
  position: 24
categories:
  - logic
item_ids:
  - little_big_redstone:rs_nor_latch
---

# Trava RS NOR

<FloatingColumn width="100" align="right">
	### Tabela-verdade
	<TruthTable inputs="2" outputs="1">
		<TruthState input="0,0" output="0" />
		<TruthState input="0,1" output="0" />
		<TruthState input="1,0" output="1" />
		<TruthState input="1,1" output="0" />
	</TruthTable>
	*Para detalhes sobre tabelas-verdade, veja a página [aqui](introduction.md).*
</FloatingColumn>

<Row>
	<Column>
		<RecipeFor id="rs_nor_latch" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/rs_nor_latch.snbt" />
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

A trava RS NOR, também conhecida como trava NOR de reset-set, é um componente lógico que tem 2 entradas e 1 saída.

A Entrada A, ou o que também é referida como a entrada Set (Definir), quando LIGADA, definirá a saída como LIGADA e ela permanecerá LIGADA mesmo
depois que A for desligada. Quaisquer outras entradas LIGADAS em A não alterarão a saída, desde que a saída ainda esteja LIGADA.

A Entrada B, também conhecida como entrada Reset (Reiniciar), quando LIGADA, definirá a saída como DESLIGADA e ela permanecerá DESLIGADA mesmo depois que B for
desligada - assumindo que A também esteja DESLIGADA. Quaisquer outras entradas LIGADAS em B não alterarão a saída, desde que a
saída ainda esteja DESLIGADA.

Quando A e B estão ambas LIGADAS, o estado de saída é DESLIGADO.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="0" y="8" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="rs_nor_latch" x="32" y="4" type="rs_nor_latch" />
			<Logic name="output" x="64" y="4" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="rs_nor_latch" toPort="0" />
			<Wire from="b" fromPort="0" to="rs_nor_latch" toPort="1" />
			<Wire from="rs_nor_latch" fromPort="0" to="output" toPort="0" />
		
			<RedstoneSignal step="0" direction="east" strength="15" />
			<RedstoneSignal step="1" direction="east" strength="0" />
			<RedstoneSignal step="2" direction="west" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="0" y="8" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="rs_nor_latch" x="32" y="4" type="rs_nor_latch" />
			<Logic name="output" x="64" y="4" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />
		
			<Wire from="a" fromPort="0" to="rs_nor_latch" toPort="0" />
			<Wire from="b" fromPort="0" to="rs_nor_latch" toPort="1" />
			<Wire from="rs_nor_latch" fromPort="0" to="output" toPort="0" />
		
			<RedstoneSignal step="0" direction="east" strength="15" />
			<RedstoneSignal step="1" direction="east" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="0" y="8" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="rs_nor_latch" x="32" y="4" type="rs_nor_latch" />
			<Logic name="output" x="64" y="4" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />
		
			<Wire from="a" fromPort="0" to="rs_nor_latch" toPort="0" />
			<Wire from="b" fromPort="0" to="rs_nor_latch" toPort="1" />
			<Wire from="rs_nor_latch" fromPort="0" to="output" toPort="0" />
		
			<RedstoneSignal step="0" direction="west" strength="15" />
			<RedstoneSignal step="1" direction="west" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red">
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="0" y="8" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="rs_nor_latch" x="32" y="4" type="rs_nor_latch" />
			<Logic name="output" x="64" y="4" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />
		
			<Wire from="a" fromPort="0" to="rs_nor_latch" to="0" />
			<Wire from="b" fromPort="0" to="rs_nor_latch" to="1" />
			<Wire from="rs_nor_latch" fromPort="0" to="output" to="0" />
		
			<RedstoneSignal step="0" direction="east" strength="15" />
			<RedstoneSignal step="0" direction="west" strength="15" />
		</MicrochipScene>
	</Column>
</Row>