#!/usr/bin/Rscript

# install.packages("RMySQL")
library(RMySQL)

con <- dbConnect(MySQL(), dbname="bigmovie", user="root", password="1234")
values <- dbGetQuery(con, "select genres.genre as format, count(title) as freq from genres where genre = 'Horror'")

invisible(jpeg('video-format.jpg'))
barplot(values$freq, names.arg = values$format, horiz=FALSE, cex.names=0.5)
invisible(dev.off())



