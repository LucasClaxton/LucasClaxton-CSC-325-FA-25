P.S. switch to code view to see the proper layout.
Assignment 3:
  Levels completed:
    Level 1: Yes.
    Level 2: No.
    Level 3: No.
  
  Part 3:
    Using interfaces in the Shape Management System provided greater flexibility and separation of concerns. 
  Interfaces allowed each shape class to implement only the behaviors it needed, such as drawing, calculating 
  area, or describing itself. This made it easy to add new behaviors or mix and match capabilities without 
  being forced into a rigid inheritance hierarchy. For example, if a future shape should only be drawable but 
  not calculable, interfaces make this possible without unnecessary method stubs.
    The abstract class version was more readable and reusable for shared attributes and logic. By defining 
  common fields like color and name in the abstract Shape class, subclasses automatically inherited these 
  properties, reducing code duplication. The abstract class also provided a clear template for what every 
  shape should implement, making the codebase easier to understand for new developers.
    In terms of scalability, the interface-based design is superior. As the system grows and more shapes 
  or behaviors are added, interfaces prevent the inheritance tree from becoming too deep or convoluted. 
  They support multiple inheritance of behavior, which is not possible with classes in Java. This makes 
  it easier to extend the system with new features or integrate with other components.
    For a real-world system, I would choose the interface-based approach. It offers better modularity, 
  easier testing, and greater adaptability to change. While abstract classes are useful for sharing code, 
  interfaces provide the flexibility needed for complex, evolving systems where shapes may have diverse 
  and overlapping capabilities. Combining both approaches—using abstract classes for shared state and 
  interfaces for behavior—can also be effective in larger projects.

AI Reflection:
  Level 1:
    I used Copilot to get a skeleton for the project and after adding my own code and trying to get the packaging to work properly, I asked Copilot how it would improve the code, and in total Copilot made a change to the base shape classes, but left the shape classes with "2" in them alone. I then added a Main class without AI help.
