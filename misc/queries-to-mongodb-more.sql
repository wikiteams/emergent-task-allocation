-- napotkaj na repo od poczatku wymiaru czasu (GitHub timeline)


db.events.find({type:"PullRequestEvent"}).sort({created_at:1})


-- uwzglednij tylko niezbedne pola ktore bedziesz agregowal
-- repo name, jezyk programowania liczba commitow, ilosc lini dodanych, ilosc lini usunietych


db.events.find({type:"PullRequestEvent"},{"repo.name":1, "payload.pull_request.commits":1, "payload.pull_request.additions":1, "payload.pull_request.deletions":1}).sort({created_at:1})


-- group records
-- indeksy powinny przyspieszyc troche te operacje..

db.events.aggregate(
    { 
	$group : {_id : "$repo.name", total : { $sum : 1 }}
    }
  ).limit(10).pretty()

db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repo.name", total : { $sum : 1 }} }
    ])
  ).limit(10).pretty()

db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total : { $sum : 1 }} }
    ])
  ).limit(10).pretty()

db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total : { $sum : 1 }} },
	{ $sort: { created_at: 1 } }
    ])
  ).limit(10).pretty()
  
db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total : { $sum : 1 }} },
	{ $sort: { created_at: 1 } },
	{ $limit: 100 }
    ])
  ).pretty() -- w koncu dopiero ta funkcja zwraca cos sensownego w realnym czasie :)

db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total_entries : { $sum : 1 }, total_commits : { $sum : "$payload.pull_request.commits"}} },
	{ $sort: { created_at: 1 } },
	{ $limit: 100 }
    ])
  ).pretty()

db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total_entries : { $sum : 1 }, total_commits : { $sum : "$payload.pull_request.commits"}, total_additions: { $sum : "$payload.pull_request.additions"} , total_deletions: { $sum : "$payload.pull_request.deletions"}} },
	{ $sort: { created_at: 1 } },
	{ $limit: 100 }
    ])
  ).pretty()
  
db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total_entries : { $sum : 1 }, total_commits : { $sum : "$payload.pull_request.commits"}, total_additions: { $sum : "$payload.pull_request.additions"} , total_deletions: { $sum : "$payload.pull_request.deletions"}} },
	{ "$project" : {
      'commits' : '$total_commits',
      'additions' : '$total_additions',
      'deletions' : '$total_deletions',
      'changes' : { '$add' : [ '$additions', 'deletions' ] }
     } },
	{ $sort: { created_at: -1 } },
	{ $limit: 100 }
    ])
  ).pretty()
  
db.events.aggregate([
	{ $match: { type:"PullRequestEvent" } },
	{ $group : {_id : "$repository.url", total_commits : { $sum : "payload.pull_request.commits" }} },
	{ $group : {_id : "$repository.language", total_commits_per_language : { $sum : "$total_commits" }} },
	{ $sort: { created_at: 1 } },
	{ $limit: 500 }
    ])
  ).pretty()


--  ------------------------- 

-- jak widac idzie to beznadziejnie, postanowilem ze napisze skrypt w pymongo
-- ktory zrobi mi pieknista agregacje i jeszcze pokaze progres
  

db.events.find({type:"PullRequestEvent"}).sort({created_at:1}).group(
   {
     key: { repo.name: 1, payload.pull_request.deletions: 1 },
     cond: { ord_dt: { $gt: new Date( '01/01/2012' ) } },
     reduce: function ( curr, result ) { },
     initial: { }
   }
).pretty()

-- with projection operator

db.events.find({type:"PullRequestEvent"},{"repo.name":1, "payload.pull_request.commits":1, "payload.pull_request.additions":1, "payload.pull_request.deletions":1}).limit(2).sort({created_at:1}).group(
   {
     key: { repo.name: 1, payload.pull_request.deletions: 1 },
     cond: { ord_dt: { $gt: new Date( '01/01/2012' ) } },
     reduce: function ( curr, result ) { },
     initial: { }
   }
).pretty()

-- wersja z limitem

db.events.find({type:"PullRequestEvent"}).limit(5).sort({created_at:1}).group(
   {
     key: { repo.name: 1, payload.pull_request.deletions: 1 },
     cond: { ord_dt: { $gt: new Date( '01/01/2012' ) } },
     reduce: function ( curr, result ) { },
     initial: { }
   }
).pretty()

--  payload.pull_request.commits: 1, payload.pull_request.additions: 1, payload.pull_request.deletions: 1
-- ale tam nie ma jezyka!!
-- constant change, also run this


---------------- dane na temat pushy niekoniecznie wiernie odwzorowuje aktywnosc, poniewaz zawieraja jedynie informacje o ilosci comitow, ale nie ilosci wykonanej pracy (liczby zmienionych linii)

---------------- tutaj zapytania dla pull request ktore maja wszystkie niezbedne dane

db.events.find({type:"PullRequestEvent"},{"repository.url":1, "repository.language":1, "payload.pull_request.additions":1, "payload.pull_request.deletions":1, "payload.pull_request.commits":1}).limit(1).sort({created_at:1})

db.events.find({type:"PullRequestEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}},{"repository.url":1, "repository.language":1, "payload.pull_request.additions":1, "payload.pull_request.deletions":1, "payload.pull_request.commits":1}).limit(1).sort({created_at:1})
--- to wykonuje sie bardzo dlugo...
db.events.find({type:"PullRequestEvent", "repository.url":{$exists:true}, "repository.language":{$exists:true}},{"repository.url":1, "repository.language":1, "payload.pull_request.additions":1, "payload.pull_request.deletions":1, "payload.pull_request.commits":1}).explain()
--- to na wiele sie nie zda, bo explain jest tak samo wolne jak query - musi odpalic query by zbadac czasy..

--- niby sa zalozone indeksy zlozone na to ale nie ma indeksu DOKLADNIE na pola "type" oraz "created_at", mozna sprobowac zalozyc taki potem

db.events.ensureIndex( { type: 1, created_at: 1}, {background: true}, { name: "type-and-createdate" } )

--- Sparse indexes are like non-sparse indexes, except that they omit references to documents that do not include the indexed field.

db.events.ensureIndex( { "repository.url": 1, "repository.language":1 }, { sparse: true }, {background: true}, { name: "url-and-language-sparse" } )

db.events.ensureIndex( { type:1, "repository.url": 1, "repository.language":1 }, { sparse: true }, {background: true}, { name: "type-url-and-language-sparse" } )
