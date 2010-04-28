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
name|network
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|SslContext
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
name|DiscoveryEvent
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
name|transport
operator|.
name|discovery
operator|.
name|DiscoveryListener
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
name|IntrospectionSupport
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
name|activemq
operator|.
name|util
operator|.
name|URISupport
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
comment|/**  * A network connector which uses a discovery agent to detect the remote brokers  * available and setup a connection to each available remote broker  *   * @org.apache.xbean.XBean element="networkConnector"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DiscoveryNetworkConnector
extends|extends
name|NetworkConnector
implements|implements
name|DiscoveryListener
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
name|DiscoveryNetworkConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DiscoveryAgent
name|discoveryAgent
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
decl_stmt|;
specifier|public
name|DiscoveryNetworkConnector
parameter_list|()
block|{     }
specifier|public
name|DiscoveryNetworkConnector
parameter_list|(
name|URI
name|discoveryURI
parameter_list|)
throws|throws
name|IOException
block|{
name|setUri
argument_list|(
name|discoveryURI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUri
parameter_list|(
name|URI
name|discoveryURI
parameter_list|)
throws|throws
name|IOException
block|{
name|setDiscoveryAgent
argument_list|(
name|DiscoveryAgentFactory
operator|.
name|createDiscoveryAgent
argument_list|(
name|discoveryURI
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|parameters
operator|=
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|discoveryURI
argument_list|)
expr_stmt|;
comment|// allow discovery agent to grab it's parameters
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|getDiscoveryAgent
argument_list|()
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"failed to parse query parameters from discoveryURI: "
operator|+
name|discoveryURI
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onServiceAdd
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
block|{
comment|// Ignore events once we start stopping.
if|if
condition|(
name|serviceSupport
operator|.
name|isStopped
argument_list|()
operator|||
name|serviceSupport
operator|.
name|isStopping
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|url
init|=
name|event
operator|.
name|getServiceName
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not connect to remote URI: "
operator|+
name|url
operator|+
literal|" due to bad URI syntax: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Should we try to connect to that URI?
if|if
condition|(
name|bridges
operator|.
name|containsKey
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discovery agent generated a duplicate onServiceAdd event for: "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|localURI
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|||
operator|(
name|connectionFilter
operator|!=
literal|null
operator|&&
operator|!
name|connectionFilter
operator|.
name|connectTo
argument_list|(
name|uri
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"not connecting loopback: "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return;
block|}
name|URI
name|connectUri
init|=
name|uri
decl_stmt|;
try|try
block|{
name|connectUri
operator|=
name|URISupport
operator|.
name|applyParameters
argument_list|(
name|connectUri
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not apply query parameters: "
operator|+
name|parameters
operator|+
literal|" to: "
operator|+
name|connectUri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Establishing network connection from "
operator|+
name|localURI
operator|+
literal|" to "
operator|+
name|connectUri
argument_list|)
expr_stmt|;
name|Transport
name|remoteTransport
decl_stmt|;
name|Transport
name|localTransport
decl_stmt|;
try|try
block|{
comment|// Allows the transport to access the broker's ssl configuration.
name|SslContext
operator|.
name|setCurrentSslContext
argument_list|(
name|getBrokerService
argument_list|()
operator|.
name|getSslContext
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|remoteTransport
operator|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|connectUri
argument_list|)
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
name|warn
argument_list|(
literal|"Could not connect to remote URI: "
operator|+
name|connectUri
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connection failure exception: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|localTransport
operator|=
name|createLocalTransport
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
name|remoteTransport
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not connect to local URI: "
operator|+
name|localURI
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connection failure exception: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
finally|finally
block|{
name|SslContext
operator|.
name|setCurrentSslContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|NetworkBridge
name|bridge
init|=
name|createBridge
argument_list|(
name|localTransport
argument_list|,
name|remoteTransport
argument_list|,
name|event
argument_list|)
decl_stmt|;
try|try
block|{
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
name|bridges
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|bridge
argument_list|)
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
name|localTransport
argument_list|)
expr_stmt|;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|remoteTransport
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not start network bridge between: "
operator|+
name|localURI
operator|+
literal|" and: "
operator|+
name|uri
operator|+
literal|" due to: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Start failure exception: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|discoveryAgent
operator|.
name|serviceFailed
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discovery agent failure while handling failure event: "
operator|+
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
specifier|public
name|void
name|onServiceRemove
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
block|{
name|String
name|url
init|=
name|event
operator|.
name|getServiceName
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not connect to remote URI: "
operator|+
name|url
operator|+
literal|" due to bad URI syntax: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|NetworkBridge
name|bridge
init|=
name|bridges
operator|.
name|remove
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|bridge
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|DiscoveryAgent
name|getDiscoveryAgent
parameter_list|()
block|{
return|return
name|discoveryAgent
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
if|if
condition|(
name|discoveryAgent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|discoveryAgent
operator|.
name|setDiscoveryListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|handleStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|discoveryAgent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You must configure the 'discoveryAgent' property"
argument_list|)
throw|;
block|}
name|this
operator|.
name|discoveryAgent
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|handleStart
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|handleStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|NetworkBridge
argument_list|>
name|i
init|=
name|bridges
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NetworkBridge
name|bridge
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|bridge
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stopper
operator|.
name|onException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|this
operator|.
name|discoveryAgent
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stopper
operator|.
name|onException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|handleStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|NetworkBridge
name|createBridge
parameter_list|(
name|Transport
name|localTransport
parameter_list|,
name|Transport
name|remoteTransport
parameter_list|,
specifier|final
name|DiscoveryEvent
name|event
parameter_list|)
block|{
name|NetworkBridgeListener
name|listener
init|=
operator|new
name|NetworkBridgeListener
argument_list|()
block|{
specifier|public
name|void
name|bridgeFailed
parameter_list|()
block|{
if|if
condition|(
operator|!
name|serviceSupport
operator|.
name|isStopped
argument_list|()
condition|)
block|{
try|try
block|{
name|discoveryAgent
operator|.
name|serviceFailed
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                     }
block|}
block|}
specifier|public
name|void
name|onStart
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
name|registerNetworkBridgeMBean
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onStop
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
name|unregisterNetworkBridgeMBean
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|DemandForwardingBridge
name|result
init|=
name|NetworkBridgeFactory
operator|.
name|createBridge
argument_list|(
name|this
argument_list|,
name|localTransport
argument_list|,
name|remoteTransport
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBrokerService
argument_list|(
name|getBrokerService
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|configureBridge
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|String
name|name
init|=
name|super
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|discoveryAgent
operator|.
name|toString
argument_list|()
expr_stmt|;
name|super
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DiscoveryNetworkConnector:"
operator|+
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|getBrokerService
argument_list|()
return|;
block|}
block|}
end_class

end_unit

