package com.whatsinthemicrowave.mqreceiver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

@Component
public class MessageReceiver implements MessageListener {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<Integer> processed =  Collections.synchronizedSet(new HashSet());
	private Queue<MessageItem> reprocessingQueue = new ConcurrentLinkedQueue<MessageItem>();
	
	public MessageReceiver() {
		MessageReprocessor messageReprocessor = new MessageReprocessor(10000, 5);
		messageReprocessor.start();
	}
	
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			try {
			    String textMessage = ((TextMessage) message).getText();
			    System.out.println("Message \"" + textMessage + "\" received at thread " + Thread.currentThread().getId() + ".");
			    if (!processMessage(textMessage)) {
			    	MessageItem messageItem = new MessageItem(textMessage);
			    	reprocessingQueue.add(messageItem);
			    }
			} catch (JMSException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	private boolean processMessage(String textMessage) throws InterruptedException {
		Thread.sleep(1000 + new Random().nextInt(1000));
		String[] messageValues = textMessage.split(":");
		Integer value = Integer.valueOf(messageValues[0]);
		Integer referValue = 0;
		if (messageValues.length > 1) {
			referValue = Integer.valueOf(messageValues[1]);
		}

		if (!processed.contains(referValue) && referValue!=0) {
			System.out.println("Reference value \"" + referValue + "\" not found.");
			return false;
		}
		processed.add(value);
		System.out.println("Value \"" + value + "\" added.");
		return true;
	}

    private class MessageReprocessor extends Thread {
		private int delay;
		private int attemptCount;
		
		private MessageReprocessor(int delay, int attemptCount) {
			this.delay = delay;
			this.attemptCount = attemptCount;
		}
		
		public void run () {
			while (true) {
				try {
					Thread.sleep(this.delay);
					for (int i=0; i<reprocessingQueue.size(); i++) {
						MessageItem messageItem = reprocessingQueue.poll();
			    		System.out.println("Attempting to reprocess message \"" + messageItem.getTextMessage() + "\" at iteration " + (messageItem.getAttemptCount()+1));
					    if (!processMessage(messageItem.getTextMessage())) {
					    	messageItem.setAttemptCount(messageItem.getAttemptCount()+1);
					    	if (messageItem.getAttemptCount() < this.attemptCount) {
					    		reprocessingQueue.add(messageItem);
					    	} else {
					    		System.out.println("Attempt at processing message \"" + messageItem.getTextMessage() + "\" failed.");
					    	} 
					    }
						
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	
	private class MessageItem {
		private String textMessage;
		private int    attemptCount;
		
		private MessageItem(String textMessage) {
			this.textMessage = textMessage;
			this.attemptCount = 0;
		}
		
		private String getTextMessage() {
			return this.textMessage;
		}

		private void setAttemptCount(int attemptCount) {
			this.attemptCount = attemptCount;
		}
		
		private int getAttemptCount() {
			return this.attemptCount;
		}
	}
}
