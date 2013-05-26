package com.whatsinthemicrowave.testmq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.whatsinthemicrowave.mqsender.MessageSender;

public class TestMessageSender {
	public static void main(String[] args)
	{
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring/QueueSender.xml");
        MessageSender messageSender = (MessageSender) ctx.getBean("messageSender");
                
        String dataFile = args[0];
        String line = null;
        BufferedReader breader = null;
        try {
			breader = new BufferedReader(new FileReader(dataFile));
			while ((line = breader.readLine()) != null) {
		    	messageSender.send(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (breader != null) {
				try {
					breader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
