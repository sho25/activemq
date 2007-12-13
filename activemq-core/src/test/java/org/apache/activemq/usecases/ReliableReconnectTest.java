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
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

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
name|AtomicBoolean
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|broker
operator|.
name|BrokerFactory
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
name|broker
operator|.
name|BrokerService
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
name|test
operator|.
name|TestSupport
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

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ReliableReconnectTest
extends|extends
name|TestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|100
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_BROKER_URL
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|RECEIVE_TIMEOUT
init|=
literal|10000
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
name|String
name|consumerClientId
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|AtomicBoolean
name|closeBroker
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
name|AtomicInteger
name|messagesReceived
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|int
name|firstBatch
init|=
name|MESSAGE_COUNT
operator|/
literal|10
decl_stmt|;
specifier|private
name|IdGenerator
name|idGen
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|public
name|ReliableReconnectTest
parameter_list|()
block|{     }
specifier|public
name|ReliableReconnectTest
parameter_list|(
name|String
name|n
parameter_list|)
block|{
name|super
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerClientId
operator|=
name|idGen
operator|.
name|generateId
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|topic
operator|=
literal|true
expr_stmt|;
name|destination
operator|=
name|createDestination
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
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
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
literal|"failover://"
operator|+
name|DEFAULT_BROKER_URL
decl_stmt|;
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
return|;
block|}
specifier|protected
name|void
name|startBroker
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|DEFAULT_BROKER_URL
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
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
specifier|protected
name|Connection
name|createConsumerConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|consumerConnection
init|=
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
name|consumerClientId
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|consumerConnection
return|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Connection
name|con
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|con
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"TestFred"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|spawnConsumer
parameter_list|()
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Connection
name|consumerConnection
init|=
name|createConsumerConnection
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|consumerConnection
argument_list|)
decl_stmt|;
comment|// consume some messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|firstBatch
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
comment|// log.info("GOT: " + msg);
name|messagesReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|closeBroker
init|)
block|{
name|closeBroker
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|closeBroker
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|firstBatch
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|RECEIVE_TIMEOUT
argument_list|)
decl_stmt|;
comment|// log.info("GOT: " + msg);
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|messagesReceived
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|messagesReceived
init|)
block|{
name|messagesReceived
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
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
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|()
expr_stmt|;
comment|// register an interest as a durable subscriber
name|Connection
name|consumerConnection
init|=
name|createConsumerConnection
argument_list|()
decl_stmt|;
name|createConsumer
argument_list|(
name|consumerConnection
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// send some messages ...
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|idGen
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|msg
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
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
name|msg
operator|.
name|setText
argument_list|(
literal|"msg: "
operator|+
name|i
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|spawnConsumer
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|closeBroker
init|)
block|{
if|if
condition|(
operator|!
name|closeBroker
operator|.
name|get
argument_list|()
condition|)
block|{
name|closeBroker
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
comment|// System.err.println("Stopping broker");
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|startBroker
argument_list|()
expr_stmt|;
comment|// System.err.println("Started Broker again");
synchronized|synchronized
init|(
name|messagesReceived
init|)
block|{
if|if
condition|(
name|messagesReceived
operator|.
name|get
argument_list|()
operator|<
name|MESSAGE_COUNT
condition|)
block|{
name|messagesReceived
operator|.
name|wait
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
block|}
block|}
comment|// assertTrue(messagesReceived.get() == MESSAGE_COUNT);
name|int
name|count
init|=
name|messagesReceived
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not enough messages received: "
operator|+
name|count
argument_list|,
name|count
operator|>
name|firstBatch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

