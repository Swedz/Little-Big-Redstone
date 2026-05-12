---
navigation:
  title: "Считыватель"
  icon: "reader"
  parent: little_big_redstone:logic.md
  position: 11
categories:
  - logic
item_ids:
  - little_big_redstone:reader
---

# Считыватель

<FloatingColumn width="100" align="right">
	### Булевый
	Считыватели всегда выдают только значение 0 (ВЫКЛ) или 1 (ВКЛ).
</FloatingColumn>

<RecipeFor id="reader" />

Считыватель — это логический компонент без внутреннего входа в схеме. Вместо этого он выдаёт сигнал в зависимости от заполненности контейнера, 
находящегося непосредственно по направлению его настройки.

В качестве контейнеров могут быть различные типы контейнеров: для предметов (сундуки, бочки, печи и др.), жидкостей (например, жидкостные баки) и энергии (FE-аккумуляторы и др. и т.п.). 
Изначально считыватель проверяет, насколько заполнен контейнер предметами, но это поведение можно изменить в настройках.

Считыватель можно настроить на порог минимальной заполненности контейнера для выдачи сигнала ВКЛ. 
По умолчанию порог составляет 50%.

Ниже показан пример использования считывателя для включения редстоунового фонаря, когда сундук заполнен не менее чем на 50%.

<PaddedBox left="5" top="5">
	<Row>
		<Column>
			<PaddedBox top="4">
				<GameScene zoom="2" padding="0">
					<Block id="minecraft:chest" />
					<BlockAnnotation>
						Сундук заполнен как минимум на 50%
					</BlockAnnotation>
					<Block id="red_microchip" x="-1" />
					<Block id="minecraft:redstone_lamp" x="-2" p:lit="true" />
					<IsometricCamera yaw="200" pitch="30" />
				</GameScene>
			</PaddedBox>
		</Column>

		<Column>
			<MicrochipScene color="red" padding="0" marginWidth="16">
				<Logic name="reader" x="0" y="0" type="reader" data="{config:{direction:'west'}}" />
				<Logic name="output" x="32" y="0" type="io" data="{config:{direction:'east',input:false,signal_strength:15}}" />

				<Wire from="reader" fromPort="0" to="output" toPort="0" powered="1" />
			</MicrochipScene>
		</Column>
	</Row>

	<Row>
		<Column>
			<PaddedBox top="4">
				<GameScene zoom="2" padding="0">
					<Block id="minecraft:chest" />
					<BlockAnnotation>
						Сундук заполнен менее чем на 50%
					</BlockAnnotation>
					<Block id="red_microchip" x="-1" />
					<Block id="minecraft:redstone_lamp" x="-2" p:lit="false" />
					<IsometricCamera yaw="200" pitch="30" />
				</GameScene>
			</PaddedBox>
		</Column>

		<Column>
			<MicrochipScene color="red" padding="0" marginWidth="16">
				<Logic name="reader" x="0" y="0" type="reader" data="{config:{direction:'west'}}" />
				<Logic name="output" x="32" y="0" type="io" data="{config:{direction:'east',input:false,signal_strength:15}}" />

				<Wire from="reader" fromPort="0" to="output" toPort="0" />
			</MicrochipScene>
		</Column>
	</Row>
</PaddedBox>