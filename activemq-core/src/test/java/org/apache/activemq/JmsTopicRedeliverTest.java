begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicRedeliverTest
extends|extends
name|TestSupport
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsTopicRedeliverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|Session
name|consumeSession
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|Destination
name|consumerDestination
decl_stmt|;
specifier|protected
name|Destination
name|producerDestination
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|durable
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
specifier|protected
name|long
name|initRedeliveryDelay
init|=
literal|0
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|initRedeliveryDelay
operator|=
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|connection
operator|)
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getInitialRedeliveryDelay
argument_list|()
expr_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Created connection: "
operator|+
name|connection
argument_list|)
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumeSession
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created session: "
operator|+
name|session
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created consumeSession: "
operator|+
name|consumeSession
argument_list|)
expr_stmt|;
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//producer.setDeliveryMode(deliveryMode);
name|log
operator|.
name|info
argument_list|(
literal|"Created producer: "
operator|+
name|producer
argument_list|)
expr_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|consumerDestination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|getConsumerSubject
argument_list|()
argument_list|)
expr_stmt|;
name|producerDestination
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|getProducerSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumerDestination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|getConsumerSubject
argument_list|()
argument_list|)
expr_stmt|;
name|producerDestination
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|getProducerSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Created  consumer destination: "
operator|+
name|consumerDestination
operator|+
literal|" of type: "
operator|+
name|consumerDestination
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created  producer destination: "
operator|+
name|producerDestination
operator|+
literal|" of type: "
operator|+
name|producerDestination
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created connection: "
operator|+
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the consumer subject.      *      * @return String - consumer subject      * @see org.apache.activemq.test.TestSupport#getConsumerSubject()      */
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
literal|"TEST"
return|;
block|}
comment|/**      * Returns the producer subject.      *      * @return String - producer subject      * @see org.apache.activemq.test.TestSupport#getProducerSubject()      */
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
literal|"TEST"
return|;
block|}
comment|/**      * Sends and consumes the messages.      *      * @throws Exception      */
specifier|public
name|void
name|testRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"TEST"
decl_stmt|;
name|Message
name|sendMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"About to send a message: "
operator|+
name|sendMessage
operator|+
literal|" with text: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|sendMessage
argument_list|)
expr_stmt|;
comment|//receive but don't acknowledge
name|Message
name|unackMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|initRedeliveryDelay
operator|+
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|unackMessage
argument_list|)
expr_stmt|;
name|String
name|unackId
init|=
name|unackMessage
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|unackMessage
operator|)
operator|.
name|getText
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unackMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(unackMessage.getIntProperty("JMSXDeliveryCount"),1);
comment|//receive then acknowledge
name|consumeSession
operator|.
name|recover
argument_list|()
expr_stmt|;
name|Message
name|ackMessage
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|initRedeliveryDelay
operator|+
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ackMessage
argument_list|)
expr_stmt|;
name|ackMessage
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|String
name|ackId
init|=
name|ackMessage
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|ackMessage
operator|)
operator|.
name|getText
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ackMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(ackMessage.getIntProperty("JMSXDeliveryCount"),2);
name|assertEquals
argument_list|(
name|unackId
argument_list|,
name|ackId
argument_list|)
expr_stmt|;
name|consumeSession
operator|.
name|recover
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|durable
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating durable consumer"
argument_list|)
expr_stmt|;
return|return
name|consumeSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|consumerDestination
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
return|return
name|consumeSession
operator|.
name|createConsumer
argument_list|(
name|consumerDestination
argument_list|)
return|;
block|}
block|}
end_class

end_unit

