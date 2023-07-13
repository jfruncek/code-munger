
println "System properties:"
System.getProperties().each { println it }

println "\n\nSystem environment:"
System.getenv().each { println it }
