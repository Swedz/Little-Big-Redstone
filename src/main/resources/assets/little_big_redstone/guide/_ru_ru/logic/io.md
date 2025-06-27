---
navigation:
  title: "Порт ввода-вывода"
  icon: "io"
  parent: little_big_redstone:logic.md
  position: 10
categories:
  - logic
item_ids:
  - little_big_redstone:io
---

# Порт ввода-вывода

<RecipeFor id="io" />

Порты ввода-вывода позволяют подавать и принимать редстоун-сигналы в вашей схеме.
Когда порты ввода-вывода размещаются в схеме, на гранях блока микрочипа появляются редстоуновые грани,
которые могут принимать или передавать сигнал.

<br />

Каждый порт ввода-вывода имеет направление: <Color color="#4CFF00">север</Color>,
<Color color="#0094FF">юг</Color>, <Color color="#FF0000">восток</Color>,
<Color color="#FF6A00">запад</Color>, <Color color="#FFFFFF">вверх</Color>, и <Color color="#FFD800">вниз</Color>.
Цвет направления отображается на грани микрочипа при приседании и взгляде на него.

**ПРИМЕЧАНИЕ:** Каждое направление может работать только как вход или как выход, но не одновременно.  
Если порты ввода-вывода размещены так, что на одной грани один порт — вход, а другой — выход,  
ни один из них не будет работать, и появится индикатор предупреждения.

<br />

Вы также можете настроить силу сигнала порта ввода-вывода.  
В режиме входа подаваемый редстоун-сигнал должен быть не меньше заданной силы,  
чтобы порт выдал выходной сигнал ВКЛ.  
В режиме выхода при подаче на вход порт получит сигнал ВКЛ и выдаст сигнал ровно установленной силы.

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