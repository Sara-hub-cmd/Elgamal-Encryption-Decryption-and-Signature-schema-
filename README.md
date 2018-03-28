# Elgamal

This Git has four different sub-project which are

1. ElgamalEncrypt -> Implimentation of Elgamal encription algorithm
2. ElgamalDeccrypt-> Implimentation of Elgamal decryption algorithm
3. ElgamalSignature -> Implimentation of Elgamal signature algorithm 
4. ElgamalVarification-> Implimentation of Elgamal verification algorithm

# Requirements

1. Java
2. Eclipse (not mandatory)

# Run Procedure

Import all the folders into Eclipse using import project.<br \>

to test the algorithm every project contains MyElGamalTst.java file which contains main file and is used to test algorithm functionlality. <br \>

So if you want to Encript File

## Using command line 

1. Go to src folder of the project (Elgamal\ElgamalEncrypt\src) and execute command javac -d ..\bin *.java
2. Go to bin directory (cd ..\bin) and execute command java MyElGamalTst a.text (you can put name of your file)
3. Output will be created in folder name with first charecter of algorithm. exapmle for Encrypt it will be in folder E (cd ..\E)

## Using Eclipse

1. Click on Run -> Run Configurations
2. Click on Arguments tab
3. In Program Arguments section , Enter your arguments (file name a.txt).
4. Click Apply

To run other algorithms follow the same procedure.

#### Note : You can do it for any type of file like pdf,jpg etc.
