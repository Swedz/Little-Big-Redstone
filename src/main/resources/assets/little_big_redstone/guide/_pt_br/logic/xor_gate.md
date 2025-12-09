---
navigation:
  title: "Porta XOR"
  icon: "xor_gate"
  parent: little_big_redstone:logic.md
  position: 16
categories:
  - logic
item_ids:
  - little_big_redstone:xor_gate
---

# Porta XOR

<FloatingColumn width="100" align="right">
	### Tabela-verdade
	<TruthTable inputs="2" outputs="1">
		<TruthState input="0,0" output="0" />
		<TruthState input="0,1" output="1" />
		<TruthState input="1,0" output="1" />
		<TruthState input="1,1" output="0" />
	</TruthTable>
	*Para detalhes sobre tabelas-verdade, veja a página [aqui](introduction.md).*
</FloatingColumn>

<Row>
	<Column>
		<RecipeFor id="xor_gate" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/xor_gate.snbt" />
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

Portas XOR são componentes lógicos que só têm uma saída LIGADA quando um número ímpar de entradas estão LIGADAS. No caso de uma
Porta XOR simples de duas entradas, isso significa que a saída só estará LIGADA quando exatamente uma das entradas estiver LIGADA.

Portas XOR podem ser configuradas para ter entre 2 e 10 entradas.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="12" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="b" x="0" y="20" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="and" x="32" y="16" type="xor_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="and" toPort="0" />
			<Wire from="b" fromPort="0" to="and" toPort="1" />
			<Wire from="and" fromPort="0" to="output" toPort="0" />

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
			<Logic name="a" x="0" y="12" type="io" data="{config:{direction:'north'}}" hide={true} />
			<Logic name="b" x="0" y="20" type="io" data="{config:{direction:'south'}}" hide={true} />
			<Logic name="c" x="0" y="28" type="io" data="{config:{direction:'east'}}" hide={true} />
			<Logic name="d" x="0" y="36" type="io" data="{config:{direction:'west'}}" hide={true} />
			<Logic name="and" x="32" y="16" type="xor_gate" data="{config:{input_count:4}}" />
			<Logic name="output" x="64" y="24" type="io" data="{config:{direction:'up',input:false,signal_strength:15}}" hide={true} />

			<Wire from="a" fromPort="0" to="and" toPort="0" />
			<Wire from="b" fromPort="0" to="and" toPort="1" />
			<Wire from="c" fromPort="0" to="and" toPort="2" />
			<Wire from="d" fromPort="0" to="and" toPort="3" />
			<Wire from="and" fromPort="0" to="output" toPort="0" />

			<RedstoneSignal step="0" direction="north" strength="0" />
			<RedstoneSignal step="0" direction="south" strength="0" />
			<RedstoneSignal step="0" direction="east" strength="0" />
			<RedstoneSignal step="0" direction="west" strength="0" />

			<RedstoneSignal step="1" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="south" strength="0" />
			<RedstoneSignal step="1" direction="east" strength="0" />
			<RedstoneSignal step="1" direction="west" strength="0" />

			<RedstoneSignal step="2" direction="north" strength="0" />
			<RedstoneSignal step="2" direction="south" strength="15" />
			<RedstoneSignal step="2" direction="east" strength="0" />
			<RedstoneSignal step="2" direction="west" strength="0" />

			<RedstoneSignal step="3" direction="north" strength="0" />
			<RedstoneSignal step="3" direction="south" strength="0" />
			<RedstoneSignal step="3" direction="east" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="0" />

			<RedstoneSignal step="4" direction="north" strength="0" />
			<RedstoneSignal step="4" direction="south" strength="0" />
			<RedstoneSignal step="4" direction="east" strength="0" />
			<RedstoneSignal step="4" direction="west" strength="15" />

			<RedstoneSignal step="5" direction="north" strength="0" />
			<RedstoneSignal step="5" direction="south" strength="15" />
			<RedstoneSignal step="5" direction="east" strength="0" />
			<RedstoneSignal step="5" direction="west" strength="15" />

			<RedstoneSignal step="6" direction="north" strength="15" />
			<RedstoneSignal step="6" direction="south" strength="0" />
			<RedstoneSignal step="6" direction="east" strength="15" />
			<RedstoneSignal step="6" direction="west" strength="0" />

			<RedstoneSignal step="7" direction="north" strength="15" />
			<RedstoneSignal step="7" direction="south" strength="15" />
			<RedstoneSignal step="7" direction="east" strength="15" />
			<RedstoneSignal step="7" direction="west" strength="15" />
		</MicrochipScene>
	</Column>
</Row>