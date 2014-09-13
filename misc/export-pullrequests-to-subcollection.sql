db.events.find({type:"PullRequestEvent"}).limit(10).sort({created_at:-1})

db.events.find({type:"PullRequestEvent"}).limit(10).sort({created_at:-1}).pretty()

db.events.find({type:"PullRequestEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}, "payload.pull_request.commits":{$exists:true}}).limit(10).sort({created_at:-1}).pretty()

db.events.find({type:"PullRequestEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}, "payload.pull_request.commits":{$exists:true}}).limit(10).sort({created_at:1}).pretty()

db.createCollection("pullrequests")
db.events.find({type:"PullRequestEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}, "payload.pull_request.commits":{$exists:true}}).sort({created_at:1}).forEach(function(x){db.pullrequests.save(x);})
db.createCollection("allpullrequests")
db.events.find({type:"PullRequestEvent"}).sort({created_at:1}).forEach(function(x){db.allpullrequests.save(x);})