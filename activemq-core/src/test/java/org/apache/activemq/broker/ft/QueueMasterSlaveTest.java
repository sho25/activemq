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
name|broker
operator|.
name|ft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|AtomicReference
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
name|TextMessage
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
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|advisory
operator|.
name|AdvisorySupport
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_comment
comment|/**  * Test failover for Queues  */
end_comment

begin_class
specifier|public
class|class
name|QueueMasterSlaveTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueueMasterSlaveTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|master
decl_stmt|;
specifier|protected
name|AtomicReference
argument_list|<
name|BrokerService
argument_list|>
name|slave
init|=
operator|new
name|AtomicReference
argument_list|<
name|BrokerService
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|CountDownLatch
name|slaveStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|inflightMessageCount
decl_stmt|;
specifier|protected
name|int
name|failureCount
init|=
literal|50
decl_stmt|;
specifier|protected
name|String
name|uriString
init|=
literal|"failover://(tcp://localhost:62001,tcp://localhost:62002)?randomize=false&useExponentialBackOff=false"
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setMaxTestTime
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"basedir"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|messageCount
operator|=
literal|500
expr_stmt|;
name|failureCount
operator|=
name|super
operator|.
name|messageCount
operator|/
literal|2
expr_stmt|;
name|super
operator|.
name|topic
operator|=
name|isTopic
argument_list|()
expr_stmt|;
name|createMaster
argument_list|()
expr_stmt|;
name|createSlave
argument_list|()
expr_stmt|;
comment|// wait for thing to connect
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getSlaveXml
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/ft/slave.xml"
return|;
block|}
specifier|protected
name|String
name|getMasterXml
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/ft/master.xml"
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|master
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|slaveStarted
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|BrokerService
name|brokerService
init|=
name|slave
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
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
name|uriString
argument_list|)
return|;
block|}
specifier|protected
name|void
name|messageSent
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|++
name|inflightMessageCount
operator|==
name|failureCount
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"MASTER STOPPED!@!!!!"
argument_list|)
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|isTopic
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|void
name|createMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|getMasterXml
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|master
operator|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|createSlave
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|brokerFactory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|getSlaveXml
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|brokerFactory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|brokerFactory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|slave
operator|.
name|set
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|slaveStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testVirtualTopicFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|qConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.A.VirtualTopic.TA1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"No message there yet"
argument_list|,
name|qConsumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|qConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"slave started"
argument_list|,
name|slaveStarted
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|text
init|=
literal|"ForUWhenSlaveKicksIn"
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualTopic.TA1"
argument_list|)
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|qConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.A.VirtualTopic.TA1"
argument_list|)
argument_list|)
expr_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Message
name|message
init|=
name|qConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Get message after failover"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"correct message"
argument_list|,
name|text
argument_list|,
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
block|}
specifier|public
name|void
name|testAdvisory
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageConsumer
name|advConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|getMasterBrokerAdvisoryTopic
argument_list|()
argument_list|)
decl_stmt|;
name|master
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"slave started"
argument_list|,
name|slaveStarted
operator|.
name|await
argument_list|(
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"slave started"
argument_list|)
expr_stmt|;
name|Message
name|advisoryMessage
init|=
name|advConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received "
operator|+
name|advisoryMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Didn't received advisory"
argument_list|,
name|advisoryMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

