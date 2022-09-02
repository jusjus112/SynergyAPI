package usa.synergy.utilities.service.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SynergyThreads {

  //ConcurrentUtils.buildMeAFactory(ConcurrentUtils.ROOT_THREAD_GROUP, "GameFrameworkThreads", "GameFrm")

  public static final Executor SERVICE = new ThreadPoolExecutor(0,Integer.MAX_VALUE,
      60L, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory()
  );

}
