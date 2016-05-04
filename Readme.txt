						README


/*********************************************************
				SENTIENGINE

	Akshay. A. Jadhav (es12b1004@iith.ac.in)
	Ratnesh Chandak (xs12b1030@iith.ac.in)
**********************************************************/


This is the sentiengine project. Sentiengine is an engine 
that does sentiment analysis for a reviewset. It is
independent of the reviewset.

In this readme file, we will explain how to set up the 
project.
The code is documented and necessary running steps are 
explained in the comments of code.

How to setup?

Following are the external jar files needed for this 
project:

->jaws-bin.jar
->json-20150729.jar
->mysql-connector-java-5.1.10.jar
->rita.jar
->stanford-postagger.jar
->stanford-postagger-2011-04-20.jar

All these jar files are included in the ./jar-files 
directory.

-->Add them to the build path in your Java IDE.

-->Download the stanford core nlp(http://stanfordnlp.github.io/CoreNLP/download.html)
and extract it in the root folder of this project.

-->Add all the jars from the stanford core nlp folder to java build path.

Database structure is in sentimentdb.sql

IMPORTANT!!!!!!!!!!!!!!!!!!
NOTE: IN all our codes, the sql updates(ps.esecuteUpdate()) are commented to ensure that no unecessary
updates take place, while running the code.



