---
navigation:
  title: "Microchips"
  icon: "red_microchip"
  position: 1
item_ids:
  - little_big_redstone:white_microchip
  - little_big_redstone:light_gray_microchip
  - little_big_redstone:gray_microchip
  - little_big_redstone:black_microchip
  - little_big_redstone:brown_microchip
  - little_big_redstone:red_microchip
  - little_big_redstone:orange_microchip
  - little_big_redstone:yellow_microchip
  - little_big_redstone:lime_microchip
  - little_big_redstone:green_microchip
  - little_big_redstone:cyan_microchip
  - little_big_redstone:light_blue_microchip
  - little_big_redstone:blue_microchip
  - little_big_redstone:purple_microchip
  - little_big_redstone:magenta_microchip
  - little_big_redstone:pink_microchip
---

# Microchips

<FloatingColumn align="right">
	<PaddedBox left="5">
		<RecipeFor id="red_microchip" />
	</PaddedBox>
</FloatingColumn>

<FloatingColumn>
	<PaddedBox left="5" right="10" bottom="5">
		<GameScene zoom="1.5" padding="0" background="transparent">
			<ImportStructure src="assets/structures/microchips.snbt" />
			<IsometricCamera yaw="135" pitch="30" />
		</GameScene>
	</PaddedBox>
</FloatingColumn>

Microchips são blocos que podem ter [lógica](logic/introduction.md) colocada dentro deles para criar sistemas complexos.
[Fios](redstone_bits.md) podem ser colocados entre lógicas para permitir que os sinais sejam transportados de um componente lógico para
outro.

Enquanto estiver no menu do microchip, você pode aumentar e diminuir o zoom usando o scroll do mouse. Segurar o clique esquerdo e arrastar moverá
a área de visualização do circuito.

### Direções

Ao usar componentes lógicos que recebem ou emitem sinal para o mundo, você precisará definir a direção que ele utiliza.
O microchip usa direções cardeais, significando <Color color="#4CFF00">norte</Color>,
<Color color="#0094FF">sul</Color>, <Color color="#FF0000">leste</Color>, e <Color color="#FF6A00">oeste</Color>,
assim como <Color color="#FFFFFF">para cima</Color> e <Color color="#FFD800">para baixo</Color>. A cor de uma direção pode ser vista
na lateral de um microchip pressionando **<KeyBind id="key.sneak" />** e olhando para ele.

### Coloração

Microchips podem ser tingidos com qualquer uma das 16 cores de corante padrão, e o menu refletirá a cor. Componentes lógicos podem ser
tingidos separadamente do microchip, mas por padrão eles herdarão a cor do microchip em que são colocados.
Para tingir componentes lógicos, você pode fazê-lo em sua grade de criação, como faria normalmente, ou pode clicar com o botão direito no
componente lógico no menu com o corante para aplicá-lo.

Similarmente, você pode usar um balde de água ou bolas de neve para remover a cor do componente lógico. Observe que as bolas de neve
serão consumidas, enquanto os baldes de água não.