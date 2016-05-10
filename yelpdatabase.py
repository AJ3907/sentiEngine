import MySQLdb as mdb
import json
import sys
from collections import defaultdict,OrderedDict
import datetime
import re

class Yelpdatabase:
  def __init__(self):
      self.week_day={0:'Monday',1:'Tuesday',2:'Wednesday',3:'Thursday',4:'Friday',5:'Saturday',6:'Sunday'}
      self.db='yelpdb'
      self.main_categories=defaultdict(str)
      self.cities=["Glendale","Gilbert","Mesa","Waterloo","Madison","Champaign"]
      self.business_ids=[];

  def createdb(self):
      try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("create database if not exists %s"%(self.db))

      except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)          
      finally:
          if cur: cur.close()
          if con: con.close()

  def user(self,input_file='yelp_academic_dataset_user.json',table_name='user'):
      try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("use %s"%(self.db))
          cur.execute( 'create table if not exists %s (Id int primary key auto_increment, review_count int,average_stars float, user_id varchar(200),name text)'%(table_name))
          with open(input_file) as f:
             for line in f:
                 tmp=json.loads(line)

                 sql="insert into user (review_count, average_stars,user_id, name) values(%s,%s,%s,%s)"
                  #print tmp['business_id']
                # cur.execute(sql,(tmp['city'],tmp['review_count'],tmp['name'].encode('utf8'),tmp['business_id'],main_item,tmp['latitude'],tmp['longitude']))
                 #print tmp
                 cur.execute(sql,(tmp['review_count'],tmp['average_stars'],tmp['user_id'],tmp['name'].encode("utf-8")))
      except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)               
      finally:
          con.commit()
          if cur: cur.close()
          if con: con.close()

  def count_categories(self,categories):
     categories_num=defaultdict(int)
     for item in categories:
       categories_num[item]+=1
     return categories_num

  def business(self,input_file='yelp_academic_dataset_business.json',table_name='business'):
      try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("use %s"%(self.db))
          cur.execute( 'create table if not exists %s (Id int primary key auto_increment, city text, review_count int, name text, business_id varchar(200), categories text, latitude text, longitude text,stars float)'%(table_name))
          category=[]
          with open(input_file) as f:
             for line in f:
                 tmp=json.loads(line)
                 # append categories
                 [category.append(item) for item in tmp['categories']]
             #count the occurences of each category
             categories_num=self.count_categories(category)
             #print  OrderedDict(sorted(categories_num.items(),key=lambda x: x[1]))

             # loop over again to find the main (most occured) category for each business, this step reduces the no. unique categories from ~500 to 40
          #main_items=[]
          remove_items=['Food'] # remove items from the categories that are too general, eg: food
          
          with open(input_file) as f:
             for line in f:
                max=0
                main_item=""
                tmp=json.loads(line)
                for item in tmp['categories']:
                   if categories_num[item]>max: #and item not in remove_items:
                      max=categories_num[item]
                      main_item=item
                #print tmp['categories'], main_item
                #main_items.append(main_item)
                if tmp['city'] in self.cities:
                  sql="insert into business (city,review_count,name,business_id, categories,latitude,longitude,stars) values(%s,%s,%s,%s,%s,%s,%s,%s)"
                  #print tmp['business_id']
                  cur.execute(sql,(tmp['city'],tmp['review_count'],tmp['name'].encode('utf8'),tmp['business_id'],main_item,tmp['latitude'],tmp['longitude'],tmp['stars']))
                  self.business_ids.append(tmp['business_id'])
                # few hacks to merge less frequent categories to bigger categories, before we do that, we keep the 22 categories in business table above, just in case
                if main_item=='Mass Media': main_item='Arts & Entertainment'
                if main_item=='Education' or main_item=='Religious Organizations' or main_item=='Financial Services' or  main_item=='Professional Services' or main_item=='Public Services & Government': main_item='Local Services'
                if main_item=='Local Flavor': main_item='Food'
                if main_item==None: main_item='Other'

                self.main_categories[tmp['business_id']]=main_item
          #print set(self.main_categories.values())
          #print len(list(set(main_items))), list(set(main_items))      

          #print len(category),len(list(set(category))),list(set(category))[0:10]
 
      except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)          
      finally:
          con.commit()
          if cur: cur.close()
          if con: con.close()

  def attribute(self,input_file='yelp_academic_dataset_business.json',table_name="attribute"):
      try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("use %s;"%(self.db))
          cur.execute( "create table if not exists %s (name text)"%(table_name))
          attributes={};
          with open(input_file) as f:
            for line in f:
                tmp=json.loads(line)
                #print tmp['attributes']
                isRestaurant=0
                for item in tmp['categories']:
                   if item=="Restaurants":
                    isRestaurant=1
                if isRestaurant==1:
                  for key,value in tmp['attributes'].iteritems():
                    if key=='Good For':
                      #print key
                      for k,v in tmp['attributes']['Good For'].iteritems():
                        #print k
                        attributes[k]=v
                    elif key=='Ambience':
                      for k,v in tmp['attributes']['Ambience'].iteritems():
                        attributes[k]=v
                    elif key=='Parking':
                      for k,v in tmp['attributes']['Parking'].iteritems():
                        attributes[k]=v
                    else:
                      #print(key)
                      attributes[key]=value
                      
            
            for key,value in attributes.iteritems():
              sql="insert into attribute (name) values(%s)"
              cur.execute(sql,(key,))

      except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)          
      finally:
          con.commit()
          if cur: cur.close()
          if con: con.close()

  def review(self,input_file='yelp_academic_dataset_review.json',table_name="review"):
      try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("use %s;"%(self.db))
          cur.execute( "create table if not exists %s (Id int primary key auto_increment, votes_funny varchar(10), votes_useful varchar(10), votes_cool varchar(10), user_id varchar(200), review_id varchar(200), stars varchar(10), date varchar(100), business_id varchar(200),text longtext)"%(table_name))
 
          with open(input_file) as f:
             for line in f:
                 tmp=json.loads(line)
                 tmp['text']=tmp['text'].encode('ascii','ignore')
                 year,month,day=[int(x) for x in tmp['date'].split('-')]
                 #if tmp['business_id'] in self.business_ids:
                 sql="insert into review (votes_funny,votes_useful,votes_cool, user_id, review_id, stars, date, business_id, text) values(%s,%s,%s,%s,%s,%s,%s,%s,%s)"
                 cur.execute(sql,(tmp['votes']['funny'] ,tmp['votes']['useful'] ,tmp['votes']['cool'] ,tmp['user_id'] ,tmp['review_id'] ,tmp['stars'] ,tmp['date']  ,tmp['business_id'],tmp['text']))
      except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)          
      finally:
          con.commit()
          if cur: cur.close()
          if con: con.close()

  def checkin(self, input_file='yelp_academic_dataset_checkin.json',table_name='checkin'):
     try:
          con = mdb.connect('localhost', 'root','1234')
          cur = con.cursor()
          cur.execute("use %s"%(self.db))
          cur.execute('create table if not exists %s (Id int primary key auto_increment, business_id varchar(200), weekday varchar(20), checkin_no int, categories varchar(100))'%(table_name))
          with open(input_file) as f:
             for line in f:
                check_in=defaultdict(int)
                tmp= json.loads(line)
                # get the number of checkins per business per day
                # first get all the ckeckins for a business
                d=json.dumps(tmp['checkin_info'],separators=(',',':'),skipkeys=True)
                # add the counts for each day
                for x in d.strip('{}').split(','):
                   l,r= re.split(':+',x)
                   ll,rl=re.split('-',l)
                   check_in[rl.replace('"','')]+=int(r)
                   #print self.week_day[int(rl.replace('"',''))], rl.replace('"','')
             
                # insert the counts for each day for each entry 
                for x in self.week_day.keys():
                       #print x,check_in[str(x)]
                       cur.execute("insert into %s (business_id, weekday, checkin_no, categories) values('%s','%s','%d','%s')"%(table_name,tmp['business_id'],self.week_day[x],check_in[str(x)],self.main_categories[tmp['business_id']]))
     except mdb.Error, e:
          print "Error %d: %s" % (e.args[0],e.args[1])
          sys.exit(1)          
     finally:
          con.commit()
          if cur: cur.close()
          if con: con.close()

if __name__ == "__main__":
   ydb=Yelpdatabase()
   #ydb.createdb()
   #ydb.user()
   #ydb.business()
   #ydb.attribute()
   ydb.review()
   #ydb.checkin()