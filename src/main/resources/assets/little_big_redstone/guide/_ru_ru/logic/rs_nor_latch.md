---
navigation:
  title: "RS-триггер на ИЛИ-НЕ"
  icon: "rs_nor_latch"
  parent: little_big_redstone:logic.md
  position: 24
categories:
  - logic
item_ids:
  - little_big_redstone:rs_nor_latch
---

# RS-триггер на ИЛИ-НЕ

<FloatingColumn width="100" align="right">
    ### Таблица истинности
    <TruthTable inputs="2" outputs="1">
       <TruthState input="0,0" output="0" />
       <TruthState input="0,1" output="0" />
       <TruthState input="1,0" output="1" />
       <TruthState input="1,1" output="0" />
    </TruthTable>
    *Подробности о таблицах истинности смотрите на [этой странице](introduction.md).*
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

RS-триггер на ИЛИ-НЕ — это логический компонент с двумя входами и одним выходом.

Вход A, или вход "Установка" (Set), при включении установит выход в состояние ВКЛ, и он останется в этом состоянии даже после выключения A. 
Любые последующие включения входа A не изменят выход, пока он находится в состоянии ВКЛ.

Вход B, также известный как вход "Сброс" (Reset), при включении установит выход в состояние ВЫКЛ, и он останется в этом состоянии даже после выключения B — при условии, что A также выключен. 
Любые последующие включения входа B не изменят выход, пока он находится в состоянии ВЫКЛ.

Когда A и B включены, состояние выхода — ВЫКЛ.

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
		
			<Wire from="a" fromPort="0" to="rs_nor_latch" toPort="0" />
			<Wire from="b" fromPort="0" to="rs_nor_latch" toPort="1" />
			<Wire from="rs_nor_latch" fromPort="0" to="output" toPort="0" />
		
			<RedstoneSignal step="0" direction="east" strength="15" />
			<RedstoneSignal step="0" direction="west" strength="15" />
		</MicrochipScene>
	</Column>
</Row>