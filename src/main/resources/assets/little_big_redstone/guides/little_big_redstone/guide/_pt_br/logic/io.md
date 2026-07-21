---
navigation:
  title: "Porta de E/S"
  icon: "io"
  parent: little_big_redstone:logic.md
  position: 10
categories:
  - logic
item_ids:
  - little_big_redstone:io
---

# Porta de E/S

<RecipeFor id="io" />

Portas de E/S são a forma de você inserir e extrair sinais de redstone para e de seus circuitos. Quando Portas de E/S são colocadas em
um circuito, você poderá ver faces de redstone nas laterais do bloco de microchip que podem aceitar ou fornecer um
sinal de redstone.

<br />

Cada Porta de E/S tem uma direção à qual se conecta: <Color color="#4CFF00">Norte</Color>,
<Color color="#0094FF">Sul</Color>, <Color color="#FF0000">Leste</Color>, <Color color="#FF6A00">Oeste</Color>,
<Color color="#FFFFFF">Acima</Color>, e <Color color="#FFD800">Abaixo</Color>. A cor de uma direção pode ser vista no
lado de um microchip ao agachar-se e olhar para ele.

**NOTA:** Cada direção só pode atuar como uma entrada ou uma saída, não ambas. Se Portas de E/S forem colocadas em um circuito de tal forma
que uma é uma entrada e a outra é uma saída na mesma face - nenhuma das portas funcionará e um indicador de aviso será
exibido.

<br />

Você também pode configurar a força do sinal de uma Porta de E/S. Quando a porta está no modo de entrada, o sinal de redstone inserido
deve atender ou exceder o sinal para que a porta forneça uma saída LIGADA. Quando a porta está no modo de saída e
a porta de entrada é fornecida com um sinal LIGADO, o sinal de redstone fornecido será igual à força do sinal definida.

<Row>
	<Column>
		<MicrochipScene color="red" marginWidth="16" includeToolbar={true}>
			<Logic name="input" x="0" y="0" type="io" />
			<Logic name="output" x="32" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

			<Wire from="input" fromPort="0" to="output" toPort="0" />
		</MicrochipScene>
	</Column>
	
	<Column>
		<MicrochipScene color="red" marginWidth="16" includeToolbar={true}>
			<Logic name="input" x="0" y="0" type="io" hide={true} />
			<Logic name="output" x="32" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" />
		
			<Wire from="input" fromPort="0" to="output" toPort="0" />
		</MicrochipScene>
	</Column>
</Row>