package order.smzs.com.companyorder.Util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 类描述: 线程池辅助类，整个应用程序就只有一个线程池去管理线程。 可以设置核心线程数、最大线程数、额外线程空状态生存时间，阻塞队列长度来优化线程池。
 * 
 * note 从现在起 不要自己new Thread() 会降低性能 为每个程序建立一个ThreadPoolExecutor对象 需要新线程时 从那获取
 * 
 * @author ChenHaidong
 * @version 1.0
 * @Date 日期 ： 2013-1-28 时间 ： 上午11:35:56
 */
public class ThreadPoolUtils {

	private ThreadPoolUtils() {

	}

	// 线程池当前运行数
	private static int CORE_POOL_SIZE = 5;
	// 线程池最大线程数
	private static int MAX_POOL_SIZE = 1000;
	// 额外线程最长生存时间
	private static int KEEP_ALIVE_TIME = 180;

	/*
	 * BlockingQueue是一种特殊的Queue，若BlockingQueue是空的，从BlockingQueue取东西的操作将会被阻断进入等待状态直到BlocingkQueue进了新货才会被唤醒。
	 * 同样，如果BlockingQueue是满的任何试图往里存东西的操作也会被阻断进入等待状态，直到BlockingQueue里有新的空间才会被唤醒继续操作。
	 * BlockingQueue提供的方法主要有：
	 * add(anObject):把anObject加到BlockingQueue里，如果BlockingQueue可以容纳返回true，否则抛出IllegalStateException异常。
	 * offer(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue可以容纳返回true，否则返回false。
	 * put(anObject)：把anObject加到BlockingQueue里，如果BlockingQueue没有空间，调用此方法的线程被阻断直到BlockingQueue里有新的空间再继续。
	 * 
	 * poll(time)：取出BlockingQueue里排在首位的对象，若不能立即取出可等time参数规定的时间。取不到时返回null。
	 * take()：取出BlockingQueue里排在首位的对象，若BlockingQueue为空，阻断进入等待状态直到BlockingQueue有新的对象被加入为止。
	 * 
	 * 根据不同的需要BlockingQueue有4种具体实现：
	 * （1）ArrayBlockingQueue：规定大小的BlockingQueue，其构造函数必须带一个int参数来指明其大小。其所含的对象是以FIFO（先入先出）顺序排序的。
	 * （2）LinkedBlockingQueue：大小不定的BlockingQueue，若其构造函数带一个规定大小的参数，生成的BlockingQueue有大小限制，
	 * 若不带大小参数，所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定。其所含的对象是以FIFO（先入先出）顺序排序的。
	 * LinkedBlockingQueue和ArrayBlockingQueue比较起来，它们背后所用的数据结构不一样，导致LinkedBlockingQueue的数据吞吐量要大于ArrayBlockingQueue,
	 * 但在线程数量很大时其性能的可预见性低于ArrayBlockingQueue。
	 * （3）PriorityBlockingQueue：类似于LinkedBlockingQueue，但其所含对象的排序不是FIFO，而是依据对象的自然排序顺序或者是构造函数所带的Comparator决定的顺序。
	 * （4）SynchronousQueue：特殊的BlockingQueue，对其的操作必须是放和取交替完成的。
	 */
	private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);
	
	/*
	 * 线程工厂
	 */
	private static ThreadFactory threadFactory = new ThreadFactory() {
		private final AtomicInteger integer = new AtomicInteger();// AtomicInteger 是线程安全的 ，而且性能超用synchronized的方法

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r , "myThreadPool thread:" + integer.getAndIncrement());
		}

	};
	
	// 线程池
	private static ThreadPoolExecutor threadPool;

	static {
		threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				workQueue,threadFactory);
	}
	
	public static void execute(Runnable r){
		if (r != null){
			threadPool.execute(r);
		}
	} 
	
}
