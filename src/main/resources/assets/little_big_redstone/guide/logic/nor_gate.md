---
navigation:
  title: "NOR Gate"
  icon: "nor_gate"
  parent: little_big_redstone:logic.md
  position: 15
categories:
  - logic
item_ids:
  - little_big_redstone:nor_gate
---

# NOR Gate

<ItemImage id="nor_gate" scale="2" />

<FloatingColumn width="100" align="right">
	### Truth Table
	<TruthTable inputs="2" outputs="1">
		<TruthState input="0,0" output="1" />
		<TruthState input="0,1" output="0" />
		<TruthState input="1,0" output="0" />
		<TruthState input="1,1" output="0" />
	</TruthTable>
	*For details about truth tables, see the page [here](introduction.md).*
</FloatingColumn>

<RecipeFor id="nor_gate" />

NOR gates are logic components that require at least one input to be ON for the output to be OFF. This is the inverse
of an [OR gate](or_gate.md).

NOR gates can be configured to have anywhere between 2 and 10 inputs.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="and" x="32" y="16" type="nor_gate" />
			<Logic name="output" x="64" y="16" type="io" data="{config:{input:false,signal_strength:15}}" />

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
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'north'}}" />
			<Logic name="b" x="0" y="16" type="io" data="{config:{direction:'south'}}" />
			<Logic name="c" x="0" y="32" type="io" data="{config:{direction:'east'}}" />
			<Logic name="d" x="0" y="48" type="io" data="{config:{direction:'west'}}" />
			<Logic name="and" x="32" y="16" type="nor_gate" data="{config:{input_count:4}}" />
			<Logic name="output" x="64" y="24" type="io" data="{config:{direction:'up',input:false,signal_strength:15}}" />
		
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
			<RedstoneSignal step="1" direction="west" strength="15" />
		
			<RedstoneSignal step="2" direction="north" strength="0" />
			<RedstoneSignal step="2" direction="south" strength="15" />
			<RedstoneSignal step="2" direction="east" strength="15" />
			<RedstoneSignal step="2" direction="west" strength="0" />
		
			<RedstoneSignal step="3" direction="north" strength="15" />
			<RedstoneSignal step="3" direction="south" strength="15" />
			<RedstoneSignal step="3" direction="east" strength="15" />
			<RedstoneSignal step="3" direction="west" strength="15" />
		</MicrochipScene>
	</Column>
</Row>