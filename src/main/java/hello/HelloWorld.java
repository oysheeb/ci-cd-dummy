package hello;

import java.time.LocalTime;

public class HelloWorld {
    public static void main(String[] args) {
      String time = "1 am";
		  System.out.println("The current local time is: " + time);

        Greeter greeter = new Greeter();
        System.out.println(greeter.sayHello());
    }
}
