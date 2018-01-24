# install.packages("RMySQL")
library(RMySQL)

con <- dbConnect(MySQL(), dbname="bigmovie", user="root", password="1234")
values <- dbGetQuery(con, "SELECT COUNT(title) AS amount, release_year
FROM movies, (
             SELECT movie_id 
             FROM movie_country
             WHERE country_id = 10
             ) AS c
                     WHERE id = c.movie_id AND release_year != 0
                     GROUP BY release_year")
jpeg(filename='video-format.jpg')
barplot(values$amount, names.arg = values$release_year, xlab = "Year", ylab = "Number of movies", horiz=FALSE, cex.names=1)
dev.off()
