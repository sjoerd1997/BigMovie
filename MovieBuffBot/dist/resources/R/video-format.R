#!/usr/bin/Rscript

# install.packages("RMySQL")
library(RMySQL)

con <- dbConnect(MySQL(), dbname="bigmovie", user="root", password="1234")
values <- dbGetQuery(con, "SELECT genre AS format, COUNT(title) AS freq FROM genres WHERE genre LIKE '%Horror%'")

jpeg(filename='video-format.jpg')
barplot(values$freq, names.arg = values$format, horiz=FALSE, cex.names=0.5)
dev.off()
