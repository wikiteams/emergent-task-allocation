emergent-task-allocation
========================

[![Build Status](https://drone.io/github.com/wikiteams/emergent-task-allocation/status.png)](https://drone.io/github.com/wikiteams/emergent-task-allocation/latest) [![Stories in Ready](https://badge.waffle.io/wikiteams/emergent-task-allocation.png?label=ready&title=Ready)](https://waffle.io/wikiteams/emergent-task-allocation)

**Simulator of team emergence in collaborative environment - _version 2.0_**

### Introduction

This project is an improvment of a simulation described in paper "On the Effectiveness of Emergent Task Allocation of
Virtual Programmer Teams" (Jarczyk et.al., 2014), it's results are intended to be presented in a journal on social informatics / computing. Virtual teams of programmers are a popular form of collaboration both in Open Source, and commercial software projects. In OSS (Open Source Software) projects, programmers make their own decision which project to join, and, therefore, the process of task allocation among the project members is emergent. In this simulation, we attempt to simulate such a process based on available data from GitHub. In Repast Simphony we simulate work done by programmers on tasks as they appeared through GitHub timeline.

### Data

![Possible data sources](https://dl.dropboxusercontent.com/u/103068909/data-sources.png "Possible data sources")

#### Data source

Datasource is the [GitHub Archive](https://www.githubarchive.org), which was downloaded locally to disk and than added to a [MongoDB](http://www.mongodb.org) database. Data is dated from February 2011 to April 2014. 

#### Subsetting data for simulation

In the search of a best type of data (applicable to analysing activity in GitHub repositories) we considered PushEvents and PullRequestEvents. There is no commit event in GitHub but PushEvent consists of multiple commits. A good entity should persists bellow attributes: **task** (repo) id, **programming language**, and **work units**. 

#### Perils of mining GitHub

From the "The Promises and Perils of Mining GitHub" (Kalliamvakou et.al., 2014) we read that "Only a fraction of projects use pull requests. And of those that use them, their use is very skewed. (Peril VI)". "Of the 2.6 million projects that represent actual collaborative projects (at least 2 committers) only 268,853 (10%) used the pull request model at least once to incorporate commits; the remaining 2.4 M projects would have used GitHub in a shared repository model exclusively (with no incoming pull requests) where all developers are granted commit access." Thats why we present 2 types of collaborative setttings for the dataset creation.

#### Commit rights

#### Fork-Pull model

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

#### MongoDB database

In our university repositories we have a document database holding circa 172 milion events which occured on GitHub during previous years.

![Database of GH events](https://dl.dropboxusercontent.com/u/103068909/github_events_db.png "Database of GH events")

#### Querying MongoDB

Because structure of a JSON with type PullRequestEvent changes during GH lifetime, we filter solid most often occuring structure of documents, which will always have following parameters: *repository.url*, *repository.language*, *lines deleted*, *lines added*, *no of files changed*, *no of comments* and *number of commits*. This dataset is later saved to a seperate MongoDB collection to make for easier extraction.

![DB QL commands](https://dl.dropboxusercontent.com/u/103068909/komendy_dbql.png "DB QL commands")

It is possible to retrieve sample data with below command

```sql
db.pullrequests.find().limit(5).pretty()
```

#### Saving results to flat database

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

#### Standard configurations with Pareto principle

![Evolution plans](https://dl.dropboxusercontent.com/u/103068909/evolution-plans-sus.png "Evolution plans")

#### Hybrid model

![Additional evolution plans](https://dl.dropboxusercontent.com/u/103068909/evolution-plans-sus-h.png "Additional evolution plans")

#### Stochastic universal sampling (SUS)

After every 10 ticks of simulation every agent get a new task-choice strategy according to the implemented well-known
genetic algorithm called Stochastic Universal Sampling (SUS) method. SUS is a fitness proportionate selection method which uses a single random value to sample all solutions by choosing them at evenly spaced intervals. This gives weaker members of the population (according to the fitness function) a chance to be chosen.
Utility function considers lowest experience in a single skill plus 1/20 part of an average experience in all the skills.

### Analyzing performance of strategies

![Analyzing strategies efficiency](https://dl.dropboxusercontent.com/u/103068909/workflow-of-analysing-strategies.jpg "Workflow of str.an.")

### Validation model

Validation model under construction...
