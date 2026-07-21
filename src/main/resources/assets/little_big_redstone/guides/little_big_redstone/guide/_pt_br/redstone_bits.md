---
navigation:
  title: "Bits de Redstone"
  icon: "redstone_bit"
  position: 2
item_ids:
  - little_big_redstone:redstone_bit
---

# Bits de Redstone

<FloatingColumn align="right">
	<PaddedBox left="5">
		<RecipeFor id="redstone_bit" />
	</PaddedBox>
</FloatingColumn>

<FloatingColumn>
	<ItemImage id="redstone_bit" scale="2" />
</FloatingColumn>

Bits de Redstone são usados para criar fios que se conectam entre portas. Portas referem-se às saliências triangulares na
lateral da [lógica](logic/introduction.md). As portas de saída estão sempre no lado direito da lógica, e as portas de entrada estão sempre
no lado esquerdo da lógica.

Cada porta de entrada pode ter exatamente um fio conectado a ela. Quanto às portas de saída, não há limite de quantos fios
podem sair delas. Se você precisar combinar múltiplos fios em uma porta de entrada, use uma [Porta OR](logic/or_gate.md).

Onde normalmente a redstone pode ter uma intensidade de sinal de 0 a 15, os bits de redstone têm apenas uma intensidade de sinal de 0 ou 1. Em
termos mais simples, um fio pode estar apenas ligado ou desligado — é um sistema estritamente booleano.

### Trabalhando com fios

Fios podem ser criados clicando com o botão esquerdo em uma porta de saída e, em seguida, clicando com o botão esquerdo na porta de entrada desejada. Um fio sendo
segurado pode ser descartado clicando com o botão direito. Os fios também podem ser recolhidos depois de colocados, usando o botão esquerdo.

Cada fio custa exatamente um bit de redstone, e o bit só será consumido depois que o fio for conectado à
porta de entrada.