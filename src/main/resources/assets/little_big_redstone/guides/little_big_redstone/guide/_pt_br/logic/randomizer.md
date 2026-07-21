---
navigation:
  title: "Aleatorizador"
  icon: "randomizer"
  parent: little_big_redstone:logic.md
  position: 22
categories:
  - logic
item_ids:
  - little_big_redstone:randomizer
---

# Aleatorizador

<RecipeFor id="randomizer" />

O aleatorizador pode ter entre 1 e 10 saídas, no entanto, apenas uma saída pode estar LIGADA por vez. Quando a entrada
está LIGADA, a cada tick, uma saída aleatória estará LIGADA por uma porcentagem de tempo configurável. Por padrão, a chance é de 100%,
mas você pode alterá-la para qualquer valor. Por exemplo, com a chance definida para 50%, em metade dos ticks em que a entrada estiver LIGADA,
uma saída aleatória estará LIGADA. Cada saída tem igual chance de ser selecionada.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="throttler" x="32" y="8" type="pulse_throttler" hide={true} />
			<Logic name="randomizer" x="64" y="0" type="randomizer" data="{config:{outputs:5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />
		
			<Wire from="input" fromPort="0" to="throttler" toPort="0" />
			<Wire from="throttler" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="throttler" x="32" y="8" type="pulse_throttler" hide={true} />
			<Logic name="randomizer" x="64" y="0" type="randomizer" data="{config:{outputs:5,chance:0.5}}" />
			<Logic name="output" x="96" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />
		
			<Wire from="input" fromPort="0" to="throttler" toPort="0" />
			<Wire from="throttler" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>

Os exemplos acima têm suas entradas encurtadas para um único tick. Note que se você não fizer o mesmo, a saída será
diferente a cada tick. Abaixo está um exemplo de como isso se pareceria.

<Row>
	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="randomizer" x="32" y="0" type="randomizer" data="{config:{outputs:5}}" />
			<Logic name="output" x="64" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />

			<Wire from="input" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>

	<Column>
		<MicrochipScene color="red" includeToolbar={true}>
			<Logic name="input" x="0" y="8" type="io" hide={true} />
			<Logic name="randomizer" x="32" y="0" type="randomizer" data="{config:{outputs:5,chance:0.5}}" />
			<Logic name="output" x="64" y="0" type="or_gate" data="{config:{input_count:5}}" hide={true} />
		
			<Wire from="input" fromPort="0" to="randomizer" toPort="0" />
			<Wire from="randomizer" fromPort="0" to="output" toPort="0" />
			<Wire from="randomizer" fromPort="1" to="output" toPort="1" />
			<Wire from="randomizer" fromPort="2" to="output" toPort="2" />
			<Wire from="randomizer" fromPort="3" to="output" toPort="3" />
			<Wire from="randomizer" fromPort="4" to="output" toPort="4" />
		
			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>