---
navigation:
  title: "Redstone Bits"
  icon: "redstone_bit"
item_ids:
  - little_big_redstone:redstone_bit
---

# Redstone Bits

<ItemImage id="redstone_bit" scale="2" />

<RecipeFor id="redstone_bit" />

Redstone bits are used to create wires that connect between ports. Ports refer to the triangular protrusions on the
side of [logic](logic.md). Output ports are always on the right side of logic, and input ports are always on the left
side of logic. Wires can be created by left-clicking an output port, and then left-clicking the desired input port.
Each wire costs exactly one redstone bit, and the bit will only be consumed once the wire has been connected to the
input port. A held wire can be discarded by right-clicking. Wires can also be picked up after they have been placed
using left-click.

Below is an example of a NOT gate's output connecting to the first input of an OR gate.

<MicrochipScene color="red">
    <Logic name="1" x="0" y="0" type="not_gate" />
    <Logic name="2" x="32" y="0" type="or_gate" />
    <Wire from="1" fromPort="0" to="2" toPort="0" />
</MicrochipScene>

Where normally redstone may have a signal strength of 0 to 15, redstone bits only have a signal strength of 0 or 1. In
simpler terms, a wire may only be on or off - it is a strictly boolean system.