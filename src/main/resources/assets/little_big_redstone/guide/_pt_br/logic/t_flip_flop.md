---
navigation:
  title: "Flip-Flop T"
  icon: "t_flip_flop"
  parent: little_big_redstone:logic.md
  position: 23
categories:
  - logic
item_ids:
  - little_big_redstone:t_flip_flop
---

# Flip-Flop T

<Row>
	<Column>
		<RecipeFor id="t_flip_flop" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/t_flip_flop.snbt" />
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

O flip-flop T é um componente lógico que simplesmente inverte o estado da saída sempre que um novo sinal de entrada LIGADO é recebido.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="1" x="0" y="0" type="io" hide={true} />
	<Logic name="2" x="32" y="0" type="t_flip_flop" />
	<Logic name="3" x="64" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="1" fromPort="0" to="2" toPort="0" />
	<Wire from="2" fromPort="0" to="3" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="0" />
</MicrochipScene>