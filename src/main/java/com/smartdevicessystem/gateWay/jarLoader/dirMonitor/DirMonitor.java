package iotInfrustructure.gateWay.jarLoader.dirMonitor;

import java.io.IOException;
import java.nio.file.*;
import java.util.Observable;
import java.util.Observer;
/*
DirMonitor
    - argument "path" should be string with full path without white spaces.
    - DirMonitor support multithreading,but is not recommended.
    - if you choose to use DirMonitor methods in multithreading program , you should be careful.
      especially when you deal with methods - start, stop and close.

methods documentation:
    register()/unregister()
        - that methods can be called before and after DirMonitor start()/stop().
    start()
        - start function create new thread every new calling.
        - calling start() multiple time without stop(). would lead to undefine behavior.
    stop()
        - stop to broadcast.
        - every stop() correspond to previous start().
        - note! using start() after stop() would send all unsent notifications
          to registered observers while stop state.
    close()
        - close all resource, not valid for use after calling close().
        - you should not use DirMonitor object after close() calling.
 */

public class DirMonitor {

    private final WatchService watchService;
    private final FileObserver observer = new FileObserver();
    private final Object synObjForObserver = new Object();
    private boolean isRunning = true;
    private Thread LastRunningThread;


    public DirMonitor(String path) {
        Path dirToWatch = Paths.get(path);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            dirToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        }catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    public void register(Observer observerImp){
        synchronized (synObjForObserver) {
            observer.addObserver(observerImp);
        }
    }

    public void unregister(Observer observerImp){
        synchronized (synObjForObserver){
            observer.deleteObserver(observerImp);
        }
    }

    public void start() {
        isRunning = true;
        LastRunningThread = new Thread(this::runForStart);
        LastRunningThread.start();
    }

    public void stop() {
        isRunning = false;
        LastRunningThread.interrupt();
    }

    public void close(){
        this.stop();
        try {
            watchService.close();
            observer.deleteObservers();

        }catch (IOException e) {
            throw new RuntimeException("");
        }
    }

    private void runForStart(){
        WatchKey watchKey = null;
        while(isRunning){

            try {
               if( (watchKey = watchService.take()) == null){
                   break;
               }
            }catch (InterruptedException e) {
                if(isRunning){
                    throw new RuntimeException(e);
                }else {
                    System.out.println(e);
                    break;
                }
            }catch (ClosedWatchServiceException e){
                break;
            }
              synchronized (synObjForObserver){
                    broadcastObserverMessageByWatchEvent(watchKey);
            }
            watchKey.reset();
        }
    }

    private void broadcastObserverMessageByWatchEvent(WatchKey watchKey) {
        EnumEvent crudEvent = null;
        for(WatchEvent event : watchKey.pollEvents()){
            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                crudEvent = EnumEvent.MODIFY;
            } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
                crudEvent = EnumEvent.CREATE;
            }else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                crudEvent = EnumEvent.DELETE;
            }
            observer.broadcastMessage(new ObserverMessage(event.context().toString(), crudEvent));
        }
    }

/*-------------- FileObserver CLASS --------------------------*/
    private static class FileObserver extends Observable {

        private void broadcastMessage(ObserverMessage message){
            super.setChanged();
            super.notifyObservers(message);
        }
    }
}