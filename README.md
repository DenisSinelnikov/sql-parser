# SQL SELECT Query Parser

Simple, extensible SQL SELECT query parser written in Java 21.

## Features

✅ Parses `SELECT` columns  
✅ Supports `FROM` (including subqueries like `FROM (SELECT ...) alias`)  
✅ Supports explicit `JOIN` types (`INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, `FULL JOIN`)  
✅ Parses `WHERE` clauses  
✅ Parses `GROUP BY`  
✅ Parses `HAVING` clauses  
✅ Parses `ORDER BY`  
✅ Parses `LIMIT` and `OFFSET`  
✅ Full support for composite clauses (`AND` conditions)  
✅ Supports multiple `JOIN` and multiple `WHERE`, `HAVING` conditions

## Structure

```java
class Query {
    List<String> columns;
    List<Source> fromSources;
    List<Join> joins;
    List<WhereClause> whereClauses;
    List<String> groupByColumns;
    List<HavingClause> havingClauses;
    List<Sort> sortColumns;
    Integer limit;
    Integer offset;
}
