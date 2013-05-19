package com.whatsinthemicrowave.mqreceiver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

@Component
public class MessageReceiver implements MessageListener {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<Integer> processed =  Collections.synchronizedSet(new HashSet());
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
			    String textMessage = ((TextMessage) message).getText();
			    System.out.println("Message \"" + textMessage + "\" received at thread " + Thread.currentThread().getId() + ".");
			    
			    if (!processMessage(textMessage)) {
			    	throw new RuntimeException("Message \"" + textMessage + "\" not processed.");
			    }
			} catch (JMSException e) {
				//e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean processMessage(String textMessage) throws InterruptedException {
		Thread.sleep(1000 + new Random().nextInt(1000));
		Integer value = Integer.valueOf(textMessage);
		Integer previous = value - 1;

		if (!processed.contains(previous) && previous!=0) {
			System.out.println("Previous value \"" + previous + "\" not found.");
			return false;
		}
		processed.add(value);
		System.out.println("Value \"" + value + "\" added.");
		return true;
	}

}
