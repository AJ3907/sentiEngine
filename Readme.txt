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


QUERY FOR GETTING OVERALL SENTIMENT OF FEATURE:

"select t3.feature,sum(t3.finalpolarity) as polarity,e.prodId from (select * from (select t1.feature,t1.opinion,t1.sentenceId,t1.polarity,coalesce(n.isNegateNear,1) as isNegateNear,t1.polarity*coalesce(isNegateNear,1) as finalpolarity
from (Select p.feature,p.opinion,p.sentenceId,coalesce(op.polarity,0) as polarity,p.mindistId from potentialfeature p 
left outer join opinion_polarity op on p.opinion = op.opinion) t1
left outer join negateproximity n on t1.sentenceId=n.sentenceId and t1.mindistId = n.relPos) t2
inner join reviewsentence r on t2.sentenceId = r.id) t3
inner join electronics_review e on t3.reviewId=e.id
group by e.prodId,t3.feature;"

IMPORTANT!!!!!!!!!!!!!!!!!!
NOTE: IN all our codes, the sql updates(ps.esecuteUpdate()) are commented to ensure that no unecessary
updates take place, while running the code.



