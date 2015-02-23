#   TODO list
# 
#   - batch.log jest nadpisywany i zostaje tylko jedna wartosc. sprawdzic dlaczego tak jest.
# 
#   - zalozyc indeksy na obiekt merged oraz inny
# 
#   - sjoinowac batche z instancjami
#   - znalezc funkcje do wykresu z wieloma wieloma y-wartosciami
#   - moze jakas heatmapa ?
#   - combn do wyciagniecia kombinacji

setwd("~/symulator/simphony_model_1423341325045/")

library(dplyr)
library(sqldf)

instance_1 <- read.csv2('instance_1/agents_nonaggr_state.2015.lut.07.21_38_09.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_2 <- read.csv2('instance_2/agents_nonaggr_state.2015.lut.07.21_38_07.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_3 <- read.csv2('instance_3/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_4 <- read.csv2('instance_4/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_5 <- read.csv2('instance_5/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_6 <- read.csv2('instance_6/agents_nonaggr_state.2015.lut.07.21_38_07.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_7 <- read.csv2('instance_7/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_8 <- read.csv2('instance_8/agents_nonaggr_state.2015.lut.07.21_38_09.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_9 <- read.csv2('instance_9/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")
instance_10 <- read.csv2('instance_10/agents_nonaggr_state.2015.lut.07.21_38_08.csv', sep=";", quote="\"", header = TRUE, dec=",")

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
nrow(batch_1) + nrow(batch_2) + nrow(batch_3) + nrow(batch_4) + nrow(batch_5) +
  nrow(batch_6) + nrow(batch_7) + nrow(batch_8) + 
  nrow(batch_9) + nrow(batch_10)

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

selected_columns <- c("run", "tick", "EvolutionGeneration", "EvolutionIteration", "Id", 
            "TaskStrategy", "SkillStrategy", "WasWorkingOnAnything", "GeneralExperience", "Utility", "LUtility", "RUtility")

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
                dbname = tempfile(tmpdir = c("~/symulator/simphony_model_1423341325045/")))

sqldf("create index idx_merged_gen on merged(EvolutionGeneration)")
sqldf("create index idx_merged_run on merged(run)")
sqldf("create index idx_merged_genrun on merged(EvolutionGeneration, run)")

head(batch_1)
names(batch_1)
head(merged)
names(merged)

# wygeneruj vector kombinacji

combination_of_param <- expand.grid(
  c("LearningSkills","LeftLearningSkills","RightLearningSkills"), 
  c(TRUE, FALSE), 
  c(20, 50, 100), 
  c("choice","greedy","proportional","random"), 
  c(100, 120),
  c(100, 20, 50))
typeof(combination_of_param)
combination_of_param

# bardzo duzo kombinacji parametrow symulatora :( 
# nastepnym razem policz tylko 2 × 2 × 2 , co da 8 kombinacji
# w takim razie forkuje kod do analysis-script-simplified.R

my_col_names <- c(
  'Utility', 
  'ExpDecay', 
  'GenerationLength', 
  'SkillStrategy', 
  'AgentCount', 
  'TaskCount')
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