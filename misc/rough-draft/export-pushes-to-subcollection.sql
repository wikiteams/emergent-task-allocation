db.createCollection("pushesfirst")
db.events.find({type:"PushEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}, "payload.size":{$exists:true}}).sort({created_at:1}).forEach(function(x){db.pushesfirst.save(x);})
--
db.createCollection("pushesleast")
db.events.find({type:"PushEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}, "payload.size":{$exists:true}}).sort({created_at:-1}).forEach(function(x){db.pushesleast.save(x);})
--
db.createCollection("allpushes")
db.events.find({type:"PushEvent"}).sort({created_at:1}).forEach(function(x){db.allpushes.save(x);})
-- created_at:1 means we sort from the earliest date to the latest