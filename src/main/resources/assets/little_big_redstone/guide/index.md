---
navigation:
  title: "Little Big Redstone"
  icon: "little_big_redstone:red_sticky_note"
---

# Little Big Redstone

This is a test

<MicrochipScene width="100" height="50" color="red">
    <Logic name="and1" x="10" y="10" type="and_gate" />
    <Logic name="and2" x="74" y="10" type="and_gate" />
    <Wire from="and1" from_port="0" to="and2" to_port="1" />
</MicrochipScene>