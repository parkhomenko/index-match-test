### Overview

This application connects to a Fauna database and runs several times the following:
1. Creates a random collection
2. Creates an empty document in the collection
3. Creates an index
4. Creates a document matching a created index
5. Runs a search by the index and applies a ContainsValue function to check whether a document with matching index criteria exists

Here is one of the runs examples:
```
0 run: false
1 run: false
2 run: false
3 run: false
4 run: true
5 run: false
6 run: true
7 run: false
8 run: true
9 run: false
```

You can see from the above results that the same index sometimes finds a document and sometimes not.

### Running the application
The application uses fauna managed database service, you'll need to export 2 environment variables to access it:
```
export FAUNA_DB_HOST=https://your-fauna-db-host
export FAUNA_SECRET_KEY=your-secret-key
```

You can then run the application using gradle command:
```
./gradlew run
```
