public class HelloWorld { 
    private final String name;
    private final int age;

    public HelloWorld(String name) { 
        this.name = name; 
        this.age = 19; // default value if age not provided
    }

    public HelloWorld(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void greet() { 
        System.out.println("Hello, " + name + "!"); 
    }

    // Overloaded greet method
    public void greet(String customMessage) {
        System.out.println(customMessage + ", " + name + "!");
    }

    public void introduce() {
        System.out.println("Hello, my name is " + name + " and I am " + age + " years old.");
    }

    public static void main(String[] args) { 
        HelloWorld student1 = new HelloWorld("Lucas");
        student1.greet();
        student1.greet("Welcome");
        student1.introduce();

        HelloWorld student2 = new HelloWorld("Alex", 21);
        student2.greet();
        student2.greet("Hi there");
        student2.introduce();
    } 
}