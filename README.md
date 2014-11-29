
emergent-task-allocation
========================

[![Build Status](https://drone.io/github.com/wikiteams/emergent-task-allocation/status.png)](https://drone.io/github.com/wikiteams/emergent-task-allocation/latest) [![Stories in Ready](https://badge.waffle.io/wikiteams/emergent-task-allocation.png?label=ready&title=Ready)](https://waffle.io/wikiteams/emergent-task-allocation)

**Simulator of team emergence in collaborative environment - _version 2.0_**

### Introduction

This project is an improvment of a simulation described in paper _"On the Effectiveness of Emergent Task Allocation of
Virtual Programmer Teams"_ (Jarczyk et.al., 2014), it's results are intended to be presented in a journal on social informatics and computing. Virtual teams of programmers are a popular form of collaboration both in Open Source, and commercial software projects. In OSS (_Open-source software_) projects, programmers make their own decision which project to join, and, therefore, the process of task allocation among the project members is emergent. In this simulation, we attempt to simulate such a process based on available data from GitHub. In Repast Simphony we simulate work done by programmers on tasks as they appeared through GitHub timeline.

### Data

There are 4 possible inputs for the simulator. The way of input is defined in parameter set once. First is an open network socket which accepts tasks presented as JSON objects. Second, there is a possibility of reading data (formated as JSON) from files located in /data directory. Optionally, we can choose to query and get raw data from sqlite database. Third possibility is using dataset as described in previous version of the simulator. There is also possibility of using small test data - for debugging purposes only.

![Possible data sources](https://dl.dropboxusercontent.com/u/103068909/data-sources.png "Possible data sources")

#### Data source

##### Data source - how Agents are created

We use the brainjar list to get a set of most active GitHub users. Further, using logins of those users, we use the OSRC portal to get statistics of language use for a particular uset.

##### Data source - how Tasks are created

Datasource is the [GitHub Archive](https://www.githubarchive.org), which was downloaded locally to disk and than added to a [MongoDB](http://www.mongodb.org) database. Data is dated from February 2011 to April 2014.

##### Subsetting data for simulation

In the search of a best type of data (applicable to analysing activity in GitHub repositories) we considered PushEvents and PullRequestEvents. There is no commit event in GitHub but PushEvent consists of multiple commits. A good entity should persists bellow attributes: **task** (repo) id, **programming language**, and **work units**.

##### Perils of mining GitHub

From the _"The Promises and Perils of Mining GitHub"_ (Kalliamvakou et.al., 2014) we read that "Only a fraction of projects use pull requests. And of those that use them, their use is very skewed. (Peril VI)". "Of the 2.6 million projects that represent actual collaborative projects (at least 2 committers) only 268,853 (10%) used the pull request model at least once to incorporate commits; the remaining 2.4 M projects would have used GitHub in a shared repository model exclusively (with no incoming pull requests) where all developers are granted commit access." Thats why we present 2 types of collaborative setttings for the dataset creation.

##### Commit rights model

Because most of the repos use the pushes model with pinpoint defined rights, we us the PushEvents to create a set on workload in particular languages. Fields which are in our interest are: *created_at*, *repository.created_at*, *repository.url*, *repository.language*, *payload.size*. Sample PushEvent looks like below:

```JSON
{
  "_id" : ObjectId("535f1dab768a890c68beff74"),
  "actor_attributes" : {
    "login" : "egemenulucay",
    "type" : "User",
    "name" : "Egemen Uluçay",
    "gravatar_id" : "46df3b7e452f4b6d7cc7ad42c58ce13a"
  },
  "repository" : {
    "fork" : false,
    "watchers" : 1,
    "description" : "",
    "language" : "ASP",
    "has_downloads" : true,
    "url" : "https://github.com/jon0638/Teklif",
    "master_branch" : "master",
    "created_at" : "2014-03-31T23:55:12-07:00",
    "private" : false,
    "pushed_at" : "2014-04-01T00:04:46-07:00",
    "open_issues" : 0,
    "has_wiki" : true,
    "owner" : "jon0638",
    "has_issues" : true,
    "forks" : 0,
    "size" : 0,
    "stargazers" : 1,
    "id" : 18320468,
    "name" : "Teklif"
  },
  "url" : "https://github.com/jon0638/Teklif/compare/d6092452af...350391142e",
  "created_at" : new Date("1-4-2014 09:04:46"),
  "actor" : "egemenulucay",
  "public" : true,
  "type" : "PushEvent",
  "payload" : {
    "shas" : [["350391142e28983dd180d79ece0314d2bd9eb9aa", "egemen.ulucay@windowslive.com", "update", "Egemen Uluçay", true]],
    "head" : "350391142e28983dd180d79ece0314d2bd9eb9aa",
    "ref" : "refs/heads/master",
    "size" : 1
  }
}
```

##### Fork-Pull model

We choose PullRequestEvents because they match most the mentioned requirements and furthermore, they follow a typical GitHub workflow, where a pull request must be created to integrated changes from other users. Sample PullRequestEvent looks like below:

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

Next, this collection is sorted by the created_at field which is a datetime. It means that we make a GitHub life timeline. After we encounter a repository, we iterate through all of its PullRequest events to count number of changes per a language. This gives us below structure: D1: T1:R1{Java 50/250} ; T2:R1{Obj-C 20/2000} , D2: T1:R2{C 1/100}

##### Merged dataset (hybrid model)

Probably a merged data on pulls and pushes will be just fine to analyze team emergence.

#### Technical aspects of dataset

##### MongoDB database

In our university repositories we have a document database holding circa 172 milion events which occured on GitHub during previous years.

![Database of GH events](https://dl.dropboxusercontent.com/u/103068909/github_events_db.png "Database of GH events")

##### Querying MongoDB

Because structure of a JSON with type PullRequestEvent changes during GH lifetime, we filter solid most often occuring structure of documents, which will always have following parameters: *repository.url*, *repository.language*, *lines deleted*, *lines added*, *no of files changed*, *no of comments* and *number of commits*. This dataset is later saved to a seperate MongoDB collection to make for easier extraction.

![DB QL commands](https://dl.dropboxusercontent.com/u/103068909/komendy_dbql.png "DB QL commands")

It is possible to retrieve sample data with below command

```sql
db.pullrequests.find().limit(5).pretty()
```

##### Saving results to flat database

We are using data aggregators in **Python** with a help of mongodb package to aggregate data and created dataset which will be later consumed by simulator. You can check the code which is located in the _/misc_ folder

### Simulator workflow

![Modes of work](https://dl.dropboxusercontent.com/u/103068909/modele-symulator.png "Modes of work")

### Simulation actors

Naturally, in the first version of the simulator there was  constant number of agents and tasks, it was set in a scenario parameter file. The ratio of *agent count* to *task count* was defined *a priori* through analysing GitHub data (clusters of 11 most active languages and their top repos). Now we simplify this reasoning. Task are incoming as they emerged on GitHub timeline, but what’s most important, we can answer the question of *how many agents it take to close a task* in *particular amount of time*.

### Strategies

![Types of strategies](https://dl.dropboxusercontent.com/u/103068909/types-of-strategies.png "Types of strategies")

There are basically 3 types of strategies: central planner, emergent strategies, and a random choice. We want to answer a question whether central assignment strategy is better than emergent strategy, what is the best evolutionary stable combination of strategies for a GitHub environment, and finally verify it's effectiveness compared to random choice.

In previous version of the simulation central planner was excluded from the evolutionary model. We introduce a hybrid model, where central planer can take part in the evolutionary SUS choice simply be reducing it's work to agents willing to use the central assignment method.

#### Central planner

Changes since previous version modify little central planner, especially it's blocking mechanism. Creating a perfect central planner which won't discriminate similar workers is a difficult problem from the category of task scheduling science. We propose a simple 2.0 version of an imho quite simple straightforward algorithm:

#### Preferential strategy

First version of preferential strategy was implemented to analyse general advancement in a task and choose the most advanced (deterministic action because the most advanced tasked got most attention before). Current version of preferential strategy simply checks how many times got workers to work on them.

### Evolutionary model

The evolutionary game is supposed to answer question - is there a stable set of strategies for a typical GitHub repository, and whether is it better than using a central planer to control team work or not.

#### Standard configurations with Pareto principle

The Pareto principle (also known as the 80–20 rule, the law of the vital few, and the principle of factor sparsity) states that, for many events, roughly 80% of the effects come from 20% of the causes. We add an evolutionary plan where there are three strategies taking part in the evolutionary game - homophily, heterophily and preferential, which means that even a minor strategy has a chance of creating a vast major of work.

![Evolution plans](https://dl.dropboxusercontent.com/u/103068909/evolution-plans-sus.png "Evolution plans")

#### Hybrid model (optional)

Optionally, simulator can add central planner to the evolutionary game. At the moment, mixing emergent strategies with central assignemtn strategies is not part of our research.

![Additional evolution plans](https://dl.dropboxusercontent.com/u/103068909/evolution-plans-sus-h.png "Additional evolution plans")

#### Stochastic universal sampling (SUS)

In previous version of the simulator, after every 10 ticks of simulation every agent get a new task-choice strategy according to the implemented well-known genetic algorithm called _Stochastic Universal Sampling_ (SUS) method. SUS is a fitness proportionate selection method which uses a single random value to sample all solutions by choosing them at evenly spaced intervals. This gives weaker members of the population (according to the fitness function) a chance to be chosen. Utility function considers lowest experience in a single skill plus _1/20_ part of an average experience in all the skills. In the new version, we use a fully implemented collaboration controller with generations and iterations counters. Evolution process happens at the end of a generation, and than state of objects is reset.

### Fitness function

![Fitness function](https://dl.dropboxusercontent.com/u/103068909/eval-function.png "Fitness function")

Utility function considers lowest experience in a single skill plus _1/20_ part of an average experience in all the skills. It makes for avoid bais in a situation when have some skill in which he is very weak but couple of more skills in which he/she is very advanced.

### Resetting

At the end of a generation, experience of agents is reset to the initial one.

### Mutation

Agent's experience have a chance of mutating (e.g. due to their external proffesional training they had outside GitHub portal). Agent can decide to abondon a particular skill or can improve himself in it, thus the mutation works by: deleting a skill or adding more experience after a generation.

### Time units

* Tick - our basic measurement of time, a single time unit in the simulation, 1 bit of time
* Iteration - sequent tick in the generation, resets after generation ends
* Generation - ends after _gnt_ - a particular constant period of time
* Run (instance) - in case of many seperate parallel runs at once - generating more results to remove bias

### Generation time / number of iterations

A generation time _gnt_ is a time of workload done typically in 24h hours on GitHub. We find _gnt_ to be exactly..

#### In search of the iteration count number

### Workflow in the evolution

![Analyzing strategies efficiency](https://dl.dropboxusercontent.com/u/103068909/workflow-of-analysing-strategies.jpg "Workflow of str.an.")

### Analyzing performance of strategies

Performance of strategy is measured by number of ticks required to close all the tasks.

### Validation model

Validation model under construction...
