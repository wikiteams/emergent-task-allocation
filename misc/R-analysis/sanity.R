install.packages("Rcpp")
install.packages("dplyr")
library(dplyr)

# dane_pogrup <- group_by(dane, EvolutionGeneration, Id, TaskStrategy)
dane_pogrup <- group_by(dane[dane$EvolutionIteration == 1,], EvolutionGeneration, Id, TaskStrategy)
summarize (  group_by(dane_pogrup, EvolutionGeneration, TaskStrategy), StratCount = n() )