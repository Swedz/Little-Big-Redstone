---
navigation:
  title: "Little Big Redstone"
  icon: "little_big_redstone:red_sticky_note"
---

# Little Big Redstone

This is a test

<MicrochipScene width="100" height="50" color="red">
    <Logic name="1" x="10" y="10" type="not_gate" />
    <Logic name="2" x="58" y="10" type="sequencer" />
    <Wire from="1" from_port="0" to="2" to_port="0" />
</MicrochipScene>