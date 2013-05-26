package com.whatsinthemicrowave.mqsender;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
	private JmsTemplate jmsTemplate;
	private Queue       jmsQueue;
	
	@Autowired
	MessageSender(final JmsTemplate jmsTemplate, final Queue jmsQueue) {
		this.jmsTemplate = jmsTemplate;
		this.jmsQueue = jmsQueue;
	}

	public void send(final String message) {
		jmsTemplate.send(this.jmsQueue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}
}
