---
navigation:
  title: "Sequenciador"
  icon: "sequencer"
  parent: little_big_redstone:logic.md
  position: 19
categories:
  - logic
item_ids:
  - little_big_redstone:sequencer
---

# Sequenciador

<Row>
	<Column>
		<RecipeFor id="sequencer" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/sequencer.snbt" />
			<BoxAnnotation min="2 1 0" max="3 1.5 1" color="#FFFFFF">
				O sequenciador é um pouco mais flexível que um repetidor, mas tem um conceito semelhante.
			</BoxAnnotation>
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

> **Nota:** No Minecraft, há 20 ticks por segundo da vida real.

O sequenciador é um componente lógico que permite atrasar sinais com controle preciso.

Existem três modos diferentes que podem ser usados: Fraco, Forte e Contador (veja as seções abaixo para mais
informações). O sequenciador tem um contador interno que varia de 0 ao atraso definido (padrão 20). Com base no modo
selecionado, o contador irá incrementar ou decrementar de forma diferente. Quando o contador do sequenciador está em seu máximo, a
saída está LIGADA, caso contrário, está DESLIGADA.

O sequenciador pode ser configurado para ter seu contador interno reiniciado para 0 assim que atingir seu valor máximo. Nesse ponto, ele
emitirá um sinal LIGADO por exatamente 1 tick.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:30,auto_reset:true}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal direction="north" strength="15" />
</MicrochipScene>

Além disso, o sequenciador pode ser configurado para ter uma segunda porta de entrada. Sempre que esta segunda porta de entrada estiver LIGADA, o
contador interno do sequenciador será reiniciado para 0.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input1" x="0" y="12" type="io" data="{config:{direction:'east'}}" hide={true} />
	<Logic name="input2" x="0" y="20" type="io" data="{config:{direction:'west'}}" hide={true} />
	<Logic name="sequencer" x="32" y="16" type="sequencer" data="{config:{delay:50,reset_port:true}}" />
	<Logic name="output" x="80" y="16" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input1" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="input2" fromPort="0" to="sequencer" toPort="1" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="east" strength="15" />
	<RedstoneSignal step="1" direction="east" strength="0" />
	<RedstoneSignal step="2" direction="east" strength="0" />
	<RedstoneSignal step="3" direction="west" strength="15" />
	<RedstoneSignal step="4" direction="west" strength="0" />
</MicrochipScene>

<br />

### Modo Fraco

Após um sinal LIGADO ser passado para a primeira porta de entrada do sequenciador, independentemente da duração do sinal, o
sequenciador irá incrementar até atingir o atraso configurado. Use este modo quando você quiser simplesmente atrasar um sinal
por um tempo definido.

Abaixo está um exemplo de um sequenciador que está configurado para atrasar 100 ticks (5 segundos) e reiniciar automaticamente assim que
concluir. O sinal de entrada é um tanto breve, mas o sequenciador continua a incrementar independentemente de o
input permanecer LIGADO ou não.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:100,auto_reset:true}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="0" />
	<RedstoneSignal step="2" direction="north" strength="0" />
	<RedstoneSignal step="3" direction="north" strength="0" />
</MicrochipScene>

<br />

### Modo Forte

Enquanto um sinal LIGADO for passado para a primeira porta de entrada do sequenciador, o sequenciador irá incrementar. No entanto, se
o sinal cessar, o sequenciador irá decrementar.

Abaixo estão exemplos de um sequenciador que está configurado para atrasar 100 ticks (5 segundos).

Este tem um sinal LIGADO longo o suficiente para que o sequenciador atinja seu estado LIGADO, e então desliga-se pelo mesmo tempo
para que ele retorne ao seu estado original.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:100,mode:'strong'}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="15" />
	<RedstoneSignal step="2" direction="north" strength="15" />
	<RedstoneSignal step="3" direction="north" strength="15" />
	<RedstoneSignal step="4" direction="north" strength="0" />
	<RedstoneSignal step="5" direction="north" strength="0" />
	<RedstoneSignal step="6" direction="north" strength="0" />
	<RedstoneSignal step="7" direction="north" strength="0" />
</MicrochipScene>

Este tem um sinal LIGADO apenas longo o suficiente para que o sequenciador atinja cerca da metade do caminho. Então o sinal de entrada
desliga, e ele decrementa todo o caminho de volta ao seu estado original. Por causa disso, ele nunca atinge o estado completo
e, portanto, a saída está sempre DESLIGADA.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:100,mode:'strong'}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="15" />
	<RedstoneSignal step="2" direction="north" strength="0" />
	<RedstoneSignal step="3" direction="north" strength="0" />
</MicrochipScene>

<br />

### Modo Contador

Enquanto um sinal LIGADO for passado para a primeira porta de entrada do sequenciador, o sequenciador irá incrementar. A única forma
do sequenciador descer neste modo é se ele for reiniciado pela opção de reset automático ou pela porta de reset.

Abaixo está um exemplo de um sequenciador que está configurado para atrasar 100 ticks (5 segundos) e reiniciar automaticamente assim que
concluir. A entrada liga e desliga frequentemente, mas o sequenciador não decrementa quando a entrada está DESLIGADA.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:100,mode:'counter',auto_reset:true}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="0" />
</MicrochipScene>

Abaixo está um exemplo de um sequenciador que está configurado para atrasar 5 ticks e reinicia automaticamente assim que conclui. A entrada
é limitada por um [Limitador de Pulso](pulse_throttler.md) para evitar que o sinal de entrada seja mais longo do que um único tick.
Desta forma, o sequenciador só emitirá um sinal de saída por um único tick assim que 5 entradas LIGADAS separadas forem recebidas.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="pulse_throttler" x="32" y="0" type="pulse_throttler" />
	<Logic name="sequencer" x="64" y="0" type="sequencer" data="{config:{delay:5,mode:'counter',auto_reset:true}}" />
	<Logic name="output" x="112" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="pulse_throttler" toPort="0" />
	<Wire from="pulse_throttler" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="0" />
</MicrochipScene>