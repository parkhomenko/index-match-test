package com.parkhomenko;

import com.faunadb.client.FaunaClient;
import com.faunadb.client.query.Expr;
import com.faunadb.client.query.Language;
import com.faunadb.client.types.Field;
import com.faunadb.client.types.Value;
import com.faunadb.client.types.Value.*;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.faunadb.client.query.Language.*;
import static com.faunadb.client.types.Codec.REF;

public class Main {
    private static final Field<Value.RefV> REF_FIELD = Field.at("ref").to(REF);
    private static FaunaClient faunaClient;

    public static void main(String[] args) throws Exception {
        String url = System.getenv("FAUNA_DB_HOST");
        String secret = System.getenv("FAUNA_SECRET_KEY");

        faunaClient = FaunaClient.builder()
            .withEndpoint(url)
            .withSecret(secret)
            .build();

        Main main = new Main();

        for (int i = 0; i < 10; i++) {
            main.runIndexMatchingQuery(i);
        }
    }

    public void runIndexMatchingQuery(int currentRun) throws Exception {
        RefV collectionRef = onARandomCollection();
        String indexName = randomStartingWith("index_");

        query(Create(collectionRef, Obj())).get();

        query(
                CreateIndex(Obj(
                        "name", Language.Value(indexName),
                        "source", collectionRef,
                        "active", Language.Value(true),
                        "values", Arr(Obj("field", Arr(Language.Value("data"), Language.Value("value")))),
                        "terms", Arr(Obj("field", Arr(Language.Value("data"), Language.Value("value"))))
                ))
        ).get();

        query(Create(collectionRef, Obj("data", Obj("value", Language.Value("foo"))))).get();

        Value result = query(
                ContainsValue(
                        Language.Value("foo"),
                        Match(Index(Language.Value(indexName)), Language.Value("foo"))
                )
        ).get();

        System.out.println(currentRun + " run: " + result);
    }

    private RefV onARandomCollection() throws Exception {
        Expr expr = CreateCollection(Obj("name", Value(randomStartingWith("some_collection_"))));
        Value clazz = query(expr).get();

        return clazz.get(REF_FIELD);
    }

    private CompletableFuture<Value> query(Expr expr) {
        return faunaClient.query(expr);
    }

    private String randomStartingWith(String... parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts)
            builder.append(part);

        builder.append(Math.abs(new Random().nextLong()));
        return builder.toString();
    }
}
