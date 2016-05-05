select t3.feature,sum(t3.finalpolarity) as polarity,e.prodId from (select * from (select t1.feature,t1.opinion,t1.sentenceId,t1.polarity,coalesce(n.isNegateNear,1) as isNegateNear,t1.polarity*coalesce(isNegateNear,1) as finalpolarity
from (Select p.feature,p.opinion,p.sentenceId,coalesce(op.polarity,0) as polarity,p.mindistId from potentialfeature p 
left outer join opinion_polarity op on p.opinion = op.opinion) t1
left outer join negateproximity n on t1.sentenceId=n.sentenceId and t1.mindistId = n.relPos) t2
inner join reviewsentence r on t2.sentenceId = r.id) t3
inner join electronics_review e on t3.reviewId=e.id
group by e.prodId,t3.feature;