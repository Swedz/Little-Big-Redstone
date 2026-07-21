---
navigation:
  title: "Leitor"
  icon: "reader"
  parent: little_big_redstone:logic.md
  position: 11
categories:
  - logic
item_ids:
  - little_big_redstone:reader
---

# Leitor

<RecipeFor id="reader" />

O leitor é um componente lógico que não tem entrada dentro do circuito. Em vez disso, o leitor emitirá um sinal baseado
na capacidade de preenchimento do contêiner diretamente adjacente ao microchip, na direção em que está configurado.

Contêineres válidos para o leitor consistem em contêineres de itens (baús, barris, fornalhas, etc.), contêineres de fluidos (tanques de fluido de mods),
e contêineres de energia (contêineres FE de mods, como baterias). Por padrão, ele lê o preenchimento de itens, mas
você pode alterar isso no menu de configuração de lógica.

Um leitor pode ser configurado para detectar uma porcentagem mínima de preenchimento de um contêiner para que ele tenha um sinal de saída
LIGADO. Por padrão, a porcentagem mínima de preenchimento é de 50%.

Abaixo está um exemplo de um leitor sendo usado para acender uma lâmpada de redstone quando o baú está com pelo menos 50% de preenchimento.

<PaddedBox left="5" top="5">
	<Row>
		<Column>
			<PaddedBox top="4">
				<GameScene zoom="2" padding="0">
					<Block id="minecraft:chest" />
					<BlockAnnotation>
						O baú está com pelo menos 50% de preenchimento
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
	
				<Wire from="reader" fromPort="0" to="output" toPort="0" powered={true} />
			</MicrochipScene>
		</Column>
	</Row>
	
	<Row>
		<Column>
			<PaddedBox top="4">
				<GameScene zoom="2" padding="0">
					<Block id="minecraft:chest" />
					<BlockAnnotation>
						O baú está com menos de 50% de preenchimento
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