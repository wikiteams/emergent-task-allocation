# Multiple plot function
#
# ggplot objects can be passed in ..., or to plotlist (as a list of ggplot objects)
# - cols:   Number of columns in layout
# - layout: A matrix specifying the layout. If present, 'cols' is ignored.
#
# If the layout is something like matrix(c(1,2,3,3), nrow=2, byrow=TRUE),
# then plot 1 will go in the upper left, 2 will go in the upper right, and
# 3 will go all the way across the bottom.
#
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  require(grid)
  
  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)
  
  numPlots = length(plots)
  
  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                     ncol = cols, nrow = ceiling(numPlots/cols))
  }
  
  if (numPlots==1) {
    print(plots[[1]])
    
  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))
    
    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))
      
      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}

setwd("~/data/symulacje-github/simphony_model_1428098337665")

library(dplyr)
library(sqldf)

# wczytywanie stanow agentow z pojedynczych instancji
instance_1 <- read.csv2('instance_1/agents_nonaggr_state.2015.kwi.04.00_01_46.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_2 <- read.csv2('instance_2/agents_nonaggr_state.2015.kwi.04.00_01_45.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_3 <- read.csv2('instance_3/agents_nonaggr_state.2015.kwi.04.00_01_46.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_4 <- read.csv2('instance_4/agents_nonaggr_state.2015.kwi.04.00_01_45.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_5 <- read.csv2('instance_5/agents_nonaggr_state.2015.kwi.04.00_01_45.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_6 <- read.csv2('instance_6/agents_nonaggr_state.2015.kwi.04.00_01_47.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_7 <- read.csv2('instance_7/agents_nonaggr_state.2015.kwi.04.00_01_47.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_8 <- read.csv2('instance_8/agents_nonaggr_state.2015.kwi.04.00_01_47.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_9 <- read.csv2('instance_9/agents_nonaggr_state.2015.kwi.04.00_01_46.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_10 <- read.csv2('instance_10/agents_nonaggr_state.2015.kwi.04.00_01_46.csv', sep=";", quote="\"", header = TRUE, dec=",")

# wczytywanie wynikow runow z pojedynczych instancji
# zamiast stanow agentow sa tutaj koncowe wartosci, takie jak
# wystepowanie strategi, ilosc generacji itp.
batch_1 <- read.csv('instance_1/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_2 <- read.csv('instance_2/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_3 <- read.csv('instance_3/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_4 <- read.csv('instance_4/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_5 <- read.csv('instance_5/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_6 <- read.csv('instance_6/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_7 <- read.csv('instance_7/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_8 <- read.csv('instance_8/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_9 <- read.csv('instance_9/batch.log', sep=",", quote="", header = TRUE, dec=".")
batch_10 <- read.csv('instance_10/batch.log', sep=",", quote="", header = TRUE, dec=".")

# remember to make  tree | grep -i fail in OS before further analysis

typeof(batch_1)
length(batch_1)
# liczba wynikow (lini) ogolem (z plikow batch*)
nrow(batch_1) + nrow(batch_2) + nrow(batch_3) + nrow(batch_4) + nrow(batch_5) +
  nrow(batch_6) + nrow(batch_7) + nrow(batch_8) + 
  nrow(batch_9) + nrow(batch_10)

# scalanie wynikow w jeden data frame
batch_merged <- sqldf("select * from batch_1 union select * from batch_2 union select * from batch_3 union select * from batch_4  
                      union select * from batch_5 union select * from batch_6 union select * from batch_7 union select * from batch_8 
                      union select * from batch_9 union select * from batch_10")

batch_merged$Experience_Decay <- as.logical(batch_merged$Experience_Decay=='true')
batch_merged$Allow_Skill_Death <- as.logical(batch_merged$Allow_Skill_Death=='true')
batch_merged$Fll_Enabled <- as.logical(batch_merged$Fll_Enabled=='true')
batch_merged$Exp_cut_point <- as.logical(batch_merged$Exp_cut_point=='true')
batch_merged$Granularity <- as.logical(batch_merged$Granularity=='true')
batch_merged$Is_Evolutionary <- as.logical(batch_merged$Is_Evolutionary=='true')
batch_merged$Allways_Choose_Task <- as.logical(batch_merged$Allways_Choose_Task=='true')

sqldf("create index idx_batch_run on batch_merged(Run_Number)")
typeof(batch_merged)
sapply(batch_merged, class)

# subset agent's state only to important attributes
selected_columns <- c("run", "tick", "EvolutionGeneration", "EvolutionIteration", "Id", 
                      "TaskStrategy", "GeneralExperience", "Utility", "LUtility", "RUtility")

instance_1$tick <- as.numeric(instance_1$tick)
instance_2$tick <- as.numeric(instance_2$tick)
instance_3$tick <- as.numeric(instance_3$tick)
instance_4$tick <- as.numeric(instance_4$tick)
instance_5$tick <- as.numeric(instance_5$tick)
instance_6$tick <- as.numeric(instance_6$tick)
instance_7$tick <- as.numeric(instance_7$tick)
instance_8$tick <- as.numeric(instance_8$tick)
instance_9$tick <- as.numeric(instance_9$tick)
instance_10$tick <- as.numeric(instance_10$tick)

instance_1$RUtility <- as.numeric(instance_1$RUtility)
instance_1$LUtility <- as.numeric(instance_1$LUtility)
instance_1$Utility <- as.numeric(instance_1$Utility)
instance_1$GeneralExperience <- as.numeric(instance_1$GeneralExperience)

instance_2$RUtility <- as.numeric(instance_2$RUtility)
instance_2$LUtility <- as.numeric(instance_2$LUtility)
instance_2$Utility <- as.numeric(instance_2$Utility)
instance_2$GeneralExperience <- as.numeric(instance_2$GeneralExperience)

instance_3$RUtility <- as.numeric(instance_3$RUtility)
instance_3$LUtility <- as.numeric(instance_3$LUtility)
instance_3$Utility <- as.numeric(instance_3$Utility)
instance_3$GeneralExperience <- as.numeric(instance_3$GeneralExperience)

instance_4$RUtility <- as.numeric(instance_4$RUtility)
instance_4$LUtility <- as.numeric(instance_4$LUtility)
instance_4$Utility <- as.numeric(instance_4$Utility)
instance_4$GeneralExperience <- as.numeric(instance_4$GeneralExperience)

instance_5$RUtility <- as.numeric(instance_5$RUtility)
instance_5$LUtility <- as.numeric(instance_5$LUtility)
instance_5$Utility <- as.numeric(instance_5$Utility)
instance_5$GeneralExperience <- as.numeric(instance_5$GeneralExperience)

instance_6$RUtility <- as.numeric(instance_6$RUtility)
instance_6$LUtility <- as.numeric(instance_6$LUtility)
instance_6$Utility <- as.numeric(instance_6$Utility)
instance_6$GeneralExperience <- as.numeric(instance_6$GeneralExperience)

instance_7$RUtility <- as.numeric(instance_7$RUtility)
instance_7$LUtility <- as.numeric(instance_7$LUtility)
instance_7$Utility <- as.numeric(instance_7$Utility)
instance_7$GeneralExperience <- as.numeric(instance_7$GeneralExperience)

instance_8$RUtility <- as.numeric(instance_8$RUtility)
instance_8$LUtility <- as.numeric(instance_8$LUtility)
instance_8$Utility <- as.numeric(instance_8$Utility)
instance_8$GeneralExperience <- as.numeric(instance_8$GeneralExperience)

instance_9$RUtility <- as.numeric(instance_9$RUtility)
instance_9$LUtility <- as.numeric(instance_9$LUtility)
instance_9$Utility <- as.numeric(instance_9$Utility)
instance_9$GeneralExperience <- as.numeric(instance_9$GeneralExperience)

instance_10$RUtility <- as.numeric(instance_10$RUtility)
instance_10$LUtility <- as.numeric(instance_10$LUtility)
instance_10$Utility <- as.numeric(instance_10$Utility)
instance_10$GeneralExperience <- as.numeric(instance_10$GeneralExperience)

# save it to a local sqlite database
write.table(instance_1[selected_columns], "data_ts_1.dat", sep = ",", quote = FALSE, dec = ".")
data_1 <- file("data_ts_1.dat")
write.table(instance_2[selected_columns], "data_ts_2.dat", sep = ",", quote = FALSE, dec = ".")
data_2 <- file("data_ts_2.dat")
write.table(instance_3[selected_columns], "data_ts_3.dat", sep = ",", quote = FALSE, dec = ".")
data_3 <- file("data_ts_3.dat")
write.table(instance_4[selected_columns], "data_ts_4.dat", sep = ",", quote = FALSE, dec = ".")
data_4 <- file("data_ts_4.dat")
write.table(instance_5[selected_columns], "data_ts_5.dat", sep = ",", quote = FALSE, dec = ".")
data_5 <- file("data_ts_5.dat")
write.table(instance_6[selected_columns], "data_ts_6.dat", sep = ",", quote = FALSE, dec = ".")
data_6 <- file("data_ts_6.dat")
write.table(instance_7[selected_columns], "data_ts_7.dat", sep = ",", quote = FALSE, dec = ".")
data_7 <- file("data_ts_7.dat")
write.table(instance_8[selected_columns], "data_ts_8.dat", sep = ",", quote = FALSE, dec = ".")
data_8 <- file("data_ts_8.dat")
write.table(instance_9[selected_columns], "data_ts_9.dat", sep = ",", quote = FALSE, dec = ".")
data_9 <- file("data_ts_9.dat")
write.table(instance_10[selected_columns], "data_ts_10.dat", sep = ",", quote = FALSE, dec = ".")
data_10 <- file("data_ts_10.dat")

merged <- sqldf("select * from data_1 union select * from data_2 union select * from data_3 union select * from data_4  
                union select * from data_5 union select * from data_6 union select * from data_7 union select * from data_8 
                union select * from data_9 union select * from data_10", 
                dbname = tempfile(tmpdir = c("/mnt/data2/virtualteams/symulacje/")))

sqldf("create index idx_merged_gen on merged(EvolutionGeneration)")
sqldf("create index idx_merged_run on merged(run)")
sqldf("create index idx_merged_runid on merged(run, Id)")
sqldf("create index idx_merged_genrun on merged(EvolutionGeneration, run)")

head(batch_1)
names(batch_1)
head(merged)
names(merged)

# wygeneruj vector kombinacji

combination_of_param <- expand.grid(
  c(TRUE, FALSE), 
  c(50, 120),
  c(20, 100))
typeof(combination_of_param)
combination_of_param

# mamy zestaw 2 × 2 × 2 , co daje 8 kombinacji

my_col_names <- c(
  'ExpDecay',
  'AgentCount', 
  'IterationLength')
colnames(combination_of_param) <- my_col_names
typeof(combination_of_param)

# teraz majac liste kombinacji mozesz robic query o poszczegolne warunki

# otworz X obiektow multiplot, tyle ile jest kombinacji (combination_of_param)
# oraz X tabelek ze statystykami

# zlicz licznosc runow o danej konfiguracji
# 1) iteruj po runach (runid), bedzie szybciej
#   2) dla pojedynczego runu 
#       -oblicz wlasciwosci 
#       -dodaj dane do wlasciwej tabelki
#       -dodaj dane do wlasciwego plot (wg parametrow)

# narysuj multiplot z przebiegiem ewolucji x razy

# podstawowe analizy

merged_stat_1 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                        max(Utility) as maxUtility, avg(Utility) as avgUtility,
                        min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                        avg(LUtility) as avgLUtility,
                        min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                        avg(RUtility) as avgRUtility,
                        min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                        avg(GeneralExperience) as avgGeneralExperience
                        from instance_1 group by run, Id")

merged_stat_2 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                        max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_2 group by run, Id")

merged_stat_3 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                        max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_3 group by run, Id")

merged_stat_4 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_4 group by run, Id")

merged_stat_5 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_5 group by run, Id")

merged_stat_6 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_6 group by run, Id")

merged_stat_7 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_7 group by run, Id")

merged_stat_8 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_8 group by run, Id")

merged_stat_9 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_9 group by run, Id")

merged_stat_10 <- sqldf("select run as RunId, Id as AgentId, min(Utility) as minUtility, 
                       max(Utility) as maxUtility, avg(Utility) as avgUtility,
                       min(LUtility) as minLUtility, max(LUtility) as maxLUtility, 
                       avg(LUtility) as avgLUtility,
                       min(RUtility) as minRUtility, max(RUtility) as maxRUtility, 
                       avg(RUtility) as avgRUtility,
                       min(GeneralExperience) as minGeneralExperience, max(GeneralExperience) as maxGeneralExperience, 
                       avg(GeneralExperience) as avgGeneralExperience
                       from instance_10 group by run, Id")

merged_stat_all <- sqldf("select * from merged_stat_1 union 
                          select * from merged_stat_2 union 
                          select * from merged_stat_3 union
                          select * from merged_stat_4 union 
                          select * from merged_stat_5 union 
                          select * from merged_stat_6 union
                          select * from merged_stat_7 union 
                          select * from merged_stat_8 union 
                          select * from merged_stat_9 union
                          select * from merged_stat_10 order by RunId asc")

merged_stat__all_aggr <- sqldf("select RunId, avg(minUtility), avg(maxUtility), avg(avgUtility),
                          avg(minLUtility), avg(maxLUtility), avg(avgLUtility), 
                          avg(minRUtility), avg(maxRUtility), avg(avgRUtility) 
                          from merged_stat_all group by RunId")

pokrycie <- sqldf("select Experience_Decay, Agents_Count, Tasks_Count, 
                  count(*) as licznosc from batch_merged group by Experience_Decay, Agents_Count, Tasks_Count")
min(pokrycie$licznosc)
max(pokrycie$licznosc)
sd(pokrycie$licznosc)

library("scales")
library("ggplot2")
library("reshape2")
chosen_merged_stat__all_aggr <- merged_stat__all_aggr[,-1]
head(chosen_merged_stat__all_aggr)
head(as.matrix(chosen_merged_stat__all_aggr))
melted <- melt(chosen_merged_stat__all_aggr)
head(melted)
head(as.matrix(melted))
melted_col_names <- c('Obserwacja','Czestosc')
colnames(melted) <- melted_col_names

ggplot(melted, aes(x = Obserwacja, y = factor(Czestosc), fill = Obserwacja)) + 
  geom_bar(stat="identity", position = "dodge") + geom_point() + 
  scale_fill_brewer(palette = "Set1") +
  scale_y_discrete(breaks = pretty_breaks()) +
  theme(axis.text.y = element_text(), 
        axis.text.x = element_text(angle = 90, hjust = 1))
barplot(as.matrix(chosen_merged_stat__all_aggr), 
  col = heat.colors(12), main="Utility aggr",ylab="value",las=2)

# variant 1 - TRUE ; A 100 ; T 20

v1_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[1, c(1)] & 
                       Agents_Count == combination_of_param[1, c(2)] & 
                       Tasks_Count == combination_of_param[1, c(3)],
                     select=c("Run_Number"))
nrow(v1_id_runs)
v1_id_runs[,]
length(v1_id_runs[,])
scenario_1_t <- subset(merged, run %in% v1_id_runs[,], select=c(1,3,4,6))
nrow(scenario_1_t)
max(scenario_1_t$EvolutionIteration)
scenario_1 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_1_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p1_r <- 
  ggplot(scenario_1, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")

scenario_1_u <- subset(merged, run %in% v1_id_runs[,], select=c(1,3,4,10,11,12))
scenario_1_ut <- sqldf("select run, EvolutionGeneration, EvolutionIteration, 
                    avg(Utility) as avg_utility, avg(LUtility) as avg_lutility,
                    avg(RUtility) as avg_rutility
                    from scenario_1_u 
                    group by 
                    run, EvolutionGeneration, EvolutionIteration")
scenario_1_ut$grp <- paste(scenario_1_ut[,1],scenario_1_ut[,2])
p1_u <- 
  ggplot(scenario_1_ut, 
         aes(x=EvolutionIteration, y=avg_utility, group=grp)) +
  geom_point(aes(group = grp)) +
  ggtitle("Utility values through iterations (avg by all users)")
p1_ul <- 
  ggplot(scenario_1_ut, 
         aes(x=EvolutionIteration, y=avg_lutility, group=grp)) +
  geom_point(aes(group = grp)) +
  ggtitle("LUtility values through iterations (avg by all users)")
p1_ur <- 
  ggplot(scenario_1_ut, 
         aes(x=EvolutionIteration, y=avg_rutility, group=grp)) +
  geom_point(aes(group = grp)) +
  ggtitle("RUtility values through iterations (avg by all users)")
p1_multiplot_u <- multiplot(p1_u, p1_ul, p1_ur, cols=2)

p1_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p1_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p1_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p1_multiplot2 <- multiplot(p1_1, p1_2, p1_3, cols=1)

p1_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p1_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p1_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[1, c(1)] & 
                  Agents_Count == combination_of_param[1, c(2)] & 
                  Tasks_Count == combination_of_param[1, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p1_multiplot <- multiplot(p1_h_het, p1_h_hom, p1_h_pref, cols=1)

#############################################

# variant 2 -  FALSE ; Agents 100 ; Tasks 20

v2_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[2, c(1)] & 
                       Agents_Count == combination_of_param[2, c(2)] & 
                       Tasks_Count == combination_of_param[2, c(3)],
                     select=c("Run_Number"))
nrow(v2_id_runs)
v2_id_runs[,]
length(v2_id_runs[,])
scenario_2_t <- subset(merged, run %in% v2_id_runs[,], select=c(1,3,4,6))
nrow(scenario_2_t)
max(scenario_2_t$EvolutionIteration)
scenario_2 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_2_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p2_r <- 
  ggplot(scenario_2, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")
p2_r

p2_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p2_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p2_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p2_multiplot2 <- multiplot(p2_1, p2_2, p2_3, cols=1)

p2_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p2_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p2_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[2, c(1)] & 
                  Agents_Count == combination_of_param[2, c(2)] & 
                  Tasks_Count == combination_of_param[2, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p2_multiplot <- multiplot(p2_h_het, p2_h_hom, p2_h_pref, cols=1)

# variant 3 -  TRUE ; Agents 120 ; Tasks 20

v3_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[3, c(1)] & 
                       Agents_Count == combination_of_param[3, c(2)] & 
                       Tasks_Count == combination_of_param[3, c(3)],
                     select=c("Run_Number"))
nrow(v3_id_runs)
v3_id_runs[,]
length(v3_id_runs[,])
scenario_3_t <- subset(merged, run %in% v3_id_runs[,], select=c(1,3,4,6))
nrow(scenario_3_t)
max(scenario_3_t$EvolutionIteration)
scenario_3 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_3_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p3_r <- 
  ggplot(scenario_3, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")
p3_r

p3_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p3_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p3_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p3_multiplot2 <- multiplot(p3_1, p3_2, p3_3, cols=1)

p3_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p3_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p3_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[3, c(1)] & 
                  Agents_Count == combination_of_param[3, c(2)] & 
                  Tasks_Count == combination_of_param[3, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p3_multiplot <- multiplot(p3_h_het, p3_h_hom, p3_h_pref, cols=1)

# variant 4 -  FALSE ; Agents 120 ; Tasks 20

v4_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[4, c(1)] & 
                       Agents_Count == combination_of_param[4, c(2)] & 
                       Tasks_Count == combination_of_param[4, c(3)],
                     select=c("Run_Number"))
nrow(v4_id_runs)
v4_id_runs[,]
length(v4_id_runs[,])
scenario_4_t <- subset(merged, run %in% v4_id_runs[,], select=c(1,3,4,6))
nrow(scenario_4_t)
max(scenario_4_t$EvolutionIteration)
scenario_4 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_4_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p4_r <- 
  ggplot(scenario_4, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")
p4_r

p4_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p4_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p4_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p4_multiplot2 <- multiplot(p4_1, p4_2, p4_3, cols=1)

p4_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p4_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p4_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[4, c(1)] & 
                  Agents_Count == combination_of_param[4, c(2)] & 
                  Tasks_Count == combination_of_param[4, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p4_multiplot <- multiplot(p4_h_het, p4_h_hom, p4_h_pref, cols=1)

# variant 5 -  TRUE ; Agents 100 ; Tasks 50

v5_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[5, c(1)] & 
                       Agents_Count == combination_of_param[5, c(2)] & 
                       Tasks_Count == combination_of_param[5, c(3)],
                     select=c("Run_Number"))
nrow(v5_id_runs)
v5_id_runs[,]
length(v5_id_runs[,])
scenario_5_t <- subset(merged, run %in% v5_id_runs[,], select=c(1,3,4,6))
nrow(scenario_5_t)
max(scenario_5_t$EvolutionIteration)
scenario_5 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_5_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p5_r <- 
  ggplot(scenario_5, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")

scenario_5_u <- subset(merged, run %in% v5_id_runs[,], select=c(1,3,4,10,11,12))
scenario_5_ut <- sqldf("select run, EvolutionGeneration, EvolutionIteration, 
                       avg(Utility) as avg_utility, avg(LUtility) as avg_lutility,
                       avg(RUtility) as avg_rutility
                       from scenario_5_u 
                       group by 
                       run, EvolutionGeneration, EvolutionIteration")
scenario_5_ut$grp <- paste(scenario_5_ut[,1],scenario_5_ut[,2])
p5_u <- 
  ggplot(scenario_5_ut, 
         aes(x=EvolutionIteration, y=avg_utility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("Utility values through iterations (avg by all users)")
p5_ul <- 
  ggplot(scenario_5_ut, 
         aes(x=EvolutionIteration, y=avg_lutility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("LUtility values through iterations (avg by all users)")
p5_ur <- 
  ggplot(scenario_5_ut, 
         aes(x=EvolutionIteration, y=avg_rutility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("RUtility values through iterations (avg by all users)")
p5_multiplot_u <- multiplot(p5_u, p5_ul, p5_ur, cols=2)

p5_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p5_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p5_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p5_multiplot2 <- multiplot(p5_1, p5_2, p5_3, cols=1)

p5_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p5_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p5_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[5, c(1)] & 
                  Agents_Count == combination_of_param[5, c(2)] & 
                  Tasks_Count == combination_of_param[5, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p5_multiplot <- multiplot(p5_h_het, p5_h_hom, p5_h_pref, cols=1)


# variant 6 -  FALSE ; Agents 100 ; Tasks 50

v6_id_runs <- subset(batch_merged, 
                     Experience_Decay == combination_of_param[6, c(1)] & 
                       Agents_Count == combination_of_param[6, c(2)] & 
                       Tasks_Count == combination_of_param[6, c(3)],
                     select=c("Run_Number"))
nrow(v6_id_runs)
v6_id_runs[,]
length(v6_id_runs[,])
scenario_6_t <- subset(merged, run %in% v6_id_runs[,], select=c(1,3,4,6))
nrow(scenario_6_t)
max(scenario_6_t$EvolutionIteration)
scenario_6 <- sqldf("select run, EvolutionGeneration, TaskStrategy, 
                    count(TaskStrategy) as licznosc 
                    from scenario_6_t 
                    where EvolutionIteration = 20
                    group by 
                    run, EvolutionGeneration, TaskStrategy")

p6_r <- 
  ggplot(scenario_6, 
         aes(x=EvolutionGeneration, y=licznosc, colour=TaskStrategy, group=run)) +
  geom_point(aes(group = run)) +
  ggtitle("Set of strategies, grouped, through generations")

scenario_6_u <- subset(merged, run %in% v6_id_runs[,], select=c(1,3,4,10,11,12))
scenario_6_ut <- sqldf("select run, EvolutionGeneration, EvolutionIteration, 
                       avg(Utility) as avg_utility, avg(LUtility) as avg_lutility,
                       avg(RUtility) as avg_rutility
                       from scenario_6_u 
                       group by 
                       run, EvolutionGeneration, EvolutionIteration")
scenario_6_ut$grp <- paste(scenario_6_ut[,1],scenario_6_ut[,2])
p6_u <- 
  ggplot(scenario_6_ut, 
         aes(x=EvolutionIteration, y=avg_utility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("Utility values through iterations (avg by all users)")
p6_ul <- 
  ggplot(scenario_6_ut, 
         aes(x=EvolutionIteration, y=avg_lutility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("LUtility values through iterations (avg by all users)")
p6_ur <- 
  ggplot(scenario_6_ut, 
         aes(x=EvolutionIteration, y=avg_rutility, group=grp)) +
  geom_point(aes(group = grp)) +
  geom_smooth(na.rm = TRUE, method = 'glm') +
  ggtitle("RUtility values through iterations (avg by all users)")
p6_multiplot_u <- multiplot(p6_u, p6_ul, p6_ur, cols=2)

p6_1 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Generation, fill=Heterophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Heterophily_Count ~ .) +
  ggtitle("How many generations it took")
# combination_of_param[1, c(1)]
p6_2 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Generation, fill=Homophily_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Homophily_Count ~ .) +
  ggtitle("How many generations it took")
p6_3 <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Generation, fill=Preferential_Count)) +
  geom_histogram(colour="black", binwidth=30) +
  facet_grid(Preferential_Count ~ .) +
  ggtitle("How many generations it took")
p6_multiplot2 <- multiplot(p6_1, p6_2, p6_3, cols=1)

p6_h_pref <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Preferential_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Preferential_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p6_h_het <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Heterophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Heterophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p6_h_hom <- 
  ggplot(subset(batch_merged, 
                Experience_Decay == combination_of_param[6, c(1)] & 
                  Agents_Count == combination_of_param[6, c(2)] & 
                  Tasks_Count == combination_of_param[6, c(3)]), 
         aes(x=Homophily_Count)) + 
  geom_histogram(binwidth=1, colour="black", fill="white") +
  geom_vline(aes(xintercept=mean(Homophily_Count, na.rm=T)),
             color="red", linetype="dashed", size=1) +
  xlim(c(0,120))
p6_multiplot <- multiplot(p6_h_het, p6_h_hom, p6_h_pref, cols=1)

unusual_observations <- subset(
  batch_merged, Homophily_Count > 0 | Preferential_Count > 0)
View(unusual_observations)