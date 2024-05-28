package Atomic;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicRefExample {
    public static void main(String[] args) {
        String oldName = "Old Name";
        String newName = "New Name";

        AtomicReference<String> atomicReference = new AtomicReference<>(oldName);

        if(atomicReference.compareAndSet(oldName, newName)) {
            System.out.println("Value changed to: " + atomicReference.get());
        }
        else {
            System.out.println("Nothing Changed");
        }
    }
}
