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
name|assertNotSame
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
name|fail
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|javax
operator|.
name|jms
operator|.
name|TopicSession
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
name|TransportConnector
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

begin_class
specifier|public
class|class
name|PooledSessionNoPublisherCachingTest
extends|extends
name|JmsPoolTestSupport
block|{
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|PooledConnectionFactory
name|pooledFactory
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|pooledFactory
operator|=
operator|new
name|PooledConnectionFactory
argument_list|()
expr_stmt|;
name|pooledFactory
operator|.
name|setConnectionFactory
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setMaxConnections
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setBlockIfSessionPoolIsFull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pooledFactory
operator|.
name|setUseAnonymousProducers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMessageProducersAreUnique
parameter_list|()
throws|throws
name|Exception
block|{
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
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
name|Queue
name|queue1
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|Queue
name|queue2
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|PooledProducer
name|producer1
init|=
operator|(
name|PooledProducer
operator|)
name|session
operator|.
name|createProducer
argument_list|(
name|queue1
argument_list|)
decl_stmt|;
name|PooledProducer
name|producer2
init|=
operator|(
name|PooledProducer
operator|)
name|session
operator|.
name|createProducer
argument_list|(
name|queue2
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|producer1
operator|.
name|getMessageProducer
argument_list|()
argument_list|,
name|producer2
operator|.
name|getMessageProducer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testThrowsWhenDestinationGiven
parameter_list|()
throws|throws
name|Exception
block|{
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
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
name|Queue
name|queue1
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|Queue
name|queue2
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|PooledProducer
name|producer
init|=
operator|(
name|PooledProducer
operator|)
name|session
operator|.
name|createProducer
argument_list|(
name|queue1
argument_list|)
decl_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|queue2
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should only be able to send to queue 1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{         }
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
literal|null
argument_list|,
name|session
operator|.
name|createTextMessage
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should only be able to send to queue 1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testCreateTopicPublisher
parameter_list|()
throws|throws
name|Exception
block|{
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|TopicSession
name|session
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|topic1
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"Topic-1"
argument_list|)
decl_stmt|;
name|Topic
name|topic2
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"Topic-2"
argument_list|)
decl_stmt|;
name|PooledTopicPublisher
name|publisher1
init|=
operator|(
name|PooledTopicPublisher
operator|)
name|session
operator|.
name|createPublisher
argument_list|(
name|topic1
argument_list|)
decl_stmt|;
name|PooledTopicPublisher
name|publisher2
init|=
operator|(
name|PooledTopicPublisher
operator|)
name|session
operator|.
name|createPublisher
argument_list|(
name|topic2
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|publisher1
operator|.
name|getMessageProducer
argument_list|()
argument_list|,
name|publisher2
operator|.
name|getMessageProducer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testQueueSender
parameter_list|()
throws|throws
name|Exception
block|{
name|PooledConnection
name|connection
init|=
operator|(
name|PooledConnection
operator|)
name|pooledFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|QueueSession
name|session
init|=
name|connection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue1
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|Queue
name|queue2
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|PooledQueueSender
name|sender1
init|=
operator|(
name|PooledQueueSender
operator|)
name|session
operator|.
name|createSender
argument_list|(
name|queue1
argument_list|)
decl_stmt|;
name|PooledQueueSender
name|sender2
init|=
operator|(
name|PooledQueueSender
operator|)
name|session
operator|.
name|createSender
argument_list|(
name|queue2
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|sender1
operator|.
name|getMessageProducer
argument_list|()
argument_list|,
name|sender2
operator|.
name|getMessageProducer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

