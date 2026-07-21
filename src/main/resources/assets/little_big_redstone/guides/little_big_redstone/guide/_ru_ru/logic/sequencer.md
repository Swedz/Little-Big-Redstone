---
navigation:
  title: "Секвенсор"
  icon: "sequencer"
  parent: little_big_redstone:logic.md
  position: 19
categories:
  - logic
item_ids:
  - little_big_redstone:sequencer
---

# Секвенсор

<FloatingColumn width="100" align="right">
	### Аналоговый
    Секвенсоры выдают выходной сигнал той же силы, что и входной. 
    В случае секвенсоров входной сигнал не всегда должен оставаться включённым, чтобы выход был включён. 
    В таком случае для выхода используется сила входного сигнала, который привёл к включению выхода.
</FloatingColumn>

<Row>
	<Column>
		<RecipeFor id="sequencer" />
	</Column>

	<Column>
		<GameScene zoom="1.48" padding="3" interactive={true}>
			<ImportStructure src="../assets/structures/sequencer.snbt" />
			<BoxAnnotation min="2 1 0" max="3 1.5 1" color="#FFFFFF">
				Секвенсор гораздо гибче повторителя, но по сути похож на него.
			</BoxAnnotation>
			<IsometricCamera yaw="150" pitch="30" />
		</GameScene>
	</Column>
</Row>

> **Примечание:** В Minecraft 20 тиков соответствуют одной секунде реального времени.

Секвенсор — это логический компонент, который позволяет точно задерживать сигналы.

Существует три различных режима, которые можно использовать:
«Слабый», «Сильный» и «Счётчик» (подробнее см. в разделах ниже).
Секвенсор имеет внутренний счётчик, который колеблется от 0 до установленной задержки (по умолчанию 20). В зависимости от выбранного режима, счётчик будет увеличиваться или уменьшаться по-разному. 
Когда счётчик секвенсора достигает своего максимума, выход включён, в противном случае он выключен.

Секвенсор может быть настроен на автоматический сброс внутреннего счётчика до 0, как только он достигнет своего максимального значения. 
При этом он будет выдавать сигнал ВКЛ ровно на 1 тик.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:30,auto_reset:true}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal direction="north" strength="15" />
</MicrochipScene>

Кроме того, секвенсор может быть настроен на наличие второго входного порта. 
Всякий раз, когда этот второй входной порт включён, внутренний счётчик секвенсора будет сброшен до 0.

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

### Слабый режим

После того как сигнал ВКЛ подан на первый входной порт секвенсора, независимо от длительности сигнала, 
секвенсор будет увеличивать счётчик, пока не достигнет настроенной задержки. Используйте этот режим, когда вам нужно просто задержать сигнал на определённое время.

Ниже приведён пример секвенсора, который настроен на задержку в 100 тиков (5 секунд) и автоматически сбрасывается после завершения. 
Входной сигнал довольно кратковременный, но секвенсор продолжает увеличивать счётчик независимо от того, остаётся ли вход включённым или нет.

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

### Сильный режим

Пока на первый входной порт секвенсора подаётся сигнал ВКЛ, секвенсор будет увеличивать счётчик. 
Однако, если сигнал прекратится, секвенсор будет уменьшать счётчик.

Ниже приведены примеры секвенсора, который настроен на задержку в 100 тиков (5 секунд).

В этом примере сигнал ВКЛ достаточно долгий, чтобы секвенсор достиг состояния ВКЛ, а затем выключается на столько же, чтобы вернуться в исходное состояние.

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

В этом примере сигнал ВКЛ достаточно долог лишь для того, чтобы секвенсор достиг примерно половины пути. Затем входной сигнал выключается, и счётчик уменьшается обратно до исходного состояния. 
Из-за этого он никогда не достигает завершённого состояния, и, следовательно, выход всегда ВЫКЛ.

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

### Режим «Счётчик»

Пока на первый входной порт секвенсора подаётся сигнал ВКЛ, секвенсор будет увеличивать счётчик. 
Единственный способ, которым секвенсор может уменьшить счётчик в этом режиме, — это сброс либо с помощью опции автосброса, либо через порт сброса.

Ниже приведён пример секвенсора, который настроен на задержку в 100 тиков (5 секунд) и автоматически сбрасывается после завершения. 
Входной сигнал часто включается и выключается, но секвенсор не уменьшает счётчик, когда вход ВЫКЛ.

<MicrochipScene color="red" includeToolbar={true}>
	<Logic name="input" x="0" y="0" type="io" hide={true} />
	<Logic name="sequencer" x="32" y="0" type="sequencer" data="{config:{delay:100,mode:'counter',auto_reset:true}}" />
	<Logic name="output" x="80" y="0" type="io" data="{config:{input:false,direction:'south',signal_strength:15}}" hide={true} />

	<Wire from="input" fromPort="0" to="sequencer" toPort="0" />
	<Wire from="sequencer" fromPort="0" to="output" toPort="0" />

	<RedstoneSignal step="0" direction="north" strength="15" />
	<RedstoneSignal step="1" direction="north" strength="0" />
</MicrochipScene>

Ниже приведён пример секвенсора, который настроен на задержку в 5 тиков и автоматически сбрасывается после завершения. 
Входной сигнал ограничен [Ограничителем импульсов](pulse_throttler.md), чтобы предотвратить его длительность более одного тика. 
Таким образом, секвенсор будет выдавать выходной сигнал только на один тик после получения 5 отдельных сигналов ВКЛ.

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