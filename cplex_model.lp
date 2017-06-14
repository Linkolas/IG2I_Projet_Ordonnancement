\ENCODING=ISO-8859-1
\Problem name: ilog.cplex

Minimize
 obj: 10 x1 + 12 x2 + 18 x3 + 16 x4
Subject To
 c1: x1 + x3 + x4  = 1
 c3: x2 + x3 + x4  = 1
Bounds
 0 <= x1 <= 1
 0 <= x2 <= 1
 0 <= x3 <= 1
 0 <= x4 <= 1
Binaries
 x1  x2  x3  x4 
End
