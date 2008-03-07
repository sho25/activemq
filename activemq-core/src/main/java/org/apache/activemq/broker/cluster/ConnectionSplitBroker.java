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
name|cluster
package|;
end_package

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
name|ConcurrentHashMap
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
name|MessageListener
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
name|advisory
operator|.
name|AdvisoryBroker
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
name|Broker
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
name|BrokerFilter
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
name|TransportConnection
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
name|command
operator|.
name|ActiveMQMessage
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
name|BrokerId
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
name|ConnectionId
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
name|ConnectionInfo
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
name|DataStructure
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
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * Monitors for client connections that may fail to another  * broker - but this broker isn't aware they've gone.  * Can occur with network glitches or client error  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionSplitBroker
extends|extends
name|BrokerFilter
implements|implements
name|MessageListener
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConnectionSplitBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ConnectionId
argument_list|,
name|ConnectionContext
argument_list|>
name|clientMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ConnectionId
argument_list|,
name|ConnectionContext
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ConnectionSplitBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|clientMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|clientMap
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerService
argument_list|()
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
decl_stmt|;
name|fac
operator|.
name|setCloseTimeout
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setWarnAboutUnstartedConnectionTimeout
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setAlwaysSessionAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setClientID
argument_list|(
name|getBrokerId
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|getBrokerName
argument_list|()
operator|+
literal|":ConnectionSplitBroker"
argument_list|)
expr_stmt|;
name|connection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|AdvisorySupport
operator|.
name|getConnectionAdvisoryTopic
argument_list|()
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|stop
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
block|{
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
name|m
parameter_list|)
block|{
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|m
decl_stmt|;
name|DataStructure
name|o
init|=
name|message
operator|.
name|getDataStructure
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|ConnectionInfo
operator|.
name|class
condition|)
block|{
name|ConnectionInfo
name|info
init|=
operator|(
name|ConnectionInfo
operator|)
name|o
decl_stmt|;
name|String
name|brokerId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|brokerId
operator|=
name|message
operator|.
name|getStringProperty
argument_list|(
name|AdvisorySupport
operator|.
name|MSG_PROPERTY_ORIGIN_BROKER_ID
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerId
operator|!=
literal|null
operator|&&
operator|!
name|brokerId
operator|.
name|equals
argument_list|(
name|getBrokerId
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
comment|//see if it already exits
name|ConnectionContext
name|old
init|=
name|clientMap
operator|.
name|remove
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|.
name|getConnection
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|str
init|=
literal|"connectionId="
operator|+
name|old
operator|.
name|getConnectionId
argument_list|()
operator|+
literal|",clientId="
operator|+
name|old
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Removing stale connection: "
operator|+
name|str
argument_list|)
expr_stmt|;
try|try
block|{
comment|//remove connection states
name|TransportConnection
name|connection
init|=
operator|(
name|TransportConnection
operator|)
name|old
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|processRemoveConnection
argument_list|(
name|old
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|stopAsync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to remove stale connection: "
operator|+
name|str
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to get message property "
operator|+
name|AdvisorySupport
operator|.
name|MSG_PROPERTY_ORIGIN_BROKER_ID
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|boolean
name|contains
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|,
name|BrokerId
name|brokerId
parameter_list|)
block|{
if|if
condition|(
name|brokerPath
operator|!=
literal|null
condition|)
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
name|brokerPath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|brokerId
operator|.
name|equals
argument_list|(
name|brokerPath
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

