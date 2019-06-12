package com.server.edu.election.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.AbstractElecQueue;

public class Test extends AbstractElecQueue
{
    static LinkedBlockingQueue<ElecRequest> queue = new LinkedBlockingQueue<>();
    
    private static ExecutorService starter = Executors.newFixedThreadPool(3);
    
    public static void main(String[] args)
    {
        for (int i = 0; i < 500; i++)
        {
            ElecRequest elecRequest = new ElecRequest();
            elecRequest.setStudentId("data" + i);
            queue.add(elecRequest);
        }
        ex();
    }
    
    static void ex()
    {
        Test t = new Test();
        // 启动
        starter.execute(() -> {
            Thread.currentThread()
                .setName("elecConsume-" + Thread.currentThread().getName());
            while (true)
            {
                try
                {
                    t.consume();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    // 异常停45秒后再试
                    try
                    {
                        TimeUnit.SECONDS.sleep(45);
                    }
                    catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    
    public void consume()
    {
        try
        {
            ElecRequest take = queue.take();
            if (null != take)
            {
                super.execute(() -> {
                    try
                    {
                        System.out.println("consume " + take.getStudentId());
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
