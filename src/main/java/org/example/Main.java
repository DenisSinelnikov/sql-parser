package org.example;

import org.example.model.Query;
import org.example.parser.SqlParser;

public class Main {

    public static void main(String[] args) {
        String sql = """
                SELECT author.name, COUNT(book.id), SUM(book.cost)
                FROM (SELECT * FROM author WHERE active = 1) a
                LEFT JOIN book b ON a.id = b.author_id
                WHERE a.age > 30 AND b.cost > 100
                GROUP BY author.name
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                ORDER BY SUM(book.cost) DESC
                LIMIT 10 OFFSET 5
                """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        System.out.println(QueryPrinter.print(query));
    }
}