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
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|JMSException
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
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|PooledConnectionSessionCleanupTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|PooledConnectionSessionCleanupTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|service
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|directConnFact
decl_stmt|;
specifier|protected
name|Connection
name|directConn1
decl_stmt|;
specifier|protected
name|Connection
name|directConn2
decl_stmt|;
specifier|protected
name|PooledConnectionFactory
name|pooledConnFact
decl_stmt|;
specifier|protected
name|Connection
name|pooledConn1
decl_stmt|;
specifier|protected
name|Connection
name|pooledConn2
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ContendedQueue"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|50
decl_stmt|;
comment|/**      * Prepare to run a test case: create, configure, and start the embedded      * broker, as well as creating the client connections to the broker.      */
annotation|@
name|Before
specifier|public
name|void
name|prepTest
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|service
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|service
operator|.
name|setBrokerName
argument_list|(
literal|"PooledConnectionSessionCleanupTestBroker"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
comment|// Create the ActiveMQConnectionFactory and the PooledConnectionFactory.
comment|// Set a long idle timeout on the pooled connections to better show the
comment|// problem of holding onto created resources on close.
name|directConnFact
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|service
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
expr_stmt|;
name|pooledConnFact
operator|=
operator|new
name|PooledConnectionFactory
argument_list|()
expr_stmt|;
name|pooledConnFact
operator|.
name|setConnectionFactory
argument_list|(
name|directConnFact
argument_list|)
expr_stmt|;
name|pooledConnFact
operator|.
name|setIdleTimeout
argument_list|(
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|pooledConnFact
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Prepare the connections
name|directConn1
operator|=
name|directConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|directConn1
operator|.
name|start
argument_list|()
expr_stmt|;
name|directConn2
operator|=
name|directConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|directConn2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// The pooled Connections should have the same underlying connection
name|pooledConn1
operator|=
name|pooledConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|pooledConn1
operator|.
name|start
argument_list|()
expr_stmt|;
name|pooledConn2
operator|=
name|pooledConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|pooledConn2
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanupTest
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|pooledConn1
operator|!=
literal|null
condition|)
block|{
name|pooledConn1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
if|if
condition|(
name|pooledConn2
operator|!=
literal|null
condition|)
block|{
name|pooledConn2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
if|if
condition|(
name|directConn1
operator|!=
literal|null
condition|)
block|{
name|directConn1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
if|if
condition|(
name|directConn2
operator|!=
literal|null
condition|)
block|{
name|directConn2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|service
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
block|}
specifier|private
name|void
name|produceMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|directConn1
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
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
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
operator|++
name|i
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Test Message: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|QueueViewMBean
name|getProxyToQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq"
operator|+
literal|":destinationType=Queue,destinationName="
operator|+
name|name
operator|+
literal|",type=Broker,brokerName="
operator|+
name|service
operator|.
name|getBrokerName
argument_list|()
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|service
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLingeringPooledSessionsHoldingPrefetchedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|produceMessages
argument_list|()
expr_stmt|;
name|Session
name|pooledSession1
init|=
name|pooledConn1
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
name|pooledSession1
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
specifier|final
name|QueueViewMBean
name|view
init|=
name|getProxyToQueue
argument_list|(
name|queue
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have all sent messages in flight:"
argument_list|,
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
name|view
operator|.
name|getInFlightCount
argument_list|()
operator|==
name|MESSAGE_COUNT
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// While all the message are in flight we should get anything on this consumer.
name|Session
name|session
init|=
name|directConn1
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
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|pooledConn1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have only one consumer now:"
argument_list|,
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
name|view
operator|.
name|getSubscriptions
argument_list|()
operator|.
name|length
operator|==
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now we'd expect that the message stuck in the prefetch of the pooled session's
comment|// consumer would be rerouted to the non-pooled session's consumer.
name|assertNotNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonPooledConnectionCloseNotHoldingPrefetchedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|produceMessages
argument_list|()
expr_stmt|;
name|Session
name|directSession
init|=
name|directConn2
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
name|directSession
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
expr_stmt|;
specifier|final
name|QueueViewMBean
name|view
init|=
name|getProxyToQueue
argument_list|(
name|queue
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have all sent messages in flight:"
argument_list|,
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
name|view
operator|.
name|getInFlightCount
argument_list|()
operator|==
name|MESSAGE_COUNT
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// While all the message are in flight we should get anything on this consumer.
name|Session
name|session
init|=
name|directConn1
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
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|directConn2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have only one consumer now:"
argument_list|,
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
name|view
operator|.
name|getSubscriptions
argument_list|()
operator|.
name|length
operator|==
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now we'd expect that the message stuck in the prefetch of the first session's
comment|// consumer would be rerouted to the alternate session's consumer.
name|assertNotNull
argument_list|(
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
