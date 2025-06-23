---
navigation:
  title: "Дискета"
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

# Дискета

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

Дискета позволяет легко сохранять схему с [Микрочипа](microchips.md), а затем программировать её на другой микрочип.

Нажав **<KeyBind id="key.sneak" />** + **<KeyBind id="key.use" />** на микрочипе, 
держа в руке дискету, вы сохраните схему микрочипа на дискету.

После сохранения схемы на дискету вы можете нажать **<KeyBind id="key.use" />** на микрочипе, чтобы установить её, при условии, что у вас есть необходимые логические компоненты и редстоун-биты. 
Предметы, находящиеся в [Логических матрицах](logic_arrays.md), учитываются как предметы в вашем инвентаре при установке схем. 
Необходимые для установки схемы предметы будут отображаться над панелью быстрого доступа, когда вы смотрите на микрочип, держа в руке дискету.