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
name|broker
operator|.
name|ConnectionContext
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
name|DurableSubscriptionViewMBean
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
name|SubscriptionView
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
name|SubscriptionViewMBean
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
name|Destination
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
name|DurableTopicSubscription
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
name|Subscription
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
name|command
operator|.
name|RemoveSubscriptionInfo
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
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|*
import|;
end_import

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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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

begin_class
specifier|public
class|class
name|DurableSubscriptionUnsubscribeTest
extends|extends
name|TestSupport
block|{
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|public
name|void
name|testJMXSubscriptionUnsubscribe
parameter_list|()
throws|throws
name|Exception
block|{
name|doJMXUnsubscribe
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJMXSubscriptionUnsubscribeWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doJMXUnsubscribe
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionSubscriptionUnsubscribe
parameter_list|()
throws|throws
name|Exception
block|{
name|doConnectionUnsubscribe
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionSubscriptionUnsubscribeWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doConnectionUnsubscribe
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDirectSubscriptionUnsubscribe
parameter_list|()
throws|throws
name|Exception
block|{
name|doDirectUnsubscribe
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDirectubscriptionUnsubscribeWithRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doDirectUnsubscribe
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doJMXUnsubscribe
parameter_list|(
name|boolean
name|restart
parameter_list|)
throws|throws
name|Exception
block|{
name|createSubscriptions
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|ObjectName
index|[]
name|subs
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getInactiveDurableTopicSubscribers
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectName
name|subName
init|=
name|subs
index|[
name|i
index|]
decl_stmt|;
name|DurableSubscriptionViewMBean
name|sub
init|=
operator|(
name|DurableSubscriptionViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|subName
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|sub
operator|.
name|destroy
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|20
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
operator|-
name|i
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doConnectionUnsubscribe
parameter_list|(
name|boolean
name|restart
parameter_list|)
throws|throws
name|Exception
block|{
name|createSubscriptions
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId1"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Session
name|session2
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
name|session2
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId2"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
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
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
literal|"SubsId"
operator|+
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|20
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
operator|-
name|i
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doDirectUnsubscribe
parameter_list|(
name|boolean
name|restart
parameter_list|)
throws|throws
name|Exception
block|{
name|createSubscriptions
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
literal|"SubsId"
operator|+
name|i
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
operator|.
name|getRegionBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|20
operator|==
literal|0
condition|)
block|{
name|assertCount
argument_list|(
literal|100
operator|-
name|i
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|restart
condition|)
block|{
name|restartBroker
argument_list|()
expr_stmt|;
name|assertCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createSubscriptions
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
operator|+
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertCount
parameter_list|(
name|int
name|all
parameter_list|,
name|int
name|active
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|inactive
init|=
name|all
operator|-
name|active
decl_stmt|;
comment|// broker check
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Subscription
argument_list|>
name|subs
init|=
name|destination
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
name|int
name|cActive
init|=
literal|0
decl_stmt|,
name|cInactive
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Subscription
name|sub
range|:
name|subs
control|)
block|{
if|if
condition|(
name|sub
operator|instanceof
name|DurableTopicSubscription
condition|)
block|{
name|DurableTopicSubscription
name|durable
init|=
operator|(
name|DurableTopicSubscription
operator|)
name|sub
decl_stmt|;
if|if
condition|(
name|durable
operator|.
name|isActive
argument_list|()
condition|)
name|cActive
operator|++
expr_stmt|;
else|else
name|cInactive
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|active
argument_list|,
name|cActive
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|inactive
argument_list|,
name|cInactive
argument_list|)
expr_stmt|;
comment|// admin view
name|ObjectName
index|[]
name|subscriptions
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|active
argument_list|,
name|subscriptions
operator|.
name|length
argument_list|)
expr_stmt|;
name|subscriptions
operator|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|inactive
argument_list|,
name|subscriptions
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check the strange false MBean
if|if
condition|(
name|all
operator|==
literal|0
condition|)
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|countMBean
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|countMBean
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|InstanceNotFoundException
block|{
name|int
name|count
init|=
literal|0
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"org.apache.activemq:BrokerName="
operator|+
name|getName
argument_list|()
operator|+
literal|",Type=Subscription,active=false,name="
operator|+
name|getName
argument_list|()
operator|+
literal|"_SubsId"
operator|+
name|i
decl_stmt|;
name|ObjectName
name|sub
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getObjectInstance
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstanceNotFoundException
name|ignore
parameter_list|)
block|{
comment|// this should happen
block|}
block|}
return|return
name|count
return|;
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
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://"
operator|+
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|KahaDBPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"activemq-data/"
operator|+
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
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
name|broker
operator|.
name|setKeepDurableSubsActive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|connection
operator|=
name|createConnection
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
name|connection
operator|!=
literal|null
condition|)
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
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
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
name|broker
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|void
name|restartBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
literal|false
argument_list|)
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
literal|"vm://"
operator|+
name|getName
argument_list|()
operator|+
literal|"?waitForStart=5000&create=false"
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
argument_list|()
expr_stmt|;
name|startBroker
argument_list|(
literal|true
argument_list|)
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
annotation|@
name|Override
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|rc
init|=
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|rc
operator|.
name|setClientID
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
block|}
end_class

end_unit

