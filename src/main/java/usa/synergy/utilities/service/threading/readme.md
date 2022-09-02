# Futures and caches

The caches and futures inside CubeLib help take complex Java objects and simplifies them
down to our needs, while still providing complex functionality.

## SimpleFuture

The simple future is the object that lays at the base of all the functionality for these
libraries. This class serves as a simple wrapper for the Java wrapper, but adds functionality
for running methods in the Bukkit main thread for example, and is also easier to understand
compared to the default Java CompletableFuture.

### Creating a SimpleFuture

Lot's libraries will already give you a
SimpleFuture, however if you wish to create some, here is how you would go about it.

---

`SimpleFuture.of(CompletableFuture)`

If you have a CompletableFuture object you can simply turn it to a SimpleFuture by using
the above code.

---

`SimpleFuture.completed(Object)`

This will create a completed SimpleFuture. This means that any calls to it will return the
value supplied instantly.

---

`SimpleFuture.compute(Supplier<T>)`

This will create a SimpleFuture that requires the supplied task to be computed to get the
value. The task is run async hence not impacting any performance in the main thread. Do
not use this for really simple tasks, such as adding 2 numbers as the overhead of scheduling
this task to another thread is more effort than it'll save.

### Getting values from a SimpleFuture

When getting a value from a SimpleFuture, it will require you to handle the value, and any
exceptions thrown while trying to get it. It is highly suggested that you do something with
the the exception and don't ignore it's existance, even if it is just sending a message to a
player.

`SimpleFuture#get()` and `SimpleFuture#getUnchecked()`

These methods will return the value from within the SimpleFuture. They will block the thread
that you are currently in, and are not recommended for use. They are acceptable to be used
if you are 100% in an async thread that does not matter if it is being blocked. The unchecked
method does not require you to surround it in a try catch.

`SimpleFuture#getSameThread()`

This method it is very important that you understand what it does before you use it. This will
get the value, however it will run in the thread that it's currently in. This means there are 2
cases.

A) The value from the future has already been calculated. This will be run instantly in the
thread that just called this method.

B) The value is still being calculated. Once the value has been calcualated, the supplied code
will be run in the thread that just supplied the result to the future.

This is recommended for lightweight tasks.

`SimpleFuture#getSync()`

_This only really affects Bukkit. If this is called in bungeecord, it will just schedule an async
task._

This will force the code supplied to this method in Bukkit's main thread. There are 2 cases
to help improve performance of this method. If this is called inside the main thread, and
the value from the future is already calculated, it will instantly run the code supplied, however
if the future is not completed, or it is not called from the main thread, a Bukkit runnable is
created once the value has been obtained, and the supplied code is run.

`SimpleFuture#getAsync()`

This will force the computed value into a new thread.

_Note: Please make sure that you handle the SimpleSuccessFuture returned, by default the get methods
will not print your exception, however appending `printStackTrace()` to the end will print it out_

### Applying changes to a SimpleFuture

Applying changes to SimpleFutures is most probabally the most power feature that this supplies.
Applying changes has the same set of methods as listed value (sync, sameThread and aync) which
can be found named `thenSync`, `thenSameThread` and `thenAsync`. What they allow you to do is
modify the result of the SimpleFuture. I think the best way to explain how this works is with a
Chunk of wicked looking code. For example:


```java
double randomNumber = Math.random() * 100000;
SimpleFuture<Double> sqrtFuture = SimpleFuture.compute(() -> {
    return Math.sqrt(randomNumber);//Our intense task.
});
SimpleFuture<BlockState> blockStateFuture = sqrtFuture.thenSync(computedValue -> {
    //A task that requires the code to operate in the main thread.
    return Bukkit.getWorld("world").getBlockAt((int) computedValue, 0, (int) computedValue).getState();
});
blockStateFuture.getAsync(blockState -> {
    //Another intense task.
    DatabaseAPI.i().executeUpdate("INSERT INTO blockState blah blah blah, you get the idea");
});
```
As you can see, in my really odd task above, I got a random number, square rooted it,
then wanted to lookup a block in the world at that location, however I needed to do this
from the main thread, otherwise Bukkit would complain, hence why I use the `thenSync` method,
and then for the last complex task, I get the block state async and then insert it into the database.
(Yes, I understand that DatabaseAPI doesn't block the thread you're in, but... image it blocks that thread)


## SimpleSuccessFuture

A SimpleSuccessFuture is basically a SimpleFuture however without a value. A prime example of
where a SimpleSuccessFuture would be used is adding points to a player. Adding the points will
be done in the Database thread, and once the transaction has been completed, it will notify the
SimpleSuccessFuture that it's also been done. It's also worth noting that they carry exceptions
still, hence meaning if adding the points failed, it will fail with the exception for why it didn't
add them. This also means that you can notify the player that an exception occured.

A method that I've recently added that makes life so much easier is the errorMessage method,
this means that if the future does fail it sends the message to the specified player, and prints
the exception out to the console.

```java
LootPluginAPI.getLootPlayer(player).getCubeletData().thenJoinSuccessSameThread(cubeletData -> {
    return LootPluginAPI.getLootAPI().getCubeletTypeRepository().getCubeletType("normal").thenJoinSuccessSameThread(cubeletType -> {
        return cubeletData.giveCubelet(cubeletType, TimeUnit.DAYS, 7);
    });
}).errorMessage(player, ChatColor.RED + "An error occured rewarding you your Cubelets!", BukkitPlatform.i()).printStackTrace();
```

As you can see in the code above, it uses the SimpleFutures `thenJoinSuccessSameThread` method,
which allows you to convert the SimpleFuture into a SimpleSuccessFuture once it's completed. If
you follow the code above, what it does, is gets the users Cubelet data on the first line. Once
that's completed it then gets the Cubelet Type "normal" and then once that's loaded, it will give
a Cubelet of that type to the users Cubelet data, and as Cubelet data returns a SimpleSuccessFuture,
we will know if that adding the new Cubelet fails, this will be passed back up. On the last line,
you can see we send an error message. This means that if any part of this process throws and exception,
it'll be caught by the error message handler and send the message to the user notifying them
that something went wrong.

SimpleSuccessFutures are also the result of any 'get' operation on a SimpleFuture. This means that
error handling falls the responsibility of the SimpleSuccessFuture. Fortunatly, there are some methods,
that allows you to simply chain methods to the SimpleSuccessFuture. For example the most simple on is
printStackTrace(), which will log any exception caught by the SimpleFuture. Remeber, this may be any
exception. This may be an exception with getting the value to complete the SimpleFuture, or it may
be an exception thrown inside your code that handles the result. There are also methods like errorMessage()
and getIfError...() if your wishing to do more with your exception. I highly suggest that you look through
all the methods inside all the classes before starting to use these libraries.


# CachingOperation

A caching operation allows you to cache values to complex operations. The values also will
have a timeout, and after this time the value will be calculated again. There are 2 main
methods you need to worry about. Both of these return CompletableFuture and can simply
be turned to a SimpleFuture using `SimpleFuture.of(future)`

`CachingOperation#getResult()`

If there is no value in the cache, or the cache has timed out, this will wait for the
operation to complete, and then future you are given will complete. If the data inside
the cache is currently valid, the returned future will be completed.

`CachingOperation#getResultQuickIfPossible()`

The only time this will return an incomplete future is if the cache has not been previously
calculated prior to this (or if it's invalidated, see later). This means if you call it, 
and the cache is outdated, then the returned future will be completed with the previous value.
If this does happen where the cache is outdated, the cache will still update in the background.

`CachingOperation#invalidate()`

This will invalidate your cache. Once a get method is called again the value inside it will
have to be recalculated. This can be useful when you realise that the cache is outdated because
some data that you've loaded doesn't match up with the values in another cache.

To create a CachingOperation just make a new CachingOperation object and override the `get()` method.
Be aware that the get method always is called async.

## SimpleLoader

A SimpleLoader is the same as a cache, but has no timeout. For example if you need to load some
player details, to stop burst queries to out database, you can put the data loading into a
SimpleLoader inside a player object. This means that when some code requires this data, it
will be loaded on request. To simply get the data all you need to do is call `get()`

To create a SimpleLoader just make a new SimpleLoader object and override the `load()` method.
Be aware that the load method always is called async.