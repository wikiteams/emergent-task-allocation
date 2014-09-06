emergent-task-allocation
========================

Simulator of team emergence in collaborative environment - version 2.0

## Data

Datasource is the [GitHub Archive](https://www.githubarchive.org), which was downloaded locally to disk and than added to a [MongoDB](http://www.mongodb.org) database. Data is dated from February 2011 to April 2014. In the search of a best type of data (applicable to analysing activity in GitHub repositories) we considered PushEvents and PullRequestEvents. There is no commit event in GitHub but PushEvent consists of multiple commits. A good entity should persists bellow attributes: **task** (repo) id, **programming language**, and **work units**. We choose PullRequestEvents because they match most the mentioned requirements and furthermore, they follow a typical GitHub workflow, where a pull request must be created to integrated changes from other users. Sample PullRequestEvent looks like below:

```JSON
{
        "_id" : ObjectId("535cf263768a890c6884e622"),
        "created_at" : ISODate("2011-02-12T00:00:33Z"),
        "actor" : {
                "url" : "https://api.github.dev/users/ardcore",
                "login" : "ardcore",
                "avatar_url" : "https://secure.gravatar.com/avatar/1a101cb270d186fd5eca97d01c4f1248?d=http://github.dev%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
                "id" : 49605,
                "gravatar_id" : "1a101cb270d186fd5eca97d01c4f1248"
        },
        "payload" : {
                "number" : 1,
                "actor" : "ardcore",
                "repo" : "ardcore/temprepo",
                "pull_request" : {
                        "deletions" : 409,
                        "title" : "czyn to!",
                        "commits" : 2,
                        "number" : 1,
                        "issue_id" : 593146,
                        "additions" : 1065,
                        "id" : 71786
                },
                "action" : "closed",
                "actor_gravatar" : "1a101cb270d186fd5eca97d01c4f1248"
        },
        "repo" : {
                "url" : "https://api.github.dev/repos/ardcore/pewpewtowers",
                "id" : 1356751,
                "name" : "ardcore/pewpewtowers"
        },
        "id" : "1127195757",
        "type" : "PullRequestEvent",
        "public" : true
}
```
