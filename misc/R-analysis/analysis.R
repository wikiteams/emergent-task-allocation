setwd("~/symulator/simphony_model_1422759723448/")

library(dplyr)
library(sqldf)

instance_1 <- read.csv('instance_1/agents_nonaggr_state.2015.lut.01.04_04_09.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_2 <- read.csv('instance_2/agents_nonaggr_state.2015.lut.01.04_04_11.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_3 <- read.csv('instance_3/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_4 <- read.csv('instance_4/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_5 <- read.csv('instance_5/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_6 <- read.csv('instance_6/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_7 <- read.csv('instance_7/agents_nonaggr_state.2015.lut.01.04_04_11.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_8 <- read.csv('instance_8/agents_nonaggr_state.2015.lut.01.04_04_11.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_9 <- read.csv('instance_9/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")
instance_10 <- read.csv('instance_10/agents_nonaggr_state.2015.lut.01.04_04_10.csv', sep=";", quote="\"", header = TRUE, dec=".")

selected_columns <- c("run", "tick", "EvolutionGeneration", "EvolutionIteration", "Id", 
            "TaskStrategy", "SkillStrategy", "WasWorkingOnAnything", "GeneralExperience", "Utility", "LUtility", "RUtility")

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

write.table(instance_1[selected_columns], "data_ts_1.dat", sep = ",", quote = FALSE, dec = ".")
data_1 <- file("data_ts_1.dat")
write.table(instance_2[selected_columns], "data_ts_2.dat", sep = ",", quote = FALSE, dec = ".")
data_2 <- file("data_ts_2.dat")
write.table(instance_3[selected_columns], "data_ts_3.dat", sep = ",", quote = FALSE, dec = ".")
data_3 <- file("data_ts_3.dat")
write.table(instance_4[selected_columns], "data_ts_4.dat", sep = ",", quote = FALSE, dec = ".")
data_4 <- file("data_ts_4.dat")

merged <- sqldf("select * from data_1 union select * from data_2 union select * from data_3 union select * from data_4", 
                dbname = tempfile(tmpdir = c("~/symulator/simphony_model_1422759723448/")))
