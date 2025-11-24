package hello;

import org.junit.Test;
import static org.junit.Assert.*;

public class GreeterTest {

    @Test
    public void testSayHello() {
        Greeter greeter = new Greeter();
        String expected = "DevOps Project for AchiStar Technologies";
        assertEquals(expected, greeter.sayHello());
    }
}
