package edu.man.util;

public class LevenshteinDistance {
  public static int get(String s, String t) {
    if (s == null || t == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }
    int n = s.length(); // length of s
    int m = t.length(); // length of t
    if (n == 0) {
      return m;
    } else if (m == 0) {
      return n;
    }

    int p[] = new int[n+1]; //'previous' cost array, horizontally
    int d[] = new int[n+1]; // cost array, horizontally
    int _d[]; //placeholder to assist in swapping p and d

    // indexes into strings s and t
    int i; // iterates through s
    int j; // iterates through t

    char t_j; // jth character of t

    int cost; // cost

    for (i = 0; i<=n; i++) {
       p[i] = i;
    }
    for (j = 1; j<=m; j++) {
      t_j = t.charAt(j-1);
      d[0] = j;
      for (i=1; i<=n; i++) {
        cost = s.charAt(i-1)==t_j ? 0 : 1;
        d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
      }

      _d = p;
      p = d;
      d = _d;
    }
    return p[n];
  }
}
