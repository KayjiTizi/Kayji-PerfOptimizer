package com.myplugin.perfoptimizer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PerfOptimizer extends JavaPlugin {

    // ExecutorService để chạy tác vụ tính toán nặng trên tất cả các lõi CPU
    private ExecutorService executor;
    // Task định kỳ để submit các tác vụ tính toán nặng
    private BukkitRunnable boostTask;

    @Override
    public void onEnable() {
        getLogger().info("PerfOptimizer đã được bật và chế độ boost luôn chạy!");
        startBoost();
    }

    @Override
    public void onDisable() {
        stopBoost();
        getLogger().info("PerfOptimizer đã được tắt!");
    }

    /**
     * Tác vụ tính toán nặng nhằm sử dụng CPU:
     * Tính các số nguyên tố trong khoảng thời gian 100ms.
     */
    private void heavyComputation() {
        long startTime = System.nanoTime();
        long duration = TimeUnit.MILLISECONDS.toNanos(100); // chạy trong 100ms mỗi tác vụ
        long count = 0;
        for (long i = 2; ; i++) {
            if (isPrime(i)) {
                count++;
            }
            if (System.nanoTime() - startTime > duration) break;
        }
    }

    /**
     * Kiểm tra số nguyên tố (phương pháp đơn giản).
     */
    private boolean isPrime(long number) {
        if (number < 2) return false;
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /**
     * Bắt đầu chế độ boost: tạo ExecutorService với số luồng = số lõi CPU,
     * sau đó, mỗi 20 ticks (≈1 giây) submit tác vụ heavyComputation lên executor.
     */
    private void startBoost() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores);
        boostTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < cores; i++) {
                    executor.submit(() -> heavyComputation());
                }
            }
        };
        boostTask.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    /**
     * Dừng boost: chỉ được gọi khi plugin bị tắt.
     */
    private void stopBoost() {
        if (boostTask != null) {
            boostTask.cancel();
            boostTask = null;
        }
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor = null;
        }
    }
}
