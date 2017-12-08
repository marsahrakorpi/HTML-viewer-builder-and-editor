# HTML-viewer-builder-and-editor

Object-Oriented Programming, Autumn 2017. A course in university to learn basic Java programming and how to use Swing components.

This is a Java program to build basic HTML websites with only pressing buttons and setting basic element attributes (color, fonts, position, etc). No knowledge of HTML is required to build your own website.

**For more information about this project, please refer to the [Wiki](https://github.com/Gizwiz/HTML-viewer-builder-and-editor/wiki).**

---

# Installation

## Requires
### [Java SE Runtime Environment 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
**Download and install JRE8 or the program will not run**. May work on older versions of java, but has not been tested.

To check your java installation, type in your command prompt / terminal the following:

```
 java -version
```

It should output something along the following:

```
java version "1.8.0_151"
Java(TM) SE Runtime Environment (build 1.8.0_151-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.151-b12, mixed mode)
```


## Download the .jar
### [Latest Version Here](https://github.com/Gizwiz/HTML-viewer-builder-and-editor/releases)

**For help on running a .jar file, see [This WikiHow Article](https://www.wikihow.com/Run-a-.Jar-Java-File)**

Place the .jar file in your preferred directory, and run it like a regular program. 
Alternatively, you may create a .bat file in the same directory as the .jar file, with the command

```
java -jar HTMLEdit.jar
```

Running through the .bat will allow you to see console errors and logs. You may log all console messages to .txt file with the following .bat

```
java -jar HTMLEdit.jar >> log.txt
```

If you run into errors, these .txt logs are useful to the developer for debugging.
Seeing console messages is NOT REQUIRED to use this program. Most messages are fluff and useful only to the developer.

---

# Usage

### First Launch of the Program

On your first launch, the program will not find a project folder, so it will ask you to create one.
Name your folder, and select where you will like the folder to be.

![](https://imgur.com/JRECTPV.png)

### Functionality Buttons

There are four buttons in the top left corner for creating a new project, saving your changes, adding folders, and adding files.

Creating a new project will prompt you to name your project, and it will not allow you to create a project of the same name in the same directory as an old one.

The save button will save any changes you have made to your project.

The Create New Folder/File buttons **HAVE NOT BEEN IMPLEMENTED** due to time restrictions. There were a lot of bugs with creating temp files and sorting out configs, which were too much to iron out before the project deadline.

![](https://i.imgur.com/MD1vvW1.png)

### Create a New HTML Element

Click on the button in the top right corner, "New HTML Element".

![](https://i.imgur.com/b9zbVK4.png)

Select the element you wish to create.
**NOTE that not all elements have an entry in the database. This means that their element specific attributes are not accessible, and they may have incorrect html tags. Most (all) "basic" elements have a database entry and will work.**

![](https://i.imgur.com/bIJrWlB.png)

Select the CSS styles and HTML attributes as you wish. All changes will be reflected dynamically on the left, and a code preview can be seen on the bottom. When done, press confirm.

![](https://i.imgur.com/8LtLiaQg.png)

The element will now appear on your website!

### Edit or Remove an HTML Element

Editing or removing an element is simple. Just right click it in the element tree, located in the panel on the right side of the screen.
Selecting "Edit Element" will open the editing dialog, which is the same as when creating a new element. All previous settings will be saved, however.

![](https://i.imgur.com/mMxkjF7.png)

### Exiting the program

The program will prompt you if you have any unsaved changes. If you don't, you're good to go! The program will let you exit.

![](https://i.imgur.com/5DrxiIn.png)

---

An Imgur album peviewing this project can be found <a href="https://imgur.com/a/GBjZM" target="_blank"> here </a>.
For more information, see the [Wiki](https://github.com/Gizwiz/HTML-viewer-builder-and-editor/wiki/About-HTML-Editor)

---

# Third Party Libraries

Libraries used in this project:
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
- [ph-css: Java CSS2 and CSS3 Parser and Builder](https://github.com/phax/ph-css) 
- [W3C SAC](https://www.w3.org/Style/CSS/SAC/Overview.en.html)
- [jsoup: Java HTML Parser](https://jsoup.org/)
- [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/)
- [JSON-Java](https://github.com/stleary/JSON-java)
- [JUnit 4](http://junit.org/junit4/)
- [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)
- [Java SwingX](https://mvnrepository.com/artifact/org.swinglabs.swingx)

---

# MongoDB Implementation

## Cloud Hosting

Hosted with [mLab](https://mlab.com/)

## Why???

Creating and Editing HTML Elements are handled through a MongoDB implementation, purely because I wanted to know how to implement MongoDB in a Java program. Opening a connection to a DB every time is horribly inefficient, and it means that **Element specific attributes are not available offline**. In a real production environment everything would be, for example, in local .json files to massively speed up the responsiveness of the program. It is also a simpler and easier solution. 

**The MongoDB implementation was done purely for learning reasons. It makes no sense in a production envirnoment.**

**More about the MongoDB implementation in the [Wiki](https://github.com/Gizwiz/HTML-viewer-builder-and-editor/wiki/MongoDB)**

