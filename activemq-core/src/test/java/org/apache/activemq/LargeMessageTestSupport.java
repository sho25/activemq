begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
import|;
end_import

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
name|DeliveryMode
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
name|MessageListener
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
name|Topic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IdGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|LargeMessageTestSupport
extends|extends
name|ClientTestSupport
implements|implements
name|MessageListener
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|LARGE_MESSAGE_SIZE
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LargeMessageTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|producerConnection
decl_stmt|;
specifier|protected
name|Connection
name|consumerConnection
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
name|Session
name|producerSession
decl_stmt|;
specifier|protected
name|Session
name|consumerSession
decl_stmt|;
specifier|protected
name|byte
index|[]
name|largeMessageData
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|isTopic
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|isDurable
init|=
literal|true
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|protected
name|IdGenerator
name|idGen
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|validMessageConsumption
init|=
literal|true
decl_stmt|;
specifier|protected
name|AtomicInteger
name|messageCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|prefetchValue
init|=
literal|10000000
decl_stmt|;
specifier|protected
name|Destination
name|createDestination
parameter_list|()
block|{
name|String
name|subject
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isTopic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|subject
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|subject
argument_list|)
return|;
block|}
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
name|isTopic
operator|&&
name|isDurable
condition|)
block|{
return|return
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
block|}
specifier|public
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
name|ClientTestSupport
operator|.
name|removeMessageStore
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up . . . . . "
argument_list|)
expr_stmt|;
name|messageCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
name|largeMessageData
operator|=
operator|new
name|byte
index|[
name|LARGE_MESSAGE_SIZE
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LARGE_MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|largeMessageData
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
else|else
block|{
name|largeMessageData
index|[
name|i
index|]
operator|=
literal|'z'
expr_stmt|;
block|}
block|}
try|try
block|{
comment|// allow the broker to start
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|ActiveMQConnectionFactory
name|fac
init|=
name|getConnectionFactory
argument_list|()
decl_stmt|;
name|producerConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|setPrefetchPolicy
argument_list|(
operator|(
name|ActiveMQConnection
operator|)
name|producerConnection
argument_list|)
expr_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|setPrefetchPolicy
argument_list|(
operator|(
name|ActiveMQConnection
operator|)
name|consumerConnection
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerSession
operator|=
name|producerConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|createDestination
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|consumerSession
operator|=
name|consumerConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup complete"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setPrefetchPolicy
parameter_list|(
name|ActiveMQConnection
name|activeMQConnection
parameter_list|)
block|{
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setTopicPrefetch
argument_list|(
name|prefetchValue
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
name|prefetchValue
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|prefetchValue
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueueBrowserPrefetch
argument_list|(
name|prefetchValue
argument_list|)
expr_stmt|;
name|activeMQConnection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setOptimizeDurableTopicPrefetch
argument_list|(
name|prefetchValue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|largeMessageData
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|boolean
name|isSame
parameter_list|(
name|BytesMessage
name|msg1
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg1
operator|)
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LARGE_MESSAGE_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|msg1
operator|.
name|readByte
argument_list|()
operator|==
name|largeMessageData
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
try|try
block|{
name|BytesMessage
name|ba
init|=
operator|(
name|BytesMessage
operator|)
name|msg
decl_stmt|;
name|validMessageConsumption
operator|&=
name|isSame
argument_list|(
name|ba
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ba
operator|.
name|getBodyLength
argument_list|()
operator|==
name|LARGE_MESSAGE_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageCount
operator|.
name|incrementAndGet
argument_list|()
operator|>=
name|MESSAGE_COUNT
condition|)
block|{
synchronized|synchronized
init|(
name|messageCount
init|)
block|{
name|messageCount
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"got message = "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageCount
operator|.
name|get
argument_list|()
operator|%
literal|50
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"count = "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testLargeMessages
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending message: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|BytesMessage
name|msg
init|=
name|producerSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|largeMessageData
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|now
operator|+
literal|60000
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|&&
name|messageCount
operator|.
name|get
argument_list|()
operator|<
name|MESSAGE_COUNT
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"message count = "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|messageCount
init|)
block|{
name|messageCount
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished count = "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not enough messages - expected "
operator|+
name|MESSAGE_COUNT
operator|+
literal|" but got "
operator|+
name|messageCount
argument_list|,
name|messageCount
operator|.
name|get
argument_list|()
operator|==
name|MESSAGE_COUNT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"received messages are not valid"
argument_list|,
name|validMessageConsumption
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"FINAL count = "
operator|+
name|messageCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

