begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
package|;
end_package

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
name|broker
operator|.
name|*
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
name|ActiveMQDestination
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
name|ProducerId
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
name|transport
operator|.
name|vm
operator|.
name|VMTransportFactory
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
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

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
name|Map
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
name|CountDownLatch
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|AMQ4222Test
extends|extends
name|TestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ4222Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Override
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
name|topic
operator|=
literal|false
expr_stmt|;
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
specifier|public
name|void
name|testTempQueueCleanedUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Destination
name|requestQueue
init|=
name|createDestination
argument_list|()
decl_stmt|;
name|Connection
name|producerConnection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
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
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|requestQueue
argument_list|)
decl_stmt|;
name|Destination
name|replyTo
init|=
name|producerSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|producerSessionConsumer
init|=
name|producerSession
operator|.
name|createConsumer
argument_list|(
name|replyTo
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|countDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// let's listen to the response on the queue
name|producerSessionConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"You got a message: "
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|countDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
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
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createRequest
argument_list|(
name|producerSession
argument_list|,
name|replyTo
argument_list|)
argument_list|)
expr_stmt|;
name|Connection
name|consumerConnection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
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
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|requestQueue
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|consumerProducer
init|=
name|consumerSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|consumerProducer
operator|.
name|send
argument_list|(
name|message
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error sending a response on the temp queue"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|countDownLatch
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// producer has not gone away yet...
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Destination
name|tempDestination
init|=
name|getDestination
argument_list|(
name|brokerService
argument_list|,
operator|(
name|ActiveMQDestination
operator|)
name|replyTo
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tempDestination
argument_list|)
expr_stmt|;
comment|// clean up
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// producer has gone away.. so the temp queue should not exist anymore... let's see..
comment|// producer has not gone away yet...
name|tempDestination
operator|=
name|getDestination
argument_list|(
name|brokerService
argument_list|,
operator|(
name|ActiveMQDestination
operator|)
name|replyTo
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|tempDestination
argument_list|)
expr_stmt|;
comment|// now.. the connection on the broker side for the dude producing to the temp dest will
comment|// still have a reference in his producerBrokerExchange.. this will keep the destination
comment|// from being reclaimed by GC if there is never another send that producer makes...
comment|// let's see if that reference is there...
specifier|final
name|TransportConnector
name|connector
init|=
name|VMTransportFactory
operator|.
name|CONNECTORS
operator|.
name|get
argument_list|(
literal|"localhost"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|connector
operator|.
name|getConnections
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|TransportConnection
name|transportConnection
init|=
name|connector
operator|.
name|getConnections
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|ProducerBrokerExchange
argument_list|>
name|exchanges
init|=
name|getProducerExchangeFromConn
argument_list|(
name|transportConnection
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exchanges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ProducerBrokerExchange
name|exchange
init|=
name|exchanges
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// so this is the reason for the test... we don't want these exchanges to hold a reference
comment|// to a region destination.. after a send is completed, the destination is not used anymore on
comment|// a producer exchange
name|assertNull
argument_list|(
name|exchange
operator|.
name|getRegionDestination
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|exchange
operator|.
name|getRegion
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|ProducerBrokerExchange
argument_list|>
name|getProducerExchangeFromConn
parameter_list|(
name|TransportConnection
name|transportConnection
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|f
init|=
name|TransportConnection
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"producerExchanges"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|ProducerBrokerExchange
argument_list|>
name|producerExchanges
init|=
operator|(
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|ProducerBrokerExchange
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
name|transportConnection
argument_list|)
decl_stmt|;
return|return
name|producerExchanges
return|;
block|}
specifier|private
name|Message
name|createRequest
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|replyTo
parameter_list|)
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Payload"
argument_list|)
decl_stmt|;
name|message
operator|.
name|setJMSReplyTo
argument_list|(
name|replyTo
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

