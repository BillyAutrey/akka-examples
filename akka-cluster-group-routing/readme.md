# Akka Cluster + Group Routing

## Running

There are a few ways to run the application.  You can start by running the following:

```sbtshell
sbt "runMain com.example.Application"
```

Once you have this running, the cluster will be up with a parent actor (as a singleton) and several worker actors.  To see work submit to this system, add a client to the cluster in a new terminal:

```sbtshell
sbt "runMain com.example.ClientMain"
```

If you would like to see more interesting behavior, run the following commands.  Each command should be in a new terminal window:

```sbtshell
sbt "runMain com.example.Application 2551"
sbt "runMain com.example.Application 2552"
sbt "runMain com.example.Application 0"
sbt "runMain com.example.WorkerMain"
sbt "runMain com.example.ClientMain"

```

The three terminals that ran Application will all have the actor singleton, and a worker.  You can terminate 2551, and see the singleton move to another cluster member with the "parent" role.

## Tests

To test this application, you will need to run the following command:

```sbtshell
sbt multi-jvm:test
```

### Test Structure

This project uses the SBT multi-jvm plugin.  Tests are all in `src/main/multi-jvm/scala`.  ApplicationTest contains the tests that will be executed by this command.

