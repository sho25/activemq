begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|broker
operator|.
name|BrokerRegistry
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
name|BrokerTestSupport
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
name|StubConnection
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|QueueRegion
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
name|memory
operator|.
name|UsageManager
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|memory
operator|.
name|MemoryPersistenceAdapter
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
name|Transport
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
name|TransportFactory
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
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
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
name|Resource
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleNetworkTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|Connection
name|localConnection
decl_stmt|;
specifier|protected
name|Connection
name|remoteConnection
decl_stmt|;
specifier|protected
name|BrokerService
name|localBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|remoteBroker
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
name|Resource
name|resource
init|=
operator|new
name|ClassPathResource
argument_list|(
literal|"org/apache/activemq/network/localBroker.xml"
argument_list|)
decl_stmt|;
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|localBroker
operator|=
name|factory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
literal|"org/apache/activemq/network/remoteBroker.xml"
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|remoteBroker
operator|=
name|factory
operator|.
name|getBroker
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|localURI
init|=
name|localBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|localURI
argument_list|)
decl_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|remoteURI
init|=
name|remoteBroker
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
name|fac
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
name|remoteConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testFiltering
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|included
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"include.test.bar"
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|excluded
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"exclude.test.bar"
argument_list|)
decl_stmt|;
name|Session
name|localSession
init|=
name|localConnection
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
name|Session
name|remoteSession
init|=
name|remoteConnection
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
name|includedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|excludedConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
name|MessageProducer
name|includedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|excludedProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|excluded
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|includedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|excludedProducer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|excludedConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|includedConsumer
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConduitBridge
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|included
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"include.test.bar"
argument_list|)
decl_stmt|;
name|Session
name|localSession
init|=
name|localConnection
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
name|Session
name|remoteSession
init|=
name|remoteConnection
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
name|consumer1
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer2
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|10
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|test
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//ensure no more messages received
name|assertNull
argument_list|(
name|consumer1
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer2
operator|.
name|receive
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

