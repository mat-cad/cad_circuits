---
marp: true
paginate: true
size: 16:9
---

CAD of circuits
===

- a complete design and implementation example 
- with composite and observer 
- with [Java and Python implementations](https://github.com/disseny-de-software/cad_circuits)

![bg right:50%](figures/binary_addition.png)

---

Goal
===

Design and implement a Java application to

- design logic circuits 
- based on logic gates AND, OR, NOT (for the moment) as building atoms
- once designed, one can simulate = compute output for a certain input

Learning goals:
- example of two patterns combined
- show code implementing the design
- illustrate the process: cycle of a little design, a little code, a little testing...


---

![height:500](figures/jhu.png)

https://ei.jhu.edu/logic-circuit-simulator/

---

![height:500](figures/academo.png)

https://academo.org/demos/logic-gate-simulator/

---

![height:500](figures/science_demos.png)

https://sciencedemos.org.uk/logic_gates.php

---

**Key difference** : we want to allow **grouping of gates and / or groups** to make a new element by copy-paste as if it was a simple gate.

![height:550](figures/copy_paste_xor.png)

---

Why ? To make complex circuits by making groups = hierarchy of abstractions, and copy-pasting them :

![height:150](figures/xor.png)  ![height:70](figures/xor2.jpeg) ![height:200](figures/one_bit_full_adder.jpeg) ![](figures/one_bit_full_adder_box.png) ![](figures/4bitsadder.png)

---

However, **no graphical user interface** for the moment. We will replace user interactions by client code. 

The user interaction is *hardcoded* in the programming sentences.


For instance, making a unique And gate and simulating it is like:

---

Java
```java
And and = new And("my and"); // two inputs, one output
System.out.println("name : " + and.getName());
boolean input1 = true;
boolean input2 = false;
and.setInput(0, input1);
and.setInput(1, input2);
and.process();
System.out.println("and(" + input1 + "," + input2 + ") = " + and.isOutput());
```

Python
```python
and1 = And("my and") # two inputs, one output
input1 = True
input2 = False
and1.set_input(0, input1)
and1.set_input(1, input2)
and1.process()
print('{} : {} and {} = {}'.format(and1.name, input1, input2, and1.output))
```

---

For simplicity, we **won’t allow loops** as in a flip-flop :

![height:300](figures/flip-flop.png)

And also, the **order of creation** of the gates is the right one for the simulation (see later)

---

How to begin
===

1. detailed design to support instantiation of gates
1. and grouping gates and/or groups
1. to check it's right, write the code to create an xor from basic gates
1. test creation of an and, or, not
1. simulation = check that for all possible inputs the output is the one we expect
    - of each individual gate
    - of several gates making an xor

Design $\rightarrow$ program $\rightarrow$ test $\rightarrow$ more design $\rightarrow \ldots$

---

First design
===

![height:400](figures/model_simple1.png)

- any circuit has a name and you can process = simulate it
- ands, ors have one output and 2 (default) or more inputs
- a group as one or more inputs and outputs
- a group has one or more circuits inside

---

How's the code to make an xor ? 


![height:400](figures/xor.png)

---

Java

In a class ``Client``, 
```java
Component xor = new Component("xor", 2, 1); // 2 inputs, 1 output
Or or  = new Or();
And and1 = new And("and1");
Not not  = new Not();
And and2 = new And("and2");
// the order of adds matters to simulation
xor.addCircuit(or); // more readable than xor.getCircuits().add(or)
xor.addCircuit(and1);
xor.addCircuit(not);
xor.addCircuit(and2);
```

---


```java
public abstract class Circuit {
    protected final String name;
    public Circuit(String name) { this.name = name; }
```

```java
public class And extends Circuit {
    public And() { this("and"); }
    public And(String name) { this(name, 2); }
    public And(String name, int numInputs) {
        super(name);
        // what about numInputs ?
    }
    public void process() { /* what to do here ? */ }
}
```

```java
public class Component extends Circuit {
    private List<Circuit> circuits = new ArrayList<Circuit>();
    public Component(String name, int numInputs, int numOutputs) {
        super(name);
        // what about the other two parameters ?
     }
    public void addCircuit(Circuit circ) { circuits.add(circ); }
```

---

Python
```python
xor = Component("xor", 2, 1)
or1 = Or("or1")
and1 = And("and1")
not1 = Not("not1")
and2 = And("and2")
# the order of adds will matter to simulation
xor.add_circuit(or1) # more readable than xor.circuits.append(or)
xor.add_circuit(and1)
xor.add_circuit(not1)
xor.add_circuit(and2)
```

---

```python
from abc import ABC

class Circuit(ABC):
    def __init__(self, name):
        self.name = name

    def process(self):
        raise NotImplementedError
        # can not call process(), it's an abstract method

class And(Circuit):
    def __init__(self, name, num_inputs=2):
        super().__init__(name)
        self.inputs = [None]*num_inputs
        self.output = None

    def process(self):
        pass # TODO
```

---

```python
class Component(Circuit):
    def __init__(self, name, num_inputs, num_outputs):
        super().__init__(name)
        self.inputs = [None] * num_inputs
        self.output = [None] * num_outputs
        self.circuits = []

    def add_circuit(self, circuit):
        self.circuits.append(circuit)

    def process(self):
        pass # TODO        
```

---

Gates and also components, i.e circuits, have **input and output pins**. 

![height:400](figures/chip_ands.jpeg)

A pin has an **state**, ``true`` or ``false``.

---

<font style="color:green;"><b>Connections</b></font> are wires that connect two <font style="color:red;"><b>pins</b></font> in order to **pass logical state** during circuit simulation. 

Are all combinations of pin connections Input-Input, Input-Output, O-I, O-O possible connections ?

![](figures/how_to_connect.png)


---

A circuit has 1 or more inputs. But a **unique output ?** No, a 1 bit full adder has 
- 3 inputs : bit1, bit2, carry in
- 2 outputs: sum bit and carry out

![height:300](figures/one_bit_full_adder.jpeg) ![height:300](figures/one_bit_full_adder_box.png)

---

Since circuits have one or more input and output pins...

![width:1200](figures/model_simple2.png)

---

``PinInput`` and ``PinOutput`` are the same, pins, the only difference is their role wrt. the circuit $\rightarrow$ redundancy.

![width:1200](figures/model_simple3.png)

- who makes the pins of a circuit ? and how many ?
- who makes the connections ? 
- how and when do they pass state from one pin to another ?

---
Java 
```java
public abstract class Circuit {
    protected final String name;
    protected List<Pin> inputs = new ArrayList<Pin>();
    protected List<Pin> outputs = new ArrayList<Pin>();
    
    protected void addOutput(Pin pin) { outputs.add(pin); }
    public void setOutput(boolean state) { this.getPinOutput(0).setState(state); }
    public boolean isOutput(int numPin) { return outputs.get(numPin).isState(); }
    public boolean isOutput() { return isOutput(0); } // same for inputs
    public abstract void process();
```

```java
public class And extends Circuit {
    public And(String name, int numInputs) { // 2 or more inputs
        super(name);
        assert numInputs >= 2 : "invalid number of inputs for an And";
        for (int i = 1; i <= numInputs; i++) {
            addInput(new Pin("input " + i));
        }
        addOutput(new Pin("output"));
    }
    public And(String name) { this(name, 2); } // 2 inputs
    public And() { this("and"); } // 2 inputs
```

---

```java
public class Component extends Circuit {
    private List<Circuit> circuits = new ArrayList<Circuit>();

    public void addCircuit(Circuit circ) { circuits.add(circ); }

    public Component(String name, int numInputs, int numOutputs) {
        super(name);
        assert numInputs >= 1 
            : "invalid number of inputs for a Component";
        assert numOutputs >= 1 
            : "invalid number of outputs for a Component";

        for (int i = 1; i <= numInputs; i++) { 
            addInput(new Pin("input " + i)); 
        }
        for (int i = 1; i <= numOutputs; i++) { 
            addOutput(new Pin("output " + i)); 
        }
    }
```

---
Python
```python
from abc import ABC

class Pin():
    def __init__(self, name):
        self.name = name
        self.state = None

class Circuit(ABC):
    def __init__(self, name, num_inputs, num_outputs):
        self.name = name
        self.inputs = [Pin('input {} of {}'.format(i + 1, name)) 
                       for i in range(num_inputs)]
        self.outputs = [Pin('output {} of {}'.format(i + 1, name)) 
                        for i in range(num_outputs)]

    def process(self):
        raise NotImplementedError
        # can not call process(), it's an abstract method
```

---

```python
class And(Circuit):
    def __init__(self, name, num_inputs=2):
        super().__init__(name, num_inputs, 1)

    def process(self):
        # TODO: not evident when we have multiple gates and components
        #  in a circuit because we have to the propagate output
        pass

class Component(Circuit):
    def __init__(self, name, num_inputs, num_outputs):
        super().__init__(name, num_inputs, num_outputs)
        self.circuits = []

    def add_circuit(self, circuit):
        self.circuits.append(circuit)

    def process(self):
        # TODO
        pass
```

---

Back to creation of an xor :

```java
Component xor = new Component("xor", 2, 1); // 2 inputs, 1 output
Or or  = new Or();
And and1 = new And("and1");
Not not  = new Not();
And and2 = new And("and2");
xor.addCircuit(or); // more readable than xor.getCircuits().add(or)
xor.addCircuit(and1);
xor.addCircuit(not);
xor.addCircuit(and2);
```

```python
xor = Component('xor', 2, 1)
or1 = Or('or1')
and1 = And('and1')
not1 = Not('not1')
and2 = And('and2')
xor.add_circuit(or1) # more readable than xor.circuits.append(or)
xor.add_circuit(and1)
xor.add_circuit(not1)
xor.add_circuit(and2)
```

---

Now logical gates and components (groups) have pins.

But **we are not yet connecting** the pins of the and1, and2, or, not and xor objects.

---

Which are the connections to make a xor ?

![height:250](figures/xor.png)

![height:300](figures/conexions_xor.png)

---

What’s the purpose of connecting input and output pins ? What should a connection object do ?

- pass (copy) the state of the origin pin to the destination pin *or pins* connected to it
- do it every time the state of the origin pin has changed
- therefore, each time ``setState(aState)`` of the origin pin is invoked

How to implement this behavior ?

---

![bg right height:400](figures/model_simple4.png)

- make ``Pin`` both observable and observer

- a ``Pin`` object may observe another ``Pin`` object. When the later changes, the former updates itself accordingly =
copies the state of the observed pin

- a pin may have several pin observers (eg. input 1 to xor goes to and1 and or)

- we are not avoiding any class dependency but reusing an already implemented behaviour

---
Java
```java
import java.util.Observable;
import java.util.Observer;

public class Pin extends Observable implements Observer {
    private String name;
    private boolean state;
    public Pin(String name) {
        this.name = name;
        clearChanged(); // from Observable : cleans this flag
    }
    public boolean isState() { return state; }
    
    // A destination pin updates itself copying the state 
    // of the origin pin to which it is connected
    public void update(Observable arg0, Object arg1) { 
        setState(((Pin) arg0).isState());
    }
    public void setState(boolean state) {
        this.state = state;
        setChanged();
        notifyObservers(this); // the observed sends itself to its observers
    }
}
```

---

Python (no ``Observable``, ``Observer`` like in Java)
```python
class Observable(ABC):
    def __init__(self):
        self.observers = []

    def add_observer(self, observer):
        self.observers.append(observer)

    def remove_observer(self, observer):
        pass # TODO

    def notify_observers(self, an_object=None):
        for obs in self.observers:
            obs.update(self, an_object)
            # observable sends itself to each observer

class Observer(ABC):
    def update(self, observable, an_object):
        raise NotImplementedError
        # abstract method
```

---

```python
class Pin(Observable, Observer):
    def __init__(self, name):
        super().__init__()
        self.name = name
        self.state = None

    def is_state(self):
        return self.state

    def set_state(self, new_state):
        self.state = new_state
        self.notify_observers(self)

    def update(self, observed_pin, an_object):
        self.set_state(observed_pin.is_state())
```

---

Again, what should a connection object do ?

Connect two pins = make one observe the other from that moment on.

```java
public class Connection {
    public Connection(Pin pinFrom, Pin pinTo) {
        pinFrom.addObserver(pinTo);
    }
}
```

```python
class Connection:
    def __init__(self, pin_from, pin_to):
        pin_from.add_observer(pin_to)
``` 

This is the whole Connection class. A connection doesn't keep reference to its pins.

Simple because we are not yet implementing disconnections nor copy-paste of a group, which requires copying also connections.

---

Java
```java
Component xor = new Component("xor",2, 1);
Or or  = new Or();
And and1 = new And("and1");
Not not  = new Not();
And and2 = new And("and2");
// order matters to simulation
xor.addCircuit(or);
xor.addCircuit(and1);
xor.addCircuit(not);
xor.addCircuit(and2);
// order matters also to simulation
new Connection(xor.getPinInput(0), and1.getPinInput(0));
new Connection(xor.getPinInput(0),   or.getPinInput(0));
new Connection(xor.getPinInput(1), and1.getPinInput(1));
new Connection(xor.getPinInput(1),   or.getPinInput(1));
new Connection(or.getPinOutput(0)  , and2.getPinInput(0));
new Connection(and1.getPinOutput(0),  not.getPinInput(0));
new Connection(not.getPinOutput(0),  and2.getPinInput(1));
new Connection(and2.getPinOutput(0), xor.getPinOutput(0));
```

---

Python
```python
xor = Component('xor', 2, 1)
or1 = Or('or1')
and1 = And('and1')
not1 = Not('not1')
and2 = And('and2')
# the order of adds will matter to simulation
xor.add_circuit(or1) 
xor.add_circuit(and1)
xor.add_circuit(not1)
xor.add_circuit(and2)
# the order will matter to simulation
Connection(xor.inputs[0], and1.inputs[0])
Connection(xor.inputs[0], or1.inputs[0])
Connection(xor.inputs[1], and1.inputs[1])
Connection(xor.inputs[1], or1.inputs[1])
Connection(or1.outputs[0], and2.inputs[0])
Connection(and1.outputs[0], not1.inputs[0])
Connection(not1.outputs[0], and2.inputs[1])
Connection(and2.outputs[0], xor.outputs[0])
```

---

### What is in ``process()`` ?

```java
public class Not extends Circuit {
    public void process() {
        setOutput(!isStateInput());
    }
```
```java
public class And extends Circuit {
    public void process() {
        boolean result = true ;
        for (Pin pinInput : inputs) {
            result = result && pinInput.isState();
        }
        setOutput(result);
    }
```
```java
public class Component extends Circuit {
    private List<Circuit> circuits = new ArrayList<Circuit>();
    public void process() {
        for (Circuit circ : circuits) { circ.process(); }
    } // this is why the order of circuits matters
```

---

Python
```python
class Not(Circuit):
    def __init__(self, name):
        super().__init__(name, 1, 1)

    def process(self):
        self.outputs[0].set_state(not self.inputs[0].is_state())

class And(Circuit):
    def __init__(self, name, num_inputs=2):
        super().__init__(name, num_inputs, 1)

    def process(self):
        result = True
        for pin_input in self.inputs:
            result = result and pin_input.is_state()
        self.outputs[0].set_state(result)
```

---

```python
class Component(Circuit):
    def __init__(self, name, num_inputs, num_outputs):
        super().__init__(name, num_inputs, num_outputs)
        self.circuits = []

    def add_circuit(self, circuit):
        self.circuits.append(circuit)

    def process(self):
        for circuit in self.circuits:
            circuit.process()
```

---

## complete first<br>design

![bg height:700](figures/model_simple.png)

---

Test : does it work ?
===

Java
```java
public class Client {
    private static final boolean[][] inputs = {
        {false,false}, {false,true}, {true,false}, {true,true}};

    public static void testAnd() {
        And and = new And();
        System.out.println("name : " + and.getName());
        boolean[] expectedOutput = {false, false, false, true};
        for(int i=0; i<4; i++) {
            and.setInput(0, inputs[i][0]);
            and.setInput(1, inputs[i][1]);
            and.process();
            System.out.println("and(" + inputs[i][0] +"," + inputs[i][1] + ") = " 
                + and.isOutput());
            assert and.isOutput() == expectedOutput[i];
        }
    }
```

---

```java
private static void testXor() {
    Component xor = makeXor();
    System.out.println("name : " + xor.getName());
    boolean[] expectedOutput = {false, true, true, false};
    for(int i=0; i<4; i++) {
        xor.setInput(0, inputs[i][0]);
        xor.setInput(1, inputs[i][1]);
        xor.process();
        System.out.println("xor(" + inputs[i][0] +"," + inputs[i][1] + ") = " 
            + xor.isOutput());
        assert xor.isOutput()==expectedOutput[i];
    }
}
```

---

```text
name : and
and(false,false) = false
and(false,true) = false
and(true,false) = false
and(true,true) = true
name : xor
xor(false,false) = false
xor(false,true) = true
xor(true,false) = true
xor(true,true) = false
```

---

### how to make ``assert`` work (Java only)

![](figures/asserts.png)

``-ea`` = enable assertions, otherwise they are like comments

---

Python
```python
print('\nTest of And')
inputs = [[False, False], [False, True], [True, False], [True, True]]
expected_outputs = [False, False, False, True]

for (input1, input2), expected_output in zip(inputs, expected_outputs):
    and1.set_input(0, input1)
    and1.set_input(1, input2)
    and1.process()
    output = and1.outputs[0].is_state()
    print('{} AND {} = {}'.format(input1, input2, output))
    assert output == expected_output

print('\nTest of xor')
expected_outputs = [False, True, True, False]
for (input1, input2), expected_output in zip(inputs, expected_outputs):
    xor.set_input(0, input1)
    xor.set_input(1, input2)
    xor.process()
    output = xor.outputs[0].is_state()
    print('{} XOR {} = {}'.format(input1, input2, output))
    assert output == expected_output
```

---

```text
Test of And
False AND False = False
False AND True = False
True AND False = False
True AND True = True

Test of xor
False XOR False = False
False XOR True = True
True XOR False = True
True XOR True = False
```

---

Second desing, Java version
===

We have not yet implemented the functionality of "copy-paste" of gates and components.

Let's make a **one bit full adder** $\rightarrow$ copy-paste of an xor.

![height:300](figures/one_bit_full_adder.jpeg) ![height:300](figures/one_bit_full_adder_box.png)

---

We want to be able to write the hardcoded version of user interface copy-paste :

```java
Component oneBitAdder = new Component("OneBitAdder", 3, 2);
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
// make many connections between input and output pins of the 6 circuits
//...
```

``clone()`` returns a copy of a circuit : gate + its pins, but if it is a component its pins, subcircuits and *its connections*.

Thus is the trickiest part, see ``clone()`` of ``Component`` class.

---

Every circuit can be cloneable (there is a ``Cloneable`` interface in Java)
```java
public abstract class Circuit implements Cloneable {
  //...
  @Override
  public abstract Circuit clone();
```

``clone()`` of ``And``, ``Or`` and ``Not`` area easy because these circuits have no connections to clone :


```java
@Override
public And clone() { return new And(name, this.inputs.size()); }
```

```java
@Override
public Or clone() { return new Or(name, this.inputs.size()); }
```

```java
@Override
public Not clone() { return new Not(name); }
```

---

Connections are made to create a group = ``Component``object. 

A ``Component``has to keep record of its connections in order to be able to clone them also.

Each connection has also to keep record of the two pins it connects.

A pin is either the $i$-th input or the $i$-th output of a circuit, and $i$ and whether input or output has to be found in order to clone the connection. See ``clone()`` of ``Component`` class.

---

```java
public class Component extends Circuit {
  private List<Connection> connections = new ArrayList<>();
  public List<Circuit> circuits = new ArrayList<Circuit>();
  //...
  public void addConnection(Connection c) {
      connections.add(c);
  }
```

---

```java
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
// order matters also here, "left to right" of the circuit
xor.addConnection( new Connection(xor.getPinInput(0), and1.getPinInput(0)) );
xor.addConnection( new Connection(xor.getPinInput(0), or.getPinInput(0)) );
xor.addConnection( new Connection(xor.getPinInput(1), and1.getPinInput(1)) );
xor.addConnection( new Connection(xor.getPinInput(1), or.getPinInput(1)) );
xor.addConnection( new Connection(or.getPinOutput(0), and2.getPinInput(0)) );
xor.addConnection( new Connection(and1.getPinOutput(0), not.getPinInput(0)) );
xor.addConnection( new Connection(not.getPinOutput(0), and2.getPinInput(1)) );
xor.addConnection( new Connection(and2.getPinOutput(0), xor.getPinOutput(0)) );
```

---

![](figures/model_clone.png)

---

## Test

```java
private static final boolean[][] table3bits = {
    {false, false, false}, {false, false, true}, {false, true, false}, {false, true, true},
    {true, false, false}, {true, false, true}, {true, true, false}, {true, true, true},
};

private static void testOneBitAdder() {
    Component oneBitAdder = makeOneBitAdder();
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
        boolean sum = oneBitAdder.isOutput(0);
        boolean carryOut = oneBitAdder.isOutput(1); 
        System.out.println("oneBitAdder(" + bit1 + "," + bit2 + "," + carryIn + ") = "
            + sum + ", " + carryOut);
        assert sum == expectedResultSum[i];
        assert carryOut == expectedResultCarryOut[i];
    }
}
```

---

We can continue with a 4-bits adder... see [source code](https://github.com/disseny-de-software/cad_circuits)

![](figures/4bitsadder.png)

```java
Component fourBitsAdder = new Component("4bitsAdder", 8, 5);
Component oneBitAdder1 = makeOneBitAdder();
Component oneBitAdder2 = oneBitAdder1.clone();
Component oneBitAdder3 = oneBitAdder1.clone();
Component oneBitAdder4 = oneBitAdder1.clone();
fourBitsAdder.addCircuit(oneBitAdder1);
//...
```

---

Second desing, Python version
===

We have not yet implemented the functionality of "copy-paste" of gates and components.

Let's make a **one bit full adder** $\rightarrow$ copy-paste of an xor.

![height:300](figures/one_bit_full_adder.jpeg) ![height:300](figures/one_bit_full_adder_box.png)

---

We want to be able to write the hardcoded version of user interface copy-paste.

```python
xor1 = Component('xor1', 2, 1)
or1 = Or('or1')
and1 = And('and1')
not1 = Not('not1')
and2 = And('and2')

xor1.add_circuit(or1) 
xor1.add_circuit(and1)
xor1.add_circuit(not1)
xor1.add_circuit(and2)

Connection(xor1.inputs[0], and1.inputs[0])
Connection(xor1.inputs[0], or1.inputs[0])
Connection(xor1.inputs[1], and1.inputs[1])
Connection(xor1.inputs[1], or1.inputs[1])
Connection(or1.outputs[0], and2.inputs[0])
Connection(and1.outputs[0], not1.inputs[0])
Connection(not1.outputs[0], and2.inputs[1])
Connection(and2.outputs[0], xor1.outputs[0])
```

![bg right:40% height:300](figures/conexions_xor_amb_xor.png)

---

Making now the one bit full adder is easy thanks to Python's  ``deepcopy``, read [Shallow vs Deep Copying](https://realpython.com/copying-python-objects/)

```python
import copy # has deepcopy

oneBitAdder = Component("OneBitAdder", 3, 2)
xor2 = copy.deepcopy(xor1)
xor2.name = 'xor2'
and3 = And('and3')
and4 = And('and4') # or copy.deepcopy(and3) and rename
or2 = Or('or2');
# this order matters for the simulation
oneBitAdder.add_circuit(xor1)
oneBitAdder.add_circuit(xor2)
oneBitAdder.add_circuit(and3)
oneBitAdder.add_circuit(and4)
oneBitAdder.add_circuit(or2)
```

![bg right:30% height:400](figures/one_bit_full_adder_sense_conexions.png)

---

Get a reference to the pins of each gate/component
```python
A = oneBitAdder.inputs[0]
B = oneBitAdder.inputs[1]
Ci = oneBitAdder.inputs[2]
S = oneBitAdder.outputs[0]
Co = oneBitAdder.inputs[1]

input1Xor1 = xor1.inputs[0]
input2Xor1 = xor1.inputs[1]
outputXor1 = xor1.outputs[0]

input1Xor2 = xor2.inputs[0]
...
outputAnd4 = and4.outputs[0]

input1Or2 = or2.inputs[0]
input2Or2 = or2.inputs[1]
outputOr2 = or2.outputs[0]
```

![bg right:30% height:400](figures/one_bit_full_adder_sense_conexions_amb_noms.png)

---

Make the connections
```python
Connection(A, input1Xor1)
Connection(B, input2Xor1)
Connection(outputXor1, input1Xor2)
Connection(Ci, input2Xor2)
Connection(outputXor1, input1And3)
Connection(Ci, input2And3)
Connection(A, input1And4)
Connection(B, input2And4)
Connection(outputAnd3, input1Or2)
Connection(outputAnd4, input2Or2)
Connection(outputXor2, S)
Connection(outputOr2, Co)
```

![bg right:30% height:230](figures/one_bit_full_adder.jpeg)

---

## Test

```python
# make all possible inputs (A, B, Ci) to 1-bit full adder
inputs = []
for a in [False, True]:
    for b in [False, True]:
        for c in [False, True]:
            inputs.append([a,b,c])

# the expected result (S, Co) for each input
expected_S = [False, True, True, False, True, False, False, True]
expected_Co = [False, False, False, True, False, True, True, True]

for (a, b, ci), exp_s, exp_co in zip(inputs, expected_S, expected_Co):
  A.set_state(a)
  B.set_state(b)
  Ci.set_state(ci)
  oneBitAdder.process()
  s = S.is_state()
  co = Co.is_state()
  print('{} + {} + {} = {}, {}'.format(a, b, ci, s, co))
  assert s == exp_s
  assert co == exp_co
```

---

$\tt{A} + \tt{B} + \tt{C_i} = \tt{S}, \tt{C_o}$

```text
False + False + False = False, False
False + False + True = True, False
False + True + False = True, False
False + True + True = False, True
True + False + False = True, False
True + False + True = False, True
True + True + False = False, True
True + True + True = True, True
```

---

We could continue with a 4-bits adder 
![height:250](figures/4bitsadder.png) ![height:250](figures/74LS83.png)

```python
one_bit_adder_1 = deepcopy(oneBitAdder)
one_bit_adder_2 = deepcopy(oneBitAdder)
one_bit_adder_3 = deepcopy(oneBitAdder)
one_bit_adder_4 = deepcopy(oneBitAdder)
four_bits_adder = Component("4bitsAdder", 9, 5) # 4As + 4Bs + 1Cin, 4S + 1Cout
# make the connections between four_bits_adder inputs and
# input of each one bit adder
...
# make the connnections between Cout -> Cin of 3 one bit adders
...
# make the connections between outputs of one bit adders and 
# outputs of the four bits adder
...
```

---

And now an **eight bits full adder**...

![height:300](figures/eight_bits_full_adder.jpeg)

<span style="color:green;">

Homework: 
- extend the Python code to make the 4-bits and the 8-bits full adders
- write the corresponding tests
</span>

---

Summary
===

- example of composite + observer
- development = cycles of design, program, test
- improvements / continuation:
    - order of circuits in a component matters, and also order of connections = propagation of states, maybe a [topological graph sorting](https://en.wikipedia.org/wiki/Topological_sorting) could solve this
    - how to remove a connection between two given pins ?   
        - disconnect is not just delete the connection object, the ``toPin`` should stop observing the ``fromPin`` $\rightarrow$ a ``Connection`` should keep a reference to its two pins
        - keep a list of all the connections made