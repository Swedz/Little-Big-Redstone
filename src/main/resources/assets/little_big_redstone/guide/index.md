---
navigation:
  title: "Little Big Redstone"
  icon: "little_big_redstone:red_sticky_note"
---

# Little Big Redstone

This is a test

<Row>
	<Column alignItems="start">
		<MicrochipScene color="red">
			<Logic name="1" x="0" y="0" type="not_gate" />
			<Logic name="2" x="48" y="0" type="sequencer" />
			<Wire from="1" fromPort="0" to="2" toPort="0" />
		</MicrochipScene>
	</Column>
	<Column alignItems="start">
		<MicrochipScene color="green">
			<Logic name="1" x="0" y="0" type="not_gate" />
			<Logic name="2" x="48" y="0" type="sequencer" />
			<Wire from="1" fromPort="0" to="2" toPort="0" />
		</MicrochipScene>
	</Column>
	<Column alignItems="start">
		<MicrochipScene color="blue">
			<Logic name="1" x="0" y="0" type="not_gate" />
			<Logic name="2" x="48" y="0" type="sequencer" />
			<Wire from="1" fromPort="0" to="2" toPort="0" />
		</MicrochipScene>
	</Column>
</Row>