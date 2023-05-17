package iotInfrustructure.gateWay.threadPool;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.*;


/*
 * ThreadPool doesn't support multithreaded program.

 * new ThreadPool(), would wait for submit(),
     -is possible to use new ThreadPool(0), for not waiting for submit();

 * cancel() method of Future, boolean argument mayInterruptIfRunning always should be false,
   ThreadPool doesn't support in interrupt running thread.

 * using shutdown()  after pause() without using resume() ,all submitted tasks (non canceled) anyway would be done.

 * shutdown() and  awaitTermination(), finish all submitted tasks
     - shutdown: main/user thread doesn't wait to finish all submitted tasks.
     - awaitTermination: main/user thread wait to finish all submitted tasks.
*/


public class ThreadPool implements Executor {

    private Hashtable<Long, WorkerThread> threadsTable = new Hashtable<>();
    private final WaitablePQueue<TaskWrapper> taskPQ;
    private boolean hasShutdown = false; /* (for throwing exception in submit) */
    private boolean isPaused = false;
    private int numberOfThreads = 0;

    private final Object synchronizeWithUsrThread = new Object();
    private final Object synchronizeForPause = new Object();

    /*-------------------------------------------------------------------------*/

    public ThreadPool(int numberOfThreads){
        taskPQ = new WaitablePQueue<>(new Comparator<TaskWrapper>() {
            @Override
            public int compare(TaskWrapper task1, TaskWrapper task2) {
                return task2.priority - task1.priority ;
            }
        });
        ThreadsCreateAndStartRun(numberOfThreads);
    }
    /*-------------------------------------------------------------------------*/

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
    /*-------------------------*/


    public Future<Void> submit(Runnable runnable, Priority p){
        return submit(runnable, p, null);
    }

    /*-------------------------*/


    public <V>Future<V> submit(Runnable runnable, Priority priority, V value){
        return submit(Executors.callable(runnable, value), priority);
    }
    /*-------------------------*/

    public <V>Future<V> submit(Callable<V> callable){
        return submit(callable, Priority.MEDIUM);
    }

    /*-------------------------*/

    public <V>Future<V> submit(Callable<V> callable, Priority priority){

        throwExemptionIfPoolShutdown();
        TaskWrapper<V> task = new TaskWrapper<>(callable, priority.getValue());
        Future<V> future = task.getFuture();
        taskPQ.enqueue(task);

        return future;
    }

    /*-------------------------*/

    public void setNumOfThreads(int numThreads){
        throwExemptionIfPoolShutdown();

        if(numThreads < threadsTable.size()){
            enqueueTerminationTasks(this.numberOfThreads - numThreads, Priority.HIGH.getValue() + 1);
        }
        else { /* same numbers the for loop in ThreadsCreate wouldn't happen */
            ThreadsCreateAndStartRun(numThreads - this.numberOfThreads);
        }
    }
    /*-------------------------*/

    public void pause(){
        throwExemptionIfPoolShutdown();
        synchronized (synchronizeForPause) {
            isPaused = true;
        }
        enqueuePauseTasks();
    }
    /*-------------------------*/

    public void resume() {
        throwExemptionIfPoolShutdown();
        synchronized (synchronizeForPause) {
            isPaused = false;
            synchronizeForPause.notifyAll();
        }
    }

    /*-------------------------*/

    public void shutdown(){

        resume();
        hasShutdown = true;
        enqueueTerminationTasks(numberOfThreads, Priority.LOW.getValue() - 1);
    }

    public void awaitTermination() {

        synchronized (synchronizeWithUsrThread){
            while (!threadsTable.isEmpty()){
                try {
                    synchronizeWithUsrThread.wait();
                }catch (InterruptedException e) {
                 throw new RuntimeException();
                }
            }
            synchronizeWithUsrThread.notifyAll();
        }
    }

    /*********************************************************************************/
    /*--ADDITIONAL PRIVATE FUNCTIONS-------------------------------------------------*/

    private void ThreadsCreateAndStartRun(int amount){

        for (int i = 0; i < amount; ++i ) {
            WorkerThread th = new WorkerThread();
            threadsTable.put(th.getId(), th);
            th.start();
        }

        /* would be done by main/user thread, avoiding unexpected behavior*/
        numberOfThreads += amount;
    }
    /*------------*/

    private void enqueuePauseTasks(){
        enqueueTasks((Callable<Void>) () -> {
            /*find himself in threadsTable and  break running loop in current thread*/
            synchronized (synchronizeForPause){
                while(isPaused){
                    synchronizeForPause.wait();
                }
            };
            return null;} , this.numberOfThreads, Priority.HIGH.getValue() + 1);
    }

    private void enqueueTerminationTasks(int amountToTerminate, int priority){

        enqueueTasks((Callable<Void>) () -> {
            /*find himself in threadsTable and  break running loop in current thread*/
            threadsTable.get(Thread.currentThread().getId()).isExecutable = false;
            return null;} , amountToTerminate, priority);

        /* would be done by main/user thread, avoiding unexpected behavior*/
        numberOfThreads -= amountToTerminate;
    }

    private <V> void enqueueTasks(Callable<V> callable ,int amountToTerminate, int priority){

        for (int i = 0; i < amountToTerminate; ++i) {
            taskPQ.enqueue(new TaskWrapper<>(callable, priority));
        }
    }

    private void throwExemptionIfPoolShutdown(){
        if(hasShutdown){
            throw new RejectedExecutionException("");
        }
    }

    /*********************************************************************************/
    /*--PRIVATE CLASSES----------------------------------------------------------------*/

    /*.....................TASK CLASS.......................*/

    private class TaskWrapper<V>{

        private Callable<V> callable;
        private final int priority;
        Thread executingThread = null;


        private final TaskFuture future = new TaskFuture();
        private final Object synchronizeExecute = new Object();


        private TaskWrapper(Callable<V> callable, int priority){
            this.callable = callable;
            this.priority = priority;
        }

        private TaskWrapper<V> outer() {
            return this;
        }

        private Future<V> getFuture(){
            return future;
        }

        /*----TASK FUTURE CLAS-------*/

        private class TaskFuture implements Future<V>{

            private V value = null;
            private final Semaphore semaphoreGettingValue = new Semaphore(0);
            private boolean isCancelled = false;
            private boolean isDone = false;

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (isCancelled || isDone) {
                    return false;
                }
                isCancelled = taskPQ.remove(outer());
                isDone = true;
                return isCancelled;
            }

            @Override
            public boolean isCancelled() {
                return isCancelled;
            }

            @Override
            public boolean isDone() {

                return isDone || isCancelled;
            }

            @Override
            public V get() throws InterruptedException, ExecutionException {

                if(isCancelled){
                    return null;
                }
                semaphoreGettingValue.acquire();
                semaphoreGettingValue.release(); /* release for next get */

                return value;
            }

            @Override
            public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                if(isCancelled){
                    return null;
                }

               if(!semaphoreGettingValue.tryAcquire(l, timeUnit)){
                   throw new TimeoutException("");
               }
                semaphoreGettingValue.release(); /* release for next get */
                return value;
            }
        }
    }

    /*................WORKER THREAD CLASS...................*/

    private class WorkerThread extends Thread{

        private boolean isExecutable = true;
        private TaskWrapper curTask;

        WorkerThread(){
        }

        @Override
        public void run() {

            while (isExecutable) {

                curTask = taskPQ.dequeue();
                try {
                    curTask.future.value = curTask.callable.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    curTask.future.isDone = true;
                    curTask.future.semaphoreGettingValue.release();
            }

            /*remove current thread from table*/
            synchronized (synchronizeWithUsrThread) {
                threadsTable.remove(Thread.currentThread().getId());
                synchronizeWithUsrThread.notifyAll();
            }
        }
    }

    /*-----------------------------------------------------------------------------*/
}



