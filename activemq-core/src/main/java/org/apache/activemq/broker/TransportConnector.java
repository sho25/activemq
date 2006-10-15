begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|net
operator|.
name|URISyntaxException
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
name|management
operator|.
name|MBeanServer
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
name|broker
operator|.
name|jmx
operator|.
name|ManagedTransportConnector
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
name|ConnectorStatistics
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
name|BrokerInfo
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
name|security
operator|.
name|MessageAuthorizationPolicy
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|TransportAcceptListener
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
name|transport
operator|.
name|TransportServer
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
name|discovery
operator|.
name|DiscoveryAgent
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
name|discovery
operator|.
name|DiscoveryAgentFactory
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
name|ServiceStopper
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
name|ServiceSupport
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * @org.apache.xbean.XBean  *   * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|TransportConnector
implements|implements
name|Connector
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransportConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Broker
name|broker
decl_stmt|;
specifier|private
name|TransportServer
name|server
decl_stmt|;
specifier|private
name|URI
name|uri
decl_stmt|;
specifier|private
name|BrokerInfo
name|brokerInfo
init|=
operator|new
name|BrokerInfo
argument_list|()
decl_stmt|;
specifier|private
name|TaskRunnerFactory
name|taskRunnerFactory
init|=
literal|null
decl_stmt|;
specifier|private
name|MessageAuthorizationPolicy
name|messageAuthorizationPolicy
decl_stmt|;
specifier|private
name|DiscoveryAgent
name|discoveryAgent
decl_stmt|;
specifier|protected
name|CopyOnWriteArrayList
name|connections
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|TransportStatusDetector
name|statusDector
decl_stmt|;
specifier|private
name|ConnectorStatistics
name|statistics
init|=
operator|new
name|ConnectorStatistics
argument_list|()
decl_stmt|;
specifier|private
name|URI
name|discoveryUri
decl_stmt|;
specifier|private
name|URI
name|connectUri
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|boolean
name|disableAsyncDispatch
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|enableStatusMonitor
init|=
literal|true
decl_stmt|;
comment|/**      * @return Returns the connections.      */
specifier|public
name|CopyOnWriteArrayList
name|getConnections
parameter_list|()
block|{
return|return
name|connections
return|;
block|}
specifier|public
name|TransportConnector
parameter_list|()
block|{     }
specifier|public
name|TransportConnector
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|TransportServer
name|server
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|setServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
name|server
operator|.
name|getConnectURI
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|URI
name|uri
init|=
name|server
operator|.
name|getConnectURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
operator|&&
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"vm"
argument_list|)
condition|)
block|{
name|setEnableStatusMonitor
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Factory method to create a JMX managed version of this transport connector      */
specifier|public
name|ManagedTransportConnector
name|asManagedConnector
parameter_list|(
name|MBeanServer
name|mbeanServer
parameter_list|,
name|ObjectName
name|connectorName
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|ManagedTransportConnector
name|rc
init|=
operator|new
name|ManagedTransportConnector
argument_list|(
name|mbeanServer
argument_list|,
name|connectorName
argument_list|,
name|getBroker
argument_list|()
argument_list|,
name|getServer
argument_list|()
argument_list|)
decl_stmt|;
name|rc
operator|.
name|setTaskRunnerFactory
argument_list|(
name|getTaskRunnerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setUri
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setConnectUri
argument_list|(
name|connectUri
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setDiscoveryAgent
argument_list|(
name|discoveryAgent
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setDiscoveryUri
argument_list|(
name|discoveryUri
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setDisableAsyncDispatch
argument_list|(
name|disableAsyncDispatch
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setBrokerInfo
argument_list|(
name|brokerInfo
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|BrokerInfo
name|getBrokerInfo
parameter_list|()
block|{
return|return
name|brokerInfo
return|;
block|}
specifier|public
name|void
name|setBrokerInfo
parameter_list|(
name|BrokerInfo
name|brokerInfo
parameter_list|)
block|{
name|this
operator|.
name|brokerInfo
operator|=
name|brokerInfo
expr_stmt|;
block|}
specifier|public
name|TransportServer
name|getServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|setServer
argument_list|(
name|createTransportServer
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
specifier|public
name|Broker
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|setBroker
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|brokerInfo
operator|.
name|setBrokerId
argument_list|(
name|broker
operator|.
name|getBrokerId
argument_list|()
argument_list|)
expr_stmt|;
name|brokerInfo
operator|.
name|setPeerBrokerInfos
argument_list|(
name|broker
operator|.
name|getPeerBrokerInfos
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|brokerInfo
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setServer
parameter_list|(
name|TransportServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|this
operator|.
name|brokerInfo
operator|.
name|setBrokerURL
argument_list|(
name|server
operator|.
name|getConnectURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|TransportAcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
try|try
block|{
name|Connection
name|connection
init|=
name|createConnection
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not accept connection: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|setBrokerInfo
argument_list|(
name|brokerInfo
argument_list|)
expr_stmt|;
block|}
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|uri
operator|=
name|getConnectUri
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
return|return
name|uri
return|;
block|}
comment|/**      * Sets the server transport URI to use if there is not a      * {@link TransportServer} configured via the      * {@link #setServer(TransportServer)} method. This value is used to lazy      * create a {@link TransportServer} instance      *       * @param uri      */
specifier|public
name|void
name|setUri
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|TaskRunnerFactory
name|getTaskRunnerFactory
parameter_list|()
block|{
return|return
name|taskRunnerFactory
return|;
block|}
specifier|public
name|void
name|setTaskRunnerFactory
parameter_list|(
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
block|{
name|this
operator|.
name|taskRunnerFactory
operator|=
name|taskRunnerFactory
expr_stmt|;
block|}
comment|/**      * @return the statistics for this connector      */
specifier|public
name|ConnectorStatistics
name|getStatistics
parameter_list|()
block|{
return|return
name|statistics
return|;
block|}
specifier|public
name|MessageAuthorizationPolicy
name|getMessageAuthorizationPolicy
parameter_list|()
block|{
return|return
name|messageAuthorizationPolicy
return|;
block|}
comment|/**      * Sets the policy used to decide if the current connection is authorized to consume      * a given message      */
specifier|public
name|void
name|setMessageAuthorizationPolicy
parameter_list|(
name|MessageAuthorizationPolicy
name|messageAuthorizationPolicy
parameter_list|)
block|{
name|this
operator|.
name|messageAuthorizationPolicy
operator|=
name|messageAuthorizationPolicy
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|getServer
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|DiscoveryAgent
name|da
init|=
name|getDiscoveryAgent
argument_list|()
decl_stmt|;
if|if
condition|(
name|da
operator|!=
literal|null
condition|)
block|{
name|da
operator|.
name|setBrokerName
argument_list|(
name|getBrokerInfo
argument_list|()
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|da
operator|.
name|registerService
argument_list|(
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|da
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|enableStatusMonitor
condition|)
block|{
name|this
operator|.
name|statusDector
operator|=
operator|new
name|TransportStatusDetector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusDector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Started"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|ServiceStopper
name|ss
init|=
operator|new
name|ServiceStopper
argument_list|()
decl_stmt|;
if|if
condition|(
name|discoveryAgent
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|stop
argument_list|(
name|discoveryAgent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|stop
argument_list|(
name|server
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|statusDector
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|statusDector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TransportConnection
name|c
init|=
operator|(
name|TransportConnection
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|throwFirstException
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Stopped"
argument_list|)
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
block|{
name|TransportConnection
name|answer
init|=
operator|new
name|TransportConnection
argument_list|(
name|this
argument_list|,
name|transport
argument_list|,
name|broker
argument_list|,
name|disableAsyncDispatch
condition|?
literal|null
else|:
name|taskRunnerFactory
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setMessageAuthorizationPolicy
argument_list|(
name|messageAuthorizationPolicy
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|TransportServer
name|createTransportServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You must specify either a server or uri property"
argument_list|)
throw|;
block|}
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"You must specify the broker property. Maybe this connector should be added to a broker?"
argument_list|)
throw|;
block|}
return|return
name|TransportFactory
operator|.
name|bind
argument_list|(
name|broker
operator|.
name|getBrokerId
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|,
name|uri
argument_list|)
return|;
block|}
specifier|public
name|DiscoveryAgent
name|getDiscoveryAgent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|discoveryAgent
operator|==
literal|null
condition|)
block|{
name|discoveryAgent
operator|=
name|createDiscoveryAgent
argument_list|()
expr_stmt|;
block|}
return|return
name|discoveryAgent
return|;
block|}
specifier|protected
name|DiscoveryAgent
name|createDiscoveryAgent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|discoveryUri
operator|!=
literal|null
condition|)
block|{
return|return
name|DiscoveryAgentFactory
operator|.
name|createDiscoveryAgent
argument_list|(
name|discoveryUri
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setDiscoveryAgent
parameter_list|(
name|DiscoveryAgent
name|discoveryAgent
parameter_list|)
block|{
name|this
operator|.
name|discoveryAgent
operator|=
name|discoveryAgent
expr_stmt|;
block|}
specifier|public
name|URI
name|getDiscoveryUri
parameter_list|()
block|{
return|return
name|discoveryUri
return|;
block|}
specifier|public
name|void
name|setDiscoveryUri
parameter_list|(
name|URI
name|discoveryUri
parameter_list|)
block|{
name|this
operator|.
name|discoveryUri
operator|=
name|discoveryUri
expr_stmt|;
block|}
specifier|public
name|URI
name|getConnectUri
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|connectUri
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|connectUri
operator|=
name|server
operator|.
name|getConnectURI
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|connectUri
return|;
block|}
specifier|public
name|void
name|setConnectUri
parameter_list|(
name|URI
name|transportUri
parameter_list|)
block|{
name|this
operator|.
name|connectUri
operator|=
name|transportUri
expr_stmt|;
block|}
specifier|public
name|void
name|onStarted
parameter_list|(
name|TransportConnection
name|connection
parameter_list|)
block|{
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onStopped
parameter_list|(
name|TransportConnection
name|connection
parameter_list|)
block|{
name|connections
operator|.
name|remove
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|getUri
argument_list|()
expr_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|uri
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|rc
init|=
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
name|rc
operator|=
name|super
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|boolean
name|isDisableAsyncDispatch
parameter_list|()
block|{
return|return
name|disableAsyncDispatch
return|;
block|}
specifier|public
name|void
name|setDisableAsyncDispatch
parameter_list|(
name|boolean
name|disableAsyncDispatch
parameter_list|)
block|{
name|this
operator|.
name|disableAsyncDispatch
operator|=
name|disableAsyncDispatch
expr_stmt|;
block|}
comment|/**      * @return the enableStatusMonitor      */
specifier|public
name|boolean
name|isEnableStatusMonitor
parameter_list|()
block|{
return|return
name|enableStatusMonitor
return|;
block|}
comment|/**      * @param enableStatusMonitor the enableStatusMonitor to set      */
specifier|public
name|void
name|setEnableStatusMonitor
parameter_list|(
name|boolean
name|enableStatusMonitor
parameter_list|)
block|{
name|this
operator|.
name|enableStatusMonitor
operator|=
name|enableStatusMonitor
expr_stmt|;
block|}
block|}
end_class

end_unit

