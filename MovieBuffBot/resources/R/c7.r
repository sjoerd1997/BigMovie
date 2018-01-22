# install.packages("RMySQL")
library(RMySQL)

con <- dbConnect(MySQL(), dbname="bigmovie", user="root", password="1234")
usa <- dbGetQuery(con, "
SELECT COUNT(m.movie_id)
FROM (
  SELECT movie_id
  FROM movie_genre AS m
  WHERE genre_id = (SELECT id FROM genres where genre = 'horror')
) AS m
INNER JOIN (
  SELECT movie_id
  FROM movie_country
  WHERE country_id = (SELECT id FROM countries where country = 'usa')
) AS a
ON m.movie_id = a.movie_id;
")
france <- dbGetQuery(con, "
SELECT COUNT(m.movie_id)
FROM (
  SELECT movie_id
  FROM movie_genre AS m
  WHERE genre_id = (SELECT id FROM genres where genre = 'horror')
) AS m
INNER JOIN (
  SELECT movie_id
  FROM movie_country
  WHERE country_id = (SELECT id FROM countries where country = 'france')
) AS a
ON m.movie_id = a.movie_id;
")

jpeg(filename='c7.jpeg')
slices <- c(as.integer(usa), as.integer(france))
lbls <- c("USA", "France")
pie(slices, labels = lbls, main="Pie Chart of Horror Movies")
dev.off()
