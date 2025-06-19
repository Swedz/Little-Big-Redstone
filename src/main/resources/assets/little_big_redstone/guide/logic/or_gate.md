---
navigation:
  title: "OR Gate"
  icon: "or_gate"
  parent: little_big_redstone:logic.md
  position: 14
categories:
  - logic
item_ids:
  - little_big_redstone:or_gate
---

# OR Gate

<ItemImage id="or_gate" scale="2" />

<RecipeFor id="or_gate" />

OR gates are logic components that require at least one input to be ON for the output to be ON. This is the inverse of
a [NOR gate](nor_gate.md).

Since input ports can only accept one wire, an OR gate should be used when you need to combine multiple wires into one
port.

<Row>
	<Column>
		<MicrochipScene color="red">
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="gate" x="32" y="16" type="or_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" />
			<Wire from="a" fromPort="0" to="gate" toPort="0" />
			<Wire from="b" fromPort="0" to="gate" toPort="1" />
			<Wire from="gate" fromPort="0" to="output" toPort="0" />
		</MicrochipScene>
	</Column>
	<Column>
		<MicrochipScene color="red">
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="gate" x="32" y="16" type="or_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" />
			<Wire from="a" fromPort="0" to="gate" toPort="0" />
			<Wire from="b" fromPort="0" to="gate" toPort="1" />
			<Wire from="gate" fromPort="0" to="output" toPort="0" />
			<RedstoneSignal direction="east" strength="15" />
		</MicrochipScene>
	</Column>
</Row>

OR gates can be configured to have anywhere between 2 and 10 inputs.

<MicrochipScene color="red">
	<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'north'}}" />
	<Logic name="b" x="0" y="16" type="io" data="{config:{direction:'south'}}" />
	<Logic name="c" x="0" y="32" type="io" data="{config:{direction:'east'}}" />
	<Logic name="d" x="0" y="48" type="io" data="{config:{direction:'west'}}" />
	<Logic name="gate" x="32" y="16" type="or_gate" data="{config:{input_count:4}}" />
	<Logic name="output" x="64" y="24" type="io" data="{config:{direction:'up',input:false,signal_strength:15}}" />
	<Wire from="a" fromPort="0" to="gate" toPort="0" />
	<Wire from="b" fromPort="0" to="gate" toPort="1" />
	<Wire from="c" fromPort="0" to="gate" toPort="2" />
	<Wire from="d" fromPort="0" to="gate" toPort="3" />
	<Wire from="gate" fromPort="0" to="output" toPort="0" />
	<RedstoneSignal direction="south" strength="15" />
	<RedstoneSignal direction="east" strength="15" />
</MicrochipScene>

## Truth Table

<TruthTable inputs="2" outputs="1">
	<TruthState input="0,0" output="0" />
	<TruthState input="0,1" output="1" />
	<TruthState input="1,0" output="1" />
	<TruthState input="1,1" output="1" />
</TruthTable>