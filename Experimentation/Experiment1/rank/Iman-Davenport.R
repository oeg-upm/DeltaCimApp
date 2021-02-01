library("scmamp")
library("ggplot2")
library("Rgraphviz")


alpha <- 0.05
csv <- read.csv(file="./rank/averaged-results.csv", header=FALSE, sep=";")
values <- as.matrix(csv) 
multipleComparisonTest(data=values, test="iman", alpha=alpha)
post.results <- postHocTest(data=values, test="aligned ranks", correct="bergmann", use.rank=TRUE)
post.results

alg.order <- order(post.results$summary)
plt <- plotPvalues(post.results$corrected.pval, alg.order=alg.order)
plt
drawAlgorithmGraph(post.results$corrected.pval, mean.value=post.results$summary, alpha=alpha,  font.size=10)