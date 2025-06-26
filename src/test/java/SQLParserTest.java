import org.example.model.Join;
import org.example.model.Query;
import org.example.model.Source;
import org.example.model.Where;
import org.example.parser.SqlParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SQLParserTest {

    @Test
    public void testSimpleSelect() {
        String sql = "SELECT * FROM book";
        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getFromSources().size());
        assertEquals("book", query.getFromSources().getFirst().name());
    }

    @Test
    public void testSelectWithWhereAndGroup() {
        String sql = """
                SELECT author, COUNT(id)
                FROM book
                WHERE year > 2000 AND cost > 100
                GROUP BY author
                HAVING COUNT(id) > 5
                """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getGroupByColumns().size());
        assertEquals(1, query.getHaving().size());
        assertEquals(2, query.getWhere().size());
    }

    @Test
    public void testSelectWithJoin() {
        String sql = """
                SELECT a.name, b.cost
                FROM author a
                INNER JOIN book b ON a.id = b.author_id
                """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getJoins().size());
        assertEquals("INNER JOIN", query.getJoins().getFirst().type());
    }

    @Test
    public void testSelectWithLimitOffset() {
        String sql = """
                SELECT name FROM user LIMIT 50 OFFSET 10
                """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(50, query.getLimit());
        assertEquals(10, query.getOffset());
    }

    @Test
    public void testSelectWithMultipleJoins() {
        String sql = """
            SELECT u.name, o.id, p.price
            FROM user u
            INNER JOIN orders o ON u.id = o.user_id
            LEFT JOIN product p ON o.product_id = p.id
            WHERE u.active = 1
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getWhere().size());
        assertEquals(2, query.getJoins().size());
        assertEquals("INNER JOIN", query.getJoins().get(0).type());
        assertEquals("LEFT JOIN", query.getJoins().get(1).type());
    }

    @Test
    public void testSelectWithGroupByAndHavingMultipleConditions() {
        String sql = """
            SELECT department, COUNT(employee_id), AVG(salary)
            FROM employees
            GROUP BY department
            HAVING COUNT(employee_id) > 5 AND AVG(salary) > 3000
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getGroupByColumns().size());
        assertEquals(2, query.getHaving().size());
    }

    @Test
    public void testSelectWithoutWhereOrHaving() {
        String sql = """
            SELECT name
            FROM customers
            ORDER BY name ASC
            LIMIT 20
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(0, query.getWhere().size());
        assertEquals(0, query.getHaving().size());
        assertEquals(1, query.getSortColumns().size());
        assertEquals(20, query.getLimit());
    }

    @Test
    public void testSelectWithSubqueryInFromWithoutAlias() {
        String sql = """
            SELECT t.id
            FROM (SELECT * FROM transactions) t
            WHERE t.amount > 1000
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getFromSources().size());
        assertTrue(query.getFromSources().getFirst().name().startsWith("(SELECT"));
        assertEquals("t", query.getFromSources().getFirst().alias());
        assertEquals(1, query.getWhere().size());
    }

    @Test
    public void testSelectWithExplicitOrderDirections() {
        String sql = """
            SELECT name, created_at
            FROM users
            ORDER BY name ASC, created_at DESC
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(2, query.getSortColumns().size());
        assertEquals("ASC", query.getSortColumns().get(0).direction());
        assertEquals("DESC", query.getSortColumns().get(1).direction());
    }

    @Test
    public void testSelectWithEmptyHavingGroup() {
        String sql = """
            SELECT city, COUNT(order_id)
            FROM orders
            GROUP BY city
            HAVING COUNT(order_id) > 10
            """;

        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);

        assertEquals(1, query.getGroupByColumns().size());
        assertEquals(1, query.getHaving().size());
        assertTrue(query.getHaving().getFirst().condition().contains("COUNT"));
    }

    @Test
    void testWhereConditionsFromPublicParse() {
        SqlParser parser = new SqlParser();
        String sql = "SELECT * FROM users WHERE active = 1 AND age > 18";
        Query query = parser.parse(sql);
        List<Where> where = query.getWhere();
        assertEquals(2, where.size());
        assertEquals("active = 1", where.get(0).condition());
        assertEquals("age > 18", where.get(1).condition());
    }

    @Test
    void testJoinParsingFromPublicParse() {
        String sql = """
        SELECT * FROM users u
        LEFT JOIN orders o ON u.id = o.user_id
        INNER JOIN payments p ON o.id = p.order_id
        """;
        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);
        List<Join> joins = query.getJoins();
        assertEquals(2, joins.size());

        Join join1 = joins.getFirst();
        assertEquals("LEFT JOIN", join1.type());
        assertEquals("orders", join1.table());
        assertEquals("o", join1.alias());
        assertEquals("u.id = o.user_id", join1.onCondition());

        Join join2 = joins.get(1);
        assertEquals("INNER JOIN", join2.type());
        assertEquals("payments", join2.table());
        assertEquals("p", join2.alias());
        assertEquals("o.id = p.order_id", join2.onCondition());
    }

    @Test
    void testFromSourcesWithSubqueryAndAlias() {
        String sql = "SELECT * FROM (SELECT * FROM data) d, users u";
        SqlParser parser = new SqlParser();
        Query query = parser.parse(sql);
        List<Source> sources = query.getFromSources();
        assertEquals(2, sources.size());

        Source subquerySource = sources.getFirst();
        assertTrue(subquerySource.name().startsWith("("));
        assertEquals("d", subquerySource.alias());

        Source second = sources.get(1);
        assertEquals("users", second.name());
        assertEquals("u", second.alias());
    }

}
