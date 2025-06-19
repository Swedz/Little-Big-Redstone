---
navigation:
  title: "AND Gate"
  icon: "and_gate"
  parent: little_big_redstone:logic.md
  position: 12
categories:
  - logic
item_ids:
  - little_big_redstone:and_gate
---

# AND Gate

<ItemImage id="and_gate" scale="2" />

<RecipeFor id="and_gate" />

AND gates are logic components that require all inputs to be ON for the output to be ON. This is the inverse of a
[NAND gate](nand_gate.md).

<Row>
	<Column>
		<MicrochipScene color="red">
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="and" x="32" y="16" type="and_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" />
			<Wire from="a" fromPort="0" to="and" toPort="0" />
			<Wire from="b" fromPort="0" to="and" toPort="1" />
			<Wire from="and" fromPort="0" to="output" toPort="0" />
		</MicrochipScene>
	</Column>
	<Column>
		<MicrochipScene color="red">
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="and" x="32" y="16" type="and_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" />
			<Wire from="a" fromPort="0" to="and" toPort="0" />
			<Wire from="b" fromPort="0" to="and" toPort="1" />
			<Wire from="and" fromPort="0" to="output" toPort="0" />
			<RedstoneSignal direction="east" strength="15" />
			<RedstoneSignal direction="west" strength="15" />
		</MicrochipScene>
	</Column>
</Row>

AND gates can be configured to have anywhere between 2 and 10 inputs.

<MicrochipScene color="red">
	<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'north'}}" />
	<Logic name="b" x="0" y="16" type="io" data="{config:{direction:'south'}}" />
	<Logic name="c" x="0" y="32" type="io" data="{config:{direction:'east'}}" />
	<Logic name="d" x="0" y="48" type="io" data="{config:{direction:'west'}}" />
	<Logic name="and" x="32" y="16" type="and_gate" data="{config:{input_count:4}}" />
	<Logic name="output" x="64" y="24" type="io" data="{config:{direction:'up',input:false,signal_strength:15}}" />
	<Wire from="a" fromPort="0" to="and" toPort="0" />
	<Wire from="b" fromPort="0" to="and" toPort="1" />
	<Wire from="c" fromPort="0" to="and" toPort="2" />
	<Wire from="d" fromPort="0" to="and" toPort="3" />
	<Wire from="and" fromPort="0" to="output" toPort="0" />
	<RedstoneSignal direction="south" strength="15" />
	<RedstoneSignal direction="east" strength="15" />
</MicrochipScene>

## Truth Table

<TruthTable inputs="2">
	<TruthState input="0,0" output="0" />
	<TruthState input="0,1" output="0" />
	<TruthState input="1,0" output="0" />
	<TruthState input="1,1" output="1" />
</TruthTable>

*For details about truth tables, see the page [here](introduction.md).*