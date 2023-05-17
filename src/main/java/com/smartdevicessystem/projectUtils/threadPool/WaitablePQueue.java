package iotInfrustructure.gateWay.threadPool;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class WaitablePQueue<T> {

    private final PriorityQueue<T> queue;
    SynchronizedUtil syncUtil;

    public WaitablePQueue(Comparator<? super T> comparator) {
        this.queue = new PriorityQueue<>(comparator);
        syncUtil = new SynchronizedUtil();
    }

    public WaitablePQueue() {
        this.queue = new PriorityQueue<>();
        syncUtil = new SynchronizedUtil();
    }

    public void enqueue(T element){
    syncUtil.startProduce();
    queue.add(element);
    syncUtil.endProduce();
    }

    public T dequeue(){
        syncUtil.startConsume();
        T ret = queue.poll();
        syncUtil.endConsume();

        return ret;
    }

    /*timeout in milliseconds*/
    public T dequeue(int timeout) throws TimeoutException{
        syncUtil.startConsume(timeout);
        T ret = queue.poll();
        syncUtil.endConsume();

        return ret;
    }

    public boolean remove(T element) {

        boolean isRemoved = false;
        if(syncUtil.countElements.tryAcquire()){
            syncUtil.lock.lock();
            isRemoved = queue.remove(element);
            if(!isRemoved){
                syncUtil.increaseCounter();
            }
            syncUtil.lock.unlock();
        }

        return isRemoved;
    }

/*********************************************************************/
    private class SynchronizedUtil {
        private final Lock lock = new ReentrantLock();
        private final Semaphore countElements = new Semaphore(0);

        public void startProduce(){
            lock.lock();
        }

        public void endProduce(){
            lock.unlock();
            countElements.release();
        }

        public void startConsume(){
            try {
                countElements.acquire();
                lock.lock();
            }catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        public void startConsume(int timeout) throws TimeoutException{
            try {
                long startTime = System.currentTimeMillis();
                if(countElements.tryAcquire( timeout, TimeUnit.MILLISECONDS)){
                    long timeLeft = timeout - (System.currentTimeMillis() - startTime);
                    if(!lock.tryLock(timeLeft, TimeUnit.MILLISECONDS)) {
                        throw new TimeoutException("");
                    }
                }
                else {
                    throw new TimeoutException("");
                }

            }catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        public void endConsume(){
            lock.unlock();
        }

        public void increaseCounter(){
            countElements.release();
        }
    }
    /*********************************************************************/

}