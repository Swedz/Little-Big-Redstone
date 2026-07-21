---
navigation:
  title: "Introdução"
  parent: little_big_redstone:logic.md
  position: 0
---

# Introdução à Lógica

<FloatingColumn align="right">
	### Índice
	<LogicIndex />
</FloatingColumn>

Componentes lógicos, ou o que às vezes também são chamados de portas lógicas, são o que impulsiona o seu circuito. Todos os componentes lógicos 
terão portas de entrada ou de saída. Essas portas são a forma como você transmite e modifica sinais para obter os resultados
que deseja. Componentes lógicos se comportam puramente em uma base booleana - o que significa que os sinais podem ter um valor
DESLIGADO (0) ou LIGADO (1).

<br />

### Tabelas-verdade

A lógica se comportará de maneira diferente dependendo de seus sinais de entrada, tipicamente de forma determinística. Assim, a maioria dos componentes lógicos 
têm o que é chamado de tabela-verdade associada a eles. Uma tabela-verdade é uma tabela que mostra o estado de saída
para cada combinação de entradas para o componente lógico em questão. A página correspondente para cada componente lógico
que pode ser descrito usando uma tabela-verdade terá uma presente nela.

As entradas são representadas por letras em ordem sequencial. Por exemplo, a primeira entrada seria A, a segunda B, e assim
por diante. As saídas são representadas por Q. Se houver múltiplas saídas, o Q é seguido por subscrito denotando
a saída específica. Por exemplo, Q₁, Q₂, e assim por diante. No entanto, na maioria dos casos haverá apenas uma saída para uma porta.

<br />

### Usando a Lógica

A lógica pode ser colocada ou retirada na interface de um [Microchip](../microchips.md) usando o clique esquerdo. Enquanto segura a lógica em seu
cursor, você pode segurar a tecla CTRL para ajustar seu posicionamento a uma grade.

Para configurar um componente lógico (se houver algo para configurar), você pode usar o clique direito nele. Isso abrirá um
menu onde você pode alterar as configurações do componente. As configurações de cada componente são descritas em sua
página correspondente.

Para obter informações sobre como conectar a lógica usando suas portas, veja a página sobre [fios](../redstone_bits.md).

Componentes lógicos podem ser tingidos separadamente do microchip, mas por padrão herdarão a cor do microchip
no qual são colocados. Para tingir componentes lógicos, você pode fazê-lo em sua grade de criação, como faria normalmente, ou você
pode clicar com o botão direito na lógica no menu com o corante para aplicá-lo.

Similarmente, você pode usar um balde de água ou bolas de neve para limpar a cor do componente lógico. Note que as bolas de neve
serão consumidas, enquanto os baldes de água não serão.