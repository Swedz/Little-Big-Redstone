---
navigation:
  title: "Тег"
  icon: "tag"
  parent: little_big_redstone:logic.md
  position: 12
categories:
  - logic
item_ids:
  - little_big_redstone:tag
---

# Tag

<RecipeFor id="tag" />

Теги позволяют передавать беспроводные сигналы между схемами. 
У тегов есть два режима: сенсор и эмиттер. С помощью сенсоров вы принимаете сигналы, а с помощью эмиттеров — передаёте их.

<br />

Для каждого тега можно установить метку. Чтобы сенсор мог обнаружить эмиттер, у них должны быть одинаковые метки. Метки **чувствительны к регистру**. 
Использование меток необязательно: если оставить поле пустым, сенсор будет реагировать только на другие теги с пустой меткой.

У сенсора есть настройка порога, которая определяет, сколько эмиттеров он должен обнаружить, чтобы выдать сигнал ВКЛ. 
Например, если у вас есть сенсор с порогом 2 и меткой «something», вам нужно иметь 2 включённых эмиттера с такой же меткой «something», чтобы сенсор выдал сигнал на выход.

Примечание: сенсоры могут обнаруживать эмиттеры внутри той же самой схемы.

<br />

Для сенсоров также можно выбрать, являются ли они глобальными. Это определяет, будут ли они обнаруживать эмиттеры, установленные другими игроками на многопользовательском сервере. 
По умолчанию сенсоры не являются глобальными — это значит, что они реагируют только на ваши эмиттеры.

<br />

Ниже приведён пример эмиттера и сенсора тегов с одинаковой меткой. 
Поскольку у них общая метка, а порог сенсора равен 1, при включении эмиттера сенсор также переходит в состояние ВКЛ.

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

Обратите внимание, что ниже у двух тегов разные метки. Из-за этого сенсор не обнаруживает эмиттер.

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