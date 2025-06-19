---
navigation:
  title: "NOT Gate"
  icon: "not_gate"
  parent: little_big_redstone:logic.md
  position: 11
categories:
  - logic
item_ids:
  - little_big_redstone:not_gate
---

# NOT Gate

<ItemImage id="not_gate" scale="2" />

<RecipeFor id="not_gate" />

NOT Gates are the simplest gate available. These take a single input, invert the signal, and provide a single output.

<Row>
	<Column>
		<MicrochipScene color="red">
			<Logic name="1" x="0" y="0" type="io" />
			<Logic name="2" x="32" y="0" type="not_gate" />
			<Logic name="3" x="64" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" />
			<Wire from="1" fromPort="0" to="2" toPort="0" />
			<Wire from="2" fromPort="0" to="3" toPort="0" />
			<RedstoneSignal direction="north" strength="15" />
		</MicrochipScene>
	</Column>
	<Column>
		<MicrochipScene color="red">
			<Logic name="1" x="0" y="0" type="io" />
			<Logic name="2" x="32" y="0" type="not_gate" />
			<Logic name="3" x="64" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" />
			<Wire from="1" fromPort="0" to="2" toPort="0" />
			<Wire from="2" fromPort="0" to="3" toPort="0" />
		</MicrochipScene>
	</Column>
</Row>

## Truth Table

<TruthTable>
	<TruthState input="0" output="1" />
	<TruthState input="1" output="0" />
</TruthTable>

*For details about truth tables, see the page [here](introduction.md).*