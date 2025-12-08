---
navigation:
  title: "Disquetes"
  icon: "red_floppy_disk"
  position: 4
item_ids:
  - little_big_redstone:white_floppy_disk
  - little_big_redstone:light_gray_floppy_disk
  - little_big_redstone:gray_floppy_disk
  - little_big_redstone:black_floppy_disk
  - little_big_redstone:brown_floppy_disk
  - little_big_redstone:red_floppy_disk
  - little_big_redstone:orange_floppy_disk
  - little_big_redstone:yellow_floppy_disk
  - little_big_redstone:lime_floppy_disk
  - little_big_redstone:green_floppy_disk
  - little_big_redstone:cyan_floppy_disk
  - little_big_redstone:light_blue_floppy_disk
  - little_big_redstone:blue_floppy_disk
  - little_big_redstone:purple_floppy_disk
  - little_big_redstone:magenta_floppy_disk
  - little_big_redstone:pink_floppy_disk
---

# Disquetes

<FloatingColumn align="right">
	<PaddedBox left="5">
		<RecipeFor id="red_floppy_disk" />
	</PaddedBox>
</FloatingColumn>

<FloatingColumn>
	<PaddedBox left="5" right="10" bottom="0">
		<Row gap="1">
			<ItemImage id="red_floppy_disk" />
			<ItemImage id="orange_floppy_disk" />
			<ItemImage id="yellow_floppy_disk" />
			<ItemImage id="lime_floppy_disk" />
		</Row>
		<Row gap="1">
			<ItemImage id="green_floppy_disk" />
			<ItemImage id="cyan_floppy_disk" />
			<ItemImage id="light_blue_floppy_disk" />
			<ItemImage id="blue_floppy_disk" />
		</Row>
		<Row gap="1">
			<ItemImage id="purple_floppy_disk" />
			<ItemImage id="magenta_floppy_disk" />
			<ItemImage id="pink_floppy_disk" />
			<ItemImage id="brown_floppy_disk" />
		</Row>
		<Row gap="1">
			<ItemImage id="white_floppy_disk" />
			<ItemImage id="light_gray_floppy_disk" />
			<ItemImage id="gray_floppy_disk" />
			<ItemImage id="black_floppy_disk" />
		</Row>
	</PaddedBox>
</FloatingColumn>

Disquetes permitem que você armazene o programa em um [Microchip](microchips.md) e o instale em outro microchip
com facilidade.

Pressionando **<KeyBind id="key.sneak" />** + **<KeyBind id="key.use" />** em um microchip enquanto segura um disquete,
ele armazenará o programa do microchip no disquete.

Depois que um programa for armazenado no disquete, você pode pressionar **<KeyBind id="key.use" />** em um microchip para instalá-lo,
desde que você tenha os componentes lógicos e os bits de redstone necessários. Os itens em [Matrizes Lógicas](logic_arrays.md)
contam como itens em seu inventário para fins de instalação de programas. Os itens necessários para instalar o programa serão
exibidos acima da barra de acesso rápido ao olhar para um microchip com um disquete na mão.

Os programas podem ser salvos e carregados de e para o seu computador local também! Abra o menu para isso pressionando
**<KeyBind id="key.use" />** enquanto segura um disquete (e não estiver olhando para um microchip). O botão "Salvar" salvará
o programa atualmente armazenado em um arquivo com o nome que você fornecer na caixa de texto "Nome do Programa". Da mesma forma, o botão
"Carregar" carregará o programa do arquivo com o nome fornecido. Se não houver um arquivo existente com este nome, o botão
"Carregar" não poderá ser clicado. Programas salvos são armazenados no diretório **/little_big_redstone/microchips**
do seu diretório do jogo. Esses arquivos são acessíveis entre mundos e servidores.