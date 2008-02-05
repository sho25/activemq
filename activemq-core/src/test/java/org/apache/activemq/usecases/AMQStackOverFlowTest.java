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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|PolicyMap
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
name|network
operator|.
name|DiscoveryNetworkConnector
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
name|network
operator|.
name|NetworkConnector
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|JmsTemplate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|MessageCreator
import|;
end_import

begin_class
specifier|public
class|class
name|AMQStackOverFlowTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|URL1
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URL2
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
specifier|public
name|void
name|testStackOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService1
init|=
literal|null
decl_stmt|;
name|BrokerService
name|brokerService2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|brokerService1
operator|=
name|createBrokerService
argument_list|(
literal|"broker1"
argument_list|,
name|URL1
argument_list|,
name|URL2
argument_list|)
expr_stmt|;
name|brokerService1
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService2
operator|=
name|createBrokerService
argument_list|(
literal|"broker2"
argument_list|,
name|URL2
argument_list|,
name|URL1
argument_list|)
expr_stmt|;
name|brokerService2
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|cf1
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|URL1
argument_list|)
decl_stmt|;
name|cf1
operator|.
name|setUseAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|cf2
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|URL2
argument_list|)
decl_stmt|;
name|cf2
operator|.
name|setUseAsyncSend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|JmsTemplate
name|template1
init|=
operator|new
name|JmsTemplate
argument_list|(
name|cf1
argument_list|)
decl_stmt|;
name|template1
operator|.
name|setReceiveTimeout
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|template1
operator|.
name|send
argument_list|(
literal|"test.q"
argument_list|,
operator|new
name|MessageCreator
argument_list|()
block|{
specifier|public
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|JmsTemplate
name|template2
init|=
operator|new
name|JmsTemplate
argument_list|(
name|cf2
argument_list|)
decl_stmt|;
name|template2
operator|.
name|setReceiveTimeout
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
specifier|final
name|Message
name|m
init|=
name|template2
operator|.
name|receive
argument_list|(
literal|"test.q"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|m
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
specifier|final
name|TextMessage
name|tm
init|=
operator|(
name|TextMessage
operator|)
name|m
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|tm
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|template2
operator|.
name|send
argument_list|(
literal|"test2.q"
argument_list|,
operator|new
name|MessageCreator
argument_list|()
block|{
specifier|public
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test2"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Message
name|m2
init|=
name|template1
operator|.
name|receive
argument_list|(
literal|"test2.q"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|m2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m2
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
specifier|final
name|TextMessage
name|tm2
init|=
operator|(
name|TextMessage
operator|)
name|m2
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test2"
argument_list|,
name|tm2
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|brokerService1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService1
operator|=
literal|null
expr_stmt|;
name|brokerService2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService2
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|BrokerService
name|createBrokerService
parameter_list|(
specifier|final
name|String
name|brokerName
parameter_list|,
specifier|final
name|String
name|uri1
parameter_list|,
specifier|final
name|String
name|uri2
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
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
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|SystemUsage
name|memoryManager
init|=
operator|new
name|SystemUsage
argument_list|()
decl_stmt|;
comment|//memoryManager.getMemoryUsage().setLimit(10);
name|brokerService
operator|.
name|setSystemUsage
argument_list|(
name|memoryManager
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|policyEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
comment|//entry.setMemoryLimit(1);
name|policyEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
specifier|final
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|policyEntries
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
specifier|final
name|TransportConnector
name|tConnector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|tConnector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
name|uri1
argument_list|)
argument_list|)
expr_stmt|;
name|tConnector
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|tConnector
operator|.
name|setName
argument_list|(
name|brokerName
operator|+
literal|".transportConnector"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|tConnector
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri2
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
operator|new
name|URI
argument_list|(
literal|"static:"
operator|+
name|uri2
argument_list|)
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setBridgeTempDestinations
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nc
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
comment|//nc.setPrefetchSize(1);
name|brokerService
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
return|return
name|brokerService
return|;
block|}
block|}
end_class

end_unit

