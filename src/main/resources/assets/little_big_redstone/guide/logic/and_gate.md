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

<FloatingColumn width="100" align="right">
	### Truth Table
	<TruthTable inputs="2">
		<TruthState input="0,0" output="0" />
		<TruthState input="0,1" output="0" />
		<TruthState input="1,0" output="0" />
		<TruthState input="1,1" output="1" />
	</TruthTable>
	*For details about truth tables, see the page [here](introduction.md).*
</FloatingColumn>

<Row>
	<Column>
		<RecipeFor id="and_gate" />
	</Column>

	<Column>
		<GameScene zoom="1.85" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/and_gate.snbt" />
			<Block id="minecraft:redstone_wire" x="0" y="0" z="1" p:east="side" p:west="side" />
			<Block id="minecraft:redstone_wire" x="3" y="0" z="2" p:east="side" p:west="side" />
			<Block id="minecraft:redstone_wire" x="4" y="0" z="2" p:east="side" p:west="side" />
			<Block id="minecraft:redstone_wire" x="3" y="0" z="0" p:east="side" p:west="side" />
			<Block id="minecraft:redstone_wire" x="4" y="0" z="0" p:east="side" p:west="side" />
			<BoxAnnotation min="3 0 2" max="5 1 3" color="#4EC5E7">
                Input A
            </BoxAnnotation>
            <BoxAnnotation min="3 0 0" max="5 1 1" color="#4EC5E7">
                Input B
            </BoxAnnotation>
            <BoxAnnotation min="0 0 1" max="2 1 2" color="#F9932B">
                Output
            </BoxAnnotation>
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

AND gates are logic components that require all inputs to be ON for the output to be ON. This is the inverse of a
[NAND gate](nand_gate.md).

AND gates can be configured to have anywhere between 2 and 10 inputs.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="a" x="0" y="0" type="io" data="{config:{direction:'east'}}" />
			<Logic name="b" x="0" y="32" type="io" data="{config:{direction:'west'}}" />
			<Logic name="and" x="32" y="16" type="and_gate" />
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
			<Logic name="and" x="32" y="16" type="and_gate" data="{config:{input_count:4}}" />
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