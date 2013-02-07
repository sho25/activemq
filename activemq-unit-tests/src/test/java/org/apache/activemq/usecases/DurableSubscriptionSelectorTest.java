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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|TopicSubscriber
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQConnection
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
name|Wait
import|;
end_import

begin_class
specifier|public
class|class
name|DurableSubscriptionSelectorTest
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
block|{
name|MBeanServer
name|mbs
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|ActiveMQTopic
name|topic
decl_stmt|;
name|ActiveMQConnection
name|consumerConnection
init|=
literal|null
decl_stmt|,
name|producerConnection
init|=
literal|null
decl_stmt|;
name|Session
name|producerSession
decl_stmt|;
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|int
name|received
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|DurableSubscriptionSelectorTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|void
name|initCombosForTestSubscription
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
name|PersistenceAdapterChoice
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSubscription
parameter_list|()
throws|throws
name|Exception
block|{
name|openConsumer
argument_list|()
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
literal|4000
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid message received."
argument_list|,
literal|0
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|closeProducer
argument_list|()
expr_stmt|;
name|closeConsumer
argument_list|()
expr_stmt|;
name|stopBroker
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|openConsumer
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
block|{
return|return
name|received
operator|>=
literal|1
return|;
block|}
block|}
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message is not received."
argument_list|,
literal|1
argument_list|,
name|received
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Message is not received."
argument_list|,
literal|2
argument_list|,
name|received
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|openConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|setClientID
argument_list|(
literal|"cliID"
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|TopicSubscriber
name|subscriber
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"subName"
argument_list|,
literal|"filter=true"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|subscriber
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
name|received
operator|++
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|closeConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|consumerConnection
operator|!=
literal|null
condition|)
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|boolean
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerConnection
operator|==
literal|null
condition|)
block|{
name|producerConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|producerConnection
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
name|topic
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
name|producerSession
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"filter"
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|closeProducer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|producerConnection
operator|!=
literal|null
condition|)
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|producerConnection
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|startBroker
parameter_list|(
name|boolean
name|deleteMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"test-broker"
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteMessages
condition|)
block|{
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|/* use maxPageSize policy in place of always pulling from the broker in maxRows chunks         if (broker.getPersistenceAdapter() instanceof JDBCPersistenceAdapter) {             ((JDBCPersistenceAdapter)broker.getPersistenceAdapter()).setMaxRows(5000);         }*/
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|defaultEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultEntry
operator|.
name|setMaxPageSize
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|stopBroker
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
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
literal|"vm://test-broker?jms.watchTopicAdvisories=false&waitForStart=5000&create=false"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
argument_list|()
expr_stmt|;
name|mbs
operator|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
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
name|stopBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

