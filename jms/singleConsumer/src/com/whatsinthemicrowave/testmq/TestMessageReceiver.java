package com.whatsinthemicrowave.testmq;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMessageReceiver {
	public static void main(String[] args)
	{
        new ClassPathXmlApplicationContext("spring/QueueReceiver.xml");
	}
}
