package clone;

import static clone.Utils.binary2Int;
import static clone.Utils.int2Binary;

public class Client {
  private static final boolean[][] table = {
          {false, false},
          {false, true},
          {true, false},
          {true, true}
  };
  private static final boolean[][] table3bits = {
          {false, false, false},
          {false, false, true},
          {false, true, false},
          {false, true, true},
          {true, false, false},
          {true, false, true},
          {true, true, false},
          {true, true, true},
  };

  public static void testAnd() {
    And and = new And();
    System.out.println();
    System.out.println("name : " + and.getName());
    boolean[] expectedResultAnd = {false, false, false, true};
    for (int i = 0; i < 4; i++) {
      and.setInput(0, table[i][0]);
      and.setInput(1, table[i][1]);
      and.process();
      System.out.println("and(" + table[i][0] + "," + table[i][1] + ") = " + and.isOutput());
      assert and.isOutput() == expectedResultAnd[i];
    }
  }

  public static void testOr() {
    Or or = new Or();
    System.out.println();
    System.out.println("name : " + or.getName());
    boolean[] expectedResultOr = {false, true, true, true};
    for (int i = 0; i < 4; i++) {
      or.setInput(0, table[i][0]);
      or.setInput(1, table[i][1]);
      or.process();
      System.out.println("or(" + table[i][0] + "," + table[i][1] + ") = " + or.isOutput());
      assert or.isOutput() == expectedResultOr[i];
    }
  }

  public static void testNot() {
    Not not = new Not();
    System.out.println();
    System.out.println("name : " + not.getName());
    not.setStateInput(true);
    not.process();
    System.out.println("not(" + true + ") = " + not.isOutput());
    assert not.isOutput() == false;
    not.setStateInput(false);
    not.process();
    System.out.println("not(" + false + ") = " + not.isOutput());
    assert not.isOutput() == true;
  }

  private static Component makeXor() {
    Component xor = new Component("xor", 2, 1);
    System.out.println();
    Or or = new Or("or");
    And and1 = new And("and1");
    Not not = new Not("not");
    And and2 = new And("and2");
    // Important : order matters for correct simulation
    xor.addCircuit(or);
    xor.addCircuit(and1);
    xor.addCircuit(not);
    xor.addCircuit(and2);
    // order matters also here, "left to right" of the circuit as drawn in the slides
    Connection[] connections = new Connection[8];
    connections[0] = new Connection(xor.getPinInput(0), and1.getPinInput(0));
    connections[1] = new Connection(xor.getPinInput(0), or.getPinInput(0));
    connections[2] = new Connection(xor.getPinInput(1), and1.getPinInput(1));
    connections[3] = new Connection(xor.getPinInput(1), or.getPinInput(1));
    connections[4] = new Connection(or.getPinOutput(0), and2.getPinInput(0));
    connections[5] = new Connection(and1.getPinOutput(0), not.getPinInput(0));
    connections[6] = new Connection(not.getPinOutput(0), and2.getPinInput(1));
    connections[7] = new Connection(and2.getPinOutput(0), xor.getPinOutput(0));
    for (Connection c : connections) {
      xor.addConnection(c);
    }
    return xor;
  }

  private static void testXor(Component xor) {
    System.out.println();
    System.out.println("name : " + xor.getName());
    boolean[] expectedResult = {false, true, true, false};
    for (int i = 0; i < 4; i++) {
      xor.setInput(0, table[i][0]);
      xor.setInput(1, table[i][1]);
      xor.process();
      System.out.println("xor(" + table[i][0] + "," + table[i][1] + ") = " + xor.isOutput());
      assert xor.isOutput() == expectedResult[i];
    }
  }

  // makes a 1 bit adder as if was done from the user interface, that is, creating an Xor in the same way,
  // then clone the Xor and make and connect the remaining gates, three Ands and one Or.
  private static Component makeOneBitAdder() {
    Component oneBitAdder = new Component("OneBitAdder", 3, 2);
    // three inputs (bit1, bit2, previous carry) and two outputs (sum and new carry)
    // sum = (bit1 xor1 bit2) xor2 carry in
    // new carry = (output of xor1 and carry) or (bit1 and bit2)
    Circuit xor1 = makeXor();
    Circuit xor2 = xor1.clone();
    And and1 = new And("and1");
    And and2 = and1.clone();
    Or or = new Or("or", 3);
    // this order matters for the simulation
    oneBitAdder.addCircuit(xor1);
    oneBitAdder.addCircuit(xor2);
    oneBitAdder.addCircuit(and1);
    oneBitAdder.addCircuit(and2);
    oneBitAdder.addCircuit(or);
    // connections "left to right"
    Pin bit1 = oneBitAdder.getPinInput(0);
    Pin bit2 = oneBitAdder.getPinInput(1);
    Pin carryIn = oneBitAdder.getPinInput(2);
    Pin input1Xor1 = xor1.getPinInput(0);
    Pin input2Xor1 = xor1.getPinInput(1);
    Pin outputXor1 = xor1.getPinOutput(0);
    Pin input1Xor2 = xor2.getPinInput(0);
    Pin input2Xor2 = xor2.getPinInput(1);
    Pin outputXor2 = xor2.getPinOutput(0);
    Pin input1And1 = and1.getPinInput(0);
    Pin input2And1 = and1.getPinInput(1);
    Pin outputAnd1 = and1.getPinOutput(0);
    Pin input1And2 = and2.getPinInput(0);
    Pin input2And2 = and2.getPinInput(1);
    Pin outputAnd2 = and2.getPinOutput(0);
    Pin input1Or = or.getPinInput(0);
    Pin input2Or = or.getPinInput(1);
    Pin input3Or = or.getPinInput(2);
    Pin outputOr = or.getPinOutput(0);
    Pin sum = oneBitAdder.getPinOutput(0);
    Pin carryOut = oneBitAdder.getPinOutput(1);
    oneBitAdder.addConnection(new Connection(bit1, input1Xor1));
    oneBitAdder.addConnection(new Connection(bit2, input2Xor1));
    oneBitAdder.addConnection(new Connection(outputXor1, input1Xor2));
    oneBitAdder.addConnection(new Connection(carryIn, input2Xor2));
    oneBitAdder.addConnection(new Connection(outputXor1, input1And1));
    oneBitAdder.addConnection(new Connection(carryIn, input2And1));
    oneBitAdder.addConnection(new Connection(bit1, input1And2));
    oneBitAdder.addConnection(new Connection(bit2, input2And2));
    oneBitAdder.addConnection(new Connection(outputAnd1, input1Or));
    oneBitAdder.addConnection(new Connection(outputAnd2, input2Or));
    oneBitAdder.addConnection(new Connection(outputXor2, sum));
    oneBitAdder.addConnection(new Connection(outputOr, carryOut));
    return oneBitAdder;
  }

  private static void testOneBitAdder() {
    Component oneBitAdder = makeOneBitAdder();
    System.out.println();
    System.out.println("name : " + oneBitAdder.getName());
    boolean[] expectedResultSum = {false, true, true, false, true, false, false, true};
    boolean[] expectedResultCarryOut = {false, false, false, true, false, true, true, true};
    for (int i = 0; i < 8; i++) {
      boolean bit1 = table3bits[i][0];
      boolean bit2 = table3bits[i][1];
      boolean carryIn = table3bits[i][2];
      oneBitAdder.setInput(0, bit1);
      oneBitAdder.setInput(1, bit2);
      oneBitAdder.setInput(2, carryIn);
      oneBitAdder.process();
      boolean sum = oneBitAdder.isOutput(0); //getPinOutput(0).isState();
      boolean carryOut = oneBitAdder.isOutput(1); //getPinOutput(1).isState();
      System.out.println("oneBitAdder(" + bit1 + "," + bit2 + "," + carryIn + ") = "
              + sum + ", " + carryOut);
      assert sum == expectedResultSum[i];
      assert carryOut == expectedResultCarryOut[i];
    }
  }


  private static Component makeFourBitsAdder() {
    Component fourBitsAdder = new Component("4bitsAdder", 8, 5);
    Component oneBitAdder1 = makeOneBitAdder();
    Component oneBitAdder2 = oneBitAdder1.clone();
    Component oneBitAdder3 = oneBitAdder1.clone();
    Component oneBitAdder4 = oneBitAdder1.clone();
    fourBitsAdder.addCircuit(oneBitAdder1);
    fourBitsAdder.addCircuit(oneBitAdder2);
    fourBitsAdder.addCircuit(oneBitAdder3);
    fourBitsAdder.addCircuit(oneBitAdder4);

    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(0), oneBitAdder1.getPinInput(0)));
    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(4), oneBitAdder1.getPinInput(1)));
    oneBitAdder1.setInput(2,false);
    fourBitsAdder.addConnection( new Connection(oneBitAdder1.getPinOutput(0), fourBitsAdder.getPinOutput(0)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder1.getPinOutput(1), oneBitAdder2.getPinInput(2)));

    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(1), oneBitAdder2.getPinInput(0)));
    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(5), oneBitAdder2.getPinInput(1)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder2.getPinOutput(0), fourBitsAdder.getPinOutput(1)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder2.getPinOutput(1), oneBitAdder3.getPinInput(2)));

    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(2), oneBitAdder3.getPinInput(0)));
    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(6), oneBitAdder3.getPinInput(1)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder3.getPinOutput(0), fourBitsAdder.getPinOutput(2)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder3.getPinOutput(1), oneBitAdder4.getPinInput(2)));

    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(3), oneBitAdder4.getPinInput(0)));
    fourBitsAdder.addConnection( new Connection(fourBitsAdder.getPinInput(7), oneBitAdder4.getPinInput(1)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder4.getPinOutput(0), fourBitsAdder.getPinOutput(3)));
    fourBitsAdder.addConnection( new Connection(oneBitAdder4.getPinOutput(1), fourBitsAdder.getPinOutput(4)));
    // carry out of last adder is the most significant output bit
    return fourBitsAdder;
  }

  public static void testFourBitsAdder() {
    Component fourBitsAdder = makeFourBitsAdder();
    System.out.println();
    System.out.println("name : " + fourBitsAdder.getName());
    for (int i=0 ; i<16 ; i++) {
      boolean[] num1 = int2Binary(i, 4);
      for (int j=0 ; j<16 ; j++) {
        boolean[] num2 = int2Binary(j, 4);
        for (int nbit = 0; nbit < 4 ; nbit++) {
          fourBitsAdder.setInput(nbit, num1[4-nbit-1]);
          fourBitsAdder.setInput(nbit+4, num2[4-nbit-1]);
        }
        fourBitsAdder.process();
        boolean[] result = new boolean[5];
        for (int nbit=0; nbit < 5 ; nbit++) {
          result[5-nbit-1] = fourBitsAdder.isOutput(nbit);
        }
        int resultDecimal = binary2Int(result);
        System.out.println(binary2Int(num1) + " + " + binary2Int(num2) + " = " + resultDecimal);
      }
    }
  }


  public static void main(String[] args) {
    testAnd();
    testOr();
    testNot();

	Component xor = makeXor();
    testXor(xor);

	Component clonedXor = xor.clone();
    testXor(clonedXor);

    testOneBitAdder();
    // clones an Xor and an And

    testFourBitsAdder();
    // clones a oneBitAdders to make an 4-bits adder
  }
}