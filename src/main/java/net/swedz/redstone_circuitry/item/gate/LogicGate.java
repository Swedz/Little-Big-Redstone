package net.swedz.redstone_circuitry.item.gate;

public interface LogicGate
{
	LogicGate AND  = new ANDGate();
	LogicGate NAND = new NANDGate();
	LogicGate NOR  = new NORGate();
	LogicGate NOT  = new NOTGate();
	LogicGate OR   = new ORGate();
	
	int inputCount();
	
	boolean process(boolean[] inputs);
}
