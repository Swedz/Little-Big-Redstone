---
navigation:
  title: "Etiqueta"
  icon: "tag"
  parent: little_big_redstone:logic.md
  position: 12
categories:
  - logic
item_ids:
  - little_big_redstone:tag
---

# Etiqueta

<RecipeFor id="tag" />

As Etiquetas permitem enviar sinais sem fio entre circuitos. As Etiquetas têm dois modos: sensor e emissor. Sensores 
são como você recebe sinais, e emissores são como você transmite sinais.

<br />

Cada etiqueta pode ter um rótulo definido. Para que um sensor possa detectar um emissor, ambos devem ter o mesmo rótulo.
Rótulos **diferenciam maiúsculas de minúsculas**. Rótulos não são obrigatórios e, se deixados vazios, coincidirão apenas 
com outras etiquetas que também tenham um rótulo vazio.

Um sensor possui uma configuração de limiar, que determina quantos emissores ele deve detectar para produzir uma saída
LIGADA. Por exemplo, se você tiver um sensor com um limiar de 2 e um rótulo de "something", você deve ter 2 emissores 
também com o rótulo "something" que estejam LIGADOS para que o sensor tenha uma saída LIGADA.

Observe que os sensores podem detectar emissores no mesmo circuito.

<br />

Sensores também podem escolher se são globais ou não. Em outras palavras, se ele detecta emissores colocados por outra 
pessoa em um servidor multijogador. Por padrão, sensores não são globais - significando que eles detectarão apenas seus emissores.

<br />

Abaixo está um exemplo de um emissor e um sensor de etiqueta, ambos com o mesmo rótulo. Como eles compartilham um rótulo 
e o sensor tem apenas um limiar de 1, quando o emissor está LIGADO, o sensor também está LIGADO.

<Row>
	<Column>
		<MicrochipScene color="red" marginWidth="16" includeToolbar={true}>
			<Logic name="input1" x="0" y="0" type="io" hide={true} />
			<Logic name="output1" x="32" y="0" type="tag" data="{config:{input:false,label:'something'}}" />

			<Logic name="tag2" x="64" y="0" type="tag" data="{config:{label:'something'}}" />
			<Logic name="input2" x="64" y="0" type="io" hide={true} />
			<Logic name="output2" x="96" y="0" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="input1" fromPort="0" to="output1" toPort="0" />
			<Wire from="input2" fromPort="0" to="output2" toPort="0" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>

Observe que, abaixo, as duas etiquetas têm rótulos diferentes. Por causa disso, o sensor não detecta o emissor.

<Row>
	<Column>
		<MicrochipScene color="red" marginWidth="16" includeToolbar={true}>
			<Logic name="input1" x="0" y="0" type="io" hide={true} />
			<Logic name="output1" x="32" y="0" type="tag" data="{config:{input:false,label:'something'}}" />

			<Logic name="input2" x="64" y="0" type="tag" data="{config:{label:'something_else'}}" />
			<Logic name="output2" x="96" y="0" type="io" data="{config:{input:false,signal_strength:15}}" hide={true} />

			<Wire from="input1" fromPort="0" to="output1" toPort="0" />
			<Wire from="input2" fromPort="0" to="output2" toPort="0" />

			<RedstoneSignal step="0" direction="north" strength="15" />
			<RedstoneSignal step="1" direction="north" strength="0" />
		</MicrochipScene>
	</Column>
</Row>