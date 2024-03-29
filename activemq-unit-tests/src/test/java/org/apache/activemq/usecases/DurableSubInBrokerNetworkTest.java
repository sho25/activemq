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
name|Destination
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
name|TopicSubscriber
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
name|network
operator|.
name|NetworkTestSupport
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

begin_comment
comment|/**  * Tests durable topic subscriptions inside a network of brokers.  *   * @author tmielke  *  */
end_comment

begin_class
specifier|public
class|class
name|DurableSubInBrokerNetworkTest
extends|extends
name|NetworkTestSupport
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
name|DurableSubInBrokerNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// protected BrokerService localBroker;
specifier|private
specifier|final
name|String
name|subName
init|=
literal|"Subscriber1"
decl_stmt|;
specifier|private
specifier|final
name|String
name|subName2
init|=
literal|"Subscriber2"
decl_stmt|;
specifier|private
specifier|final
name|String
name|topicName
init|=
literal|"TEST.FOO"
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|useJmx
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|URI
name|ncUri
init|=
operator|new
name|URI
argument_list|(
literal|"static:("
operator|+
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|NetworkConnector
name|nc
init|=
operator|new
name|DiscoveryNetworkConnector
argument_list|(
name|ncUri
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
name|nc
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
if|if
condition|(
name|remoteBroker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|.
name|isStarted
argument_list|()
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a durable topic subscription, checks that it is propagated      * in the broker network, removes the subscription and checks that      * the subscription is removed from remote broker as well.      *        * @throws Exception      */
specifier|public
name|void
name|testDurableSubNetwork
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testDurableSubNetwork started."
argument_list|)
expr_stmt|;
comment|// create durable sub
name|ActiveMQConnectionFactory
name|fact
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|fact
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setClientID
argument_list|(
literal|"clientID1"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|subName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Durable subscription of name "
operator|+
name|subName
operator|+
literal|"created."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// query durable sub on local and remote broker
comment|// raise an error if not found
name|assertTrue
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundSubInRemoteBrokerByTopicName
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
comment|// unsubscribe from durable sub
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
name|subName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsubscribed from durable subscription."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// query durable sub on local and remote broker
comment|// raise an error if its not removed from both brokers
name|assertFalse
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Durable subscription not unregistered on remote broker"
argument_list|,
name|foundSubInRemoteBrokerByTopicName
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTwoDurableSubsInNetworkWithUnsubscribe
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create 1st durable sub to topic TEST.FOO
name|ActiveMQConnectionFactory
name|fact
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|fact
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setClientID
argument_list|(
literal|"clientID1"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|subName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Durable subscription of name "
operator|+
name|subName
operator|+
literal|"created."
argument_list|)
expr_stmt|;
name|TopicSubscriber
name|sub2
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|subName2
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Durable subscription of name "
operator|+
name|subName2
operator|+
literal|"created."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// query durable sub on local and remote broker
comment|// raise an error if not found
name|assertTrue
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundSubInRemoteBrokerByTopicName
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
comment|// unsubscribe from durable sub
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
name|subName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsubscribed from durable subscription."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// query durable sub on local and remote broker
name|assertFalse
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Durable subscription should still be on remote broker"
argument_list|,
name|foundSubInRemoteBrokerByTopicName
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
name|sub2
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
name|subName2
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|foundSubInLocalBroker
argument_list|(
name|subName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Durable subscription not unregistered on remote broker"
argument_list|,
name|foundSubInRemoteBrokerByTopicName
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|foundSubInRemoteBrokerByTopicName
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|foundSub
init|=
literal|false
decl_stmt|;
name|ObjectName
index|[]
name|subs
init|=
name|remoteBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
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
if|if
condition|(
name|subs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"destinationName="
operator|+
name|topicName
argument_list|)
condition|)
name|foundSub
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|foundSub
return|;
block|}
specifier|private
name|boolean
name|foundSubInLocalBroker
parameter_list|(
name|String
name|subName
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|foundSub
init|=
literal|false
decl_stmt|;
name|ObjectName
index|[]
name|subs
init|=
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getDurableTopicSubscribers
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
if|if
condition|(
name|subs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|subName
argument_list|)
condition|)
name|foundSub
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|foundSub
return|;
block|}
block|}
end_class

end_unit

