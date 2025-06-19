---
navigation:
  title: "Microchips"
  icon: "red_microchip"
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

<Row>
    <ItemImage id="white_microchip" scale="2" />
    <ItemImage id="light_gray_microchip" scale="2" />
    <ItemImage id="gray_microchip" scale="2" />
    <ItemImage id="black_microchip" scale="2" />
    <ItemImage id="brown_microchip" scale="2" />
    <ItemImage id="red_microchip" scale="2" />
    <ItemImage id="orange_microchip" scale="2" />
    <ItemImage id="yellow_microchip" scale="2" />
</Row>
<Row>
    <ItemImage id="lime_microchip" scale="2" />
    <ItemImage id="green_microchip" scale="2" />
    <ItemImage id="cyan_microchip" scale="2" />
    <ItemImage id="light_blue_microchip" scale="2" />
    <ItemImage id="blue_microchip" scale="2" />
    <ItemImage id="purple_microchip" scale="2" />
    <ItemImage id="magenta_microchip" scale="2" />
    <ItemImage id="pink_microchip" scale="2" />
</Row>

<RecipeFor id="red_microchip" />

Microchips are blocks that can have [logic](logic.md) placed inside of it to create complex systems.
[Wires](redstone_bits.md) can be placed between logic to allow signals to be carried from one logic component to
another.

<MicrochipScene color="red" includeToolbar={true}>
    <Logic name="1" x="0" y="0" type="not_gate" />
    <Logic name="2" x="32" y="0" type="sequencer" data="{config:{auto_reset:true}}" />
    <Logic name="3" x="80" y="0" type="io" data="{config:{input:false}}" />
    <Wire from="1" fromPort="0" to="2" toPort="0" />
    <Wire from="2" fromPort="0" to="3" toPort="0" />
</MicrochipScene>