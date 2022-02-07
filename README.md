<h1 align="center">PGen</h1>

 PGen is currently a small command line tool written in kotlin and compiled to Java.
I Plan to have a executable version which won't require the Java in the future, 
However currently it is only compiled to Java.

---

### Getting started
Prerequisites
- Java JRE/JDK 8 or higher. [Temurin JDK](https://adoptium.net/)

To check what version of java you are running run 
``java --version`` from the command line.

Once the prerequisites are fulfilled you can download the latest version of PGen from the [github releases](https://github.com/DaRacci/PGen/releases/latest) on the right.

---

### Usage
From the command line you can use
``java -jar PGen.jar`` this will run PGen with the default rules.

You can use a rules file to easily change the rules used by having PGen read a json file containing those rules.
To create a new rules file you can run ``java -jar PGen.jar file create`` this will place a new rules.json file where the jar file is located.
To use an existing rules file you can run ``java -jar PGen.jar file read`` this will read the rules.json file in the in the same directory of PGen.
To point towards a custom location where your file is located you can run ``java -jar PGen.jar file read path/to/rules.json``

On top of using a rules file you can also use the following command parameters to customise the rules
```
-w [words] 
    This is the number of words to generate in the password
-ml [minLength] 
    This is the minimum length of each word generated
-Ml [maxLength] 
    This is the maximum length of each word generated
-t [transform] 
    This will selected the transformation mode to use on the words, the modes are as follows:
        - none (No transformation)
        - capitalise (All words will be capitalised)
        - upercase_all_but_first_letter (All letters will be uppercase except the first letter)
        - uppercase (All letters will be uppercase)
        - random (All words will be randomly transformed)
        - alternating (The words will be in alternating uppercase and lowercase)
-sc [seperatorChar]
    This will select the character that is used inbetween each word, the modes are as follows:
        - none (No seperator)
        - random (This will use the seperatorAlphabet to select a random character for each word)
        - custom (Any single character input will be used as the seperator eg: "-sc ." will use . as the seperator)
-mrc [matchRandomChar]
    If seperatorChar is set to random this will enforce that each seperator is the same random character from the seperator alphabet
-sa [seperatorAlphabet]
    This is the alphabet that will be used to select a random seperator character if seperatorChar is set to random
-db [digitsBefore]
    This is the number of digits that will be generated at random before the words
-da [digitsAfter]
    This is the number of digits that will be generated at random after the words
```

When using a rules file and parameters the hierarchy of the final rule that will be used is as follows:
- Parameters
- Rules file
- Default rule

This means that while using a rules file you can still overwrite it with a parameter.

### Rule file
The rules file is a json file and should look something like this: 
```json
{
    "words": 2,
    "minLength": 5,
    "maxLength": 7,
    "transform": "CAPITALISE",
    "separatorChar": "RANDOM",
    "separatorAlphabet": "!@$%.&*-+=?:;",
    "matchRandomChar": true,
    "digitsBefore": 0,
    "digitsAfter": 3
}
```
This json file would represent the default rules used by PGen.

Please note that the key values are case sensitive.
The values are read in a lenient mode which means that string keys aren't required to be boxed in `""`
however it is still recommended to use stricter standard json rules.

When using a rules file you aren't required to include all keys and values, any missing keys have to default rules used.

### Running from a script
Instead of writing `java -jar PGen.jar` and whatever options you need, you can instead use a batch, powershell or shell file like these:
- Shell script
```shell
#!/bin/sh
java -jar PGen.jar -w 10 -ml 5 -Ml 7 -t CAPITALISE -sc RANDOM -mrc false -sa !@$%.&*-+=?:; -db 0 -da 3
pause
```
- Powershell / Batch script
```shell
java -jar "C:\Users\Racci\Programs\PGen\PGen.jar" file read "C:\Users\Racci\Programs\PGen\SimpleRules.json"
pause
```
