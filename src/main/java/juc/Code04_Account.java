package juc;

/**
 * 模拟银行账问题，对业务的写方法进行加锁，堆业务的读方法不加锁
 * @Author 张三金
 * @Date 2022/1/16 0016 15:01
 * @Company jzb
 * @Version 1.0.0
 */
public class Code04_Account {

    /**
     * 账号名称
     */
    String name;

    /**
     * 余额
     */
    int balance;

    /**
     * 往账户中增加,
     * @param balance
     */
    public /*synchronized*/ void write (int balance) {
        this.balance += balance;
    }

    /**
     * 往账户中增加数量1,
     * @param balance
     */
    public synchronized void addOne () {
        this.balance ++;
    }

    /**
     * 读取账户的余额
     */
    public int read () {
        return this.balance;
    }



    public static void main(String[] args) {
        Code04_Account account = new Code04_Account();

        // 测试如果写数据不加锁时，只有当我开启10万个线程的时候，才看到了区别
        Thread[] addOneThreads = new Thread[100000];
        // 写线程
        for (int i = 0; i < 100000; i++) {
            addOneThreads[i] = new Thread(() -> {
                account.addOne();
            });
        }
        for (int i = 0; i < 100000; i++) {
            addOneThreads[i].start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(account.balance);
        // 读线程
    }


}
