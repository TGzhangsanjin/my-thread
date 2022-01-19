package juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.LockSupport;

/**
 * 题目：
 *  实现一个容器，提供两个方法 add(), size()
 *  实现两个线程， 线程 1 添加10个元素到容器中，线程 2 监控容器中的个数变化，当个数到5个时，线程2给出提示并结束
 * @Author 张三金
 * @Date 2022/1/19 0019 9:26
 * @Company jzb
 * @Version 1.0.0
 */
public class Code15_Question {

    public static class MyContainer {


        List<Object> list = new ArrayList<>();
        public void add() {
            list.add(new Object());
        }

        public int size() {
            return list.size();
        }
    }

    /**
     * 方式1（不行）：使用 volatile 修饰变量，线程之间可以共享，但是会不及时
     */
    public static class Test01 {
        public static class MyContainer01 {

            volatile List<Object> list = new ArrayList<>();
            public void add() {
                for (int i = 0; i < 10; i++) {
                    list.add(new Object());
                    System.out.println("count add one, current count = " + list.size());

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public int size() {
                return list.size();
            }
        }

        public static void main(String[] args) {
            MyContainer01 container = new MyContainer01();
            Thread t1 = new Thread(() -> {
                container.add();
            });
            t1.start();

            Thread t2 = new Thread( () -> {
                while (true) {
                    if (container.size() == 5) {
                        System.out.println("Thread-2 end");
                        break;
                    }
                }
            });
            t2.start();
        }
    }

    /**
     * 方法2：线程1和线程2同时加锁，且锁是同一个的对象，线程1增加了 5个元素时，调用 wait() 方法阻塞，
     *      线程2当判断出有5个元素时，结束线程2的业务，并且唤醒线程1
     */
    public static class Test02 {
        public static void main(String[] args) {
            MyContainer container = new MyContainer();
            Thread t1 = new Thread(() -> {
                synchronized (container) {
                    for (int i = 0; i < 10; i++) {
                        if (i == 5) {
                            try {
                                // 去唤醒 t2 线程（如果t2线程阻塞的话）
                                container.notify();
                                // 线程阻塞，并且释放锁
                                container.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        container.add();
                        System.out.println("count add one, i = " + i);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


            });
            Thread t2 = new Thread( () -> {
                synchronized (container) {
                    while (true) {
                        if (container.size() == 5) {
                            System.out.println("Thread-2 end");
                            // 唤醒之前阻塞了的线程
                            container.notify();
                            break;
                        } else {
                            try {
                                // 如果t2线程先拿到锁的话，t2线程要先阻塞， 释放锁
                                container.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

            });
            t2.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t1.start();
        }
    }

    /**
     * 使用 CountDonwLatch 的 await() 和 countDown() 方法，代替 wait() 和 notify() 方法
     * wait() 和 notify() 方法是需要加锁的
     * 这里只是要完成线程之间的通信，直接加锁的话未免就太重了，所以 CountDownLatch 是一种不错的选择
     */
    public static class Test03 {

        public static void main(String[] args) {
            MyContainer container = new MyContainer();
            CountDownLatch latch = new CountDownLatch(1);
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {
                        // 数量到5了，通知 t2 线程可以继续了
                        latch.countDown();
                    }
                    container.add();
                    System.out.println("count add one, i = " + i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread t2 = new Thread( () -> {
                if (container.size() != 5) {
                    try {
                        // 数量没到5就等在这里
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Thread-2 end");
            });
            t1.start();
            t2.start();
        }
    }

    /**
     * 使用 LockSupport
     */
    public static class Test04 {
        public static void main(String[] args) {
            MyContainer container = new MyContainer();

            Thread t2 = new Thread(() -> {
                if (container.size() != 5) {
                    LockSupport.park();
                }
                System.out.println("t2 end");
            });

            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {
                        LockSupport.unpark(t2);
                        try {
                            // 这个 sleep 很关键，唤醒 t2 后，，让才能让 t2 比 t1 早1s执行
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("count add, i = " + i);
                    container.add();
                }
            });

            t1.start();
            t2.start();
        }
    }

    /**
     * 使用 LockSupport 不需要使用 sleep() , 因为 unpark() 和 park() 两个方法的顺序是无关的，
     * 保证 i < 5 时，t1 线程执行，i == 5 时t2 执行完毕之后， t1 线程再接着执行
     */
    public static class Test05 {

        static Thread t1 = null;
        static Thread t2 = null;

        public static void main(String[] args) {
            MyContainer container = new MyContainer();

            t1 = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {
                        LockSupport.unpark(t2);
                        LockSupport.park();
                    }
                    System.out.println("count add, i = " + i);
                    container.add();
                }
            });

            t2 = new Thread(() -> {
                if (container.size() != 5) {
                    LockSupport.park();
                }
                System.out.println("t2 end");
                LockSupport.unpark(t1);
            });

            t1.start();
            t2.start();
        }
    }

    /**
     * Semaphore
     */
    public static class Test06 {
        static Thread t1 = null;
        static Thread t2 = null;
        public static void main(String[] args) {
            Semaphore s= new Semaphore(1);
            MyContainer container = new MyContainer();
            t1 = new Thread(() -> {
                System.out.println("t1 start");
                try {
                    s.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {
                       s.release();
                        try {
                            // 让 t2 先执行完, 并且在这里才能开启 start(), 在主线程里面，不能让 t2线程先运行
                            t2.start();
                            t2.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("count add, i = " + i);
                    container.add();
                }
                System.out.println("t1 end");
            });


            t2 = new Thread(() -> {
                System.out.println("t2 start");
                try {
                    s.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    s.release();
                }
                System.out.println("t2 end");
            });
            t1.start();

        }
    }
}
