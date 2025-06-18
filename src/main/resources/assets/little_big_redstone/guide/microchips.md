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

<MicrochipScene width="250" height="100" color="red">
    <Logic name="1" x="10" y="10" type="not_gate" />
    <Logic name="2" x="58" y="10" type="sequencer" />
    <Wire from="1" from_port="0" to="2" to_port="0" />
</MicrochipScene>