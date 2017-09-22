package com.yy.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by luyuanyuan on 2017/9/22.
 */
public class ZookeeperLock implements Lock {

    private static final String ZK_IP_PORT = "139.224.119.167:2181";
    private static final String LOCK_NODE = "/LOCK";

    private ZkClient zkClient = new ZkClient(ZK_IP_PORT);

    private CountDownLatch cdl;

    //阻塞对的方式去获取锁
    public void lock() {
        if(tryLock()){
            System.out.println("获取锁成功");
        }else{
            waitForLock();
            lock();
        }

    }

    private void waitForLock() {
        //1.创建一个监听
        IZkDataListener listener = new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {

            }

            public void handleDataDeleted(String s) throws Exception {
                //3.当其他线程放锁，抛出事件，让其他线程重新竞争锁
                System.out.println("捕捉到节点删除事件");
                if (cdl != null) {
                    cdl.countDown();
                }
            }
        };
        zkClient.subscribeDataChanges(LOCK_NODE, listener);

        //2.如果节点存在，让线程阻塞
        if(zkClient.exists(LOCK_NODE)){
            cdl = new CountDownLatch(1);
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        zkClient.unsubscribeDataChanges(LOCK_NODE,listener);
    }

    //通过新建节点的方式尝试去加锁，非阻塞
    public boolean tryLock() {
        try {
            zkClient.createPersistent(LOCK_NODE);
            return true;
        } catch (ZkNodeExistsException e) {
            return false;
        }
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        zkClient.delete(LOCK_NODE);

    }


    public Condition newCondition() {
        return null;
    }

    public void lockInterruptibly() throws InterruptedException {

    }
}
