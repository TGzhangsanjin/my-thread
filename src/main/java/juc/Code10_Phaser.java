package juc;

import java.util.concurrent.Phaser;

/**
 * 有点儿像 CyclicBarrier的升级版，，CyclicBarrier 每凑齐多少个线程就一起执行
 * Phaser 的话，可以自定义让这些线程满足不同的条件之后再一起执行,
 * 相当于多个线程，大家一起处理某个逻辑（汇总结果），再一起处理另外一个逻辑（汇总结果）
 *
 * @Author 张三金
 * @Date 2022/1/18 0018 20:27
 * @Company jzb
 * @Version 1.0.0
 */
public class Code10_Phaser {



    public static class Person implements Runnable{

        String name;

        Phaser phaser;


        public Person (String name, Phaser phaser) {
            this.name = name;
            this.phaser = phaser;
        }

        public void arrive () {
            System.out.println(this.name + " 到了！");
            // 来了你就先等着，等着大家都到了再就座
            phaser.arriveAndAwaitAdvance();
            System.out.println(this.name + " 就座！");
        }

        public void eat () {
            System.out.println(Thread.currentThread().getName() + " 吃完了！");
            // 吃完了你就先等着，等着大家都到了做你自己的事儿
            phaser.arriveAndAwaitAdvance();
        }

        public void leave () {
            System.out.println(this.name + " 准备离开了！");
            phaser.arriveAndAwaitAdvance();
        }

        public void hug () {
            if ("新娘".equals(this.name) || "新郎".equals(this.name)) {
                phaser.arriveAndAwaitAdvance();
            } else {
                // 其他人都撤吧
                phaser.arriveAndDeregister();
            }
        }

        @Override
        public void run() {
            arrive();

            eat();

            leave();

            hug();
        }
    }

    public static class WeddingPhaser extends Phaser{
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase){
                case 0:
                    System.out.println("所有人到齐了！" + registeredParties);
                    System.out.println();
                    return false;
                case 1:
                    System.out.println("所有人吃完饭！" + registeredParties);
                    System.out.println();
                    return false;
                case 2:
                    System.out.println("所有人离开了！" + registeredParties);
                    System.out.println();
                    return false;
                case 3:
                    System.out.println("婚礼结束！" + registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }

    public static void main(String[] args) {
        WeddingPhaser phaser = new WeddingPhaser();
        phaser.bulkRegister(8);
        for (int i = 0; i < 6; i++) {
            new Thread(new Person("person-" + i, phaser)).start();
        }

        new Thread(new Person("新娘", phaser)).start();
        new Thread(new Person("新郎", phaser)).start();

    }
}
