install.packages("Rcpp")
install.packages("dplyr")
library(dplyr)

setwd("~/simphony_model_1422230575260/instance_1")
dane_instance_1 <- read.csv('agents_nonaggr_state.2015.Jan.26.01_06_26.csv', sep=";", quote="\"", header = TRUE, dec=".")
dane_instance_1 <- head(dane_instance_1, -1)
setwd("~/simphony_model_1422230575260/instance_2")
dane_instance_2 <- read.csv('agents_nonaggr_state.2015.Jan.26.01_06_26.csv', sep=";", quote="\"", header = TRUE, dec=".")
dane_instance_2 <- head(dane_instance_2, -1)

# dane_pogrup <- group_by(dane, EvolutionGeneration, Id, TaskStrategy)
dane_pogrup <- group_by(dane_instance_1[dane_instance_1$EvolutionIteration == 1,], EvolutionGeneration, Id, TaskStrategy)
summarize (  group_by(dane_pogrup, EvolutionGeneration, TaskStrategy), StratCount = n() )
wyniki <- summarize (  group_by(dane_pogrup, EvolutionGeneration, TaskStrategy), StratCount = n() )

p <- ggplot() + 
  geom_line(data = wyniki[wyniki$TaskStrategy == "HETEROPHYLY_EXP", ], aes(x = EvolutionGeneration, y = StratCount, color = "heterophyly")) +
  geom_line(data = wyniki[wyniki$TaskStrategy == "HOMOPHYLY_EXP", ], aes(x = EvolutionGeneration, y = StratCount, color = "homophyly"))  +
  geom_line(data = wyniki[wyniki$TaskStrategy == "PREFERENTIAL", ], aes(x = EvolutionGeneration, y = StratCount, color = "preferential"))  +
  xlab('EvolutionGeneration') +
  ylab('StratCount') + labs(color="Strategies")