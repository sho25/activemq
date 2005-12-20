begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
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
name|org
operator|.
name|activemq
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * @org.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NetworkConnector
implements|implements
name|Service
implements|,
name|DiscoveryListener
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
name|NetworkConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DiscoveryAgent
name|discoveryAgent
decl_stmt|;
specifier|private
name|URI
name|localURI
decl_stmt|;
specifier|private
name|ConcurrentHashMap
name|bridges
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|brokerName
decl_stmt|;
name|boolean
name|failover
init|=
literal|true
decl_stmt|;
specifier|public
name|NetworkConnector
parameter_list|()
block|{     }
specifier|public
name|NetworkConnector
parameter_list|(
name|URI
name|localURI
parameter_list|,
name|DiscoveryAgent
name|discoveryAgent
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|localURI
operator|=
name|localURI
expr_stmt|;
name|setDiscoveryAgent
argument_list|(
name|discoveryAgent
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
if|if
condition|(
name|localURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You must configure the 'localURI' property"
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
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|discoveryAgent
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onServiceAdd
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
name|log
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
comment|// Has it allready been added?
if|if
condition|(
name|bridges
operator|.
name|containsKey
argument_list|(
name|uri
argument_list|)
operator|||
name|localURI
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
return|return;
name|URI
name|connectUri
init|=
name|uri
decl_stmt|;
if|if
condition|(
name|failover
condition|)
block|{
try|try
block|{
name|connectUri
operator|=
operator|new
name|URI
argument_list|(
literal|"failover:"
operator|+
name|connectUri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not create failover URI: "
operator|+
name|connectUri
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Establishing network connection between "
operator|+
name|localURI
operator|+
literal|" and "
operator|+
name|event
operator|.
name|getBrokerName
argument_list|()
operator|+
literal|" at "
operator|+
name|connectUri
argument_list|)
expr_stmt|;
name|Transport
name|localTransport
decl_stmt|;
try|try
block|{
name|localTransport
operator|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|localURI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
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
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|Transport
name|remoteTransport
decl_stmt|;
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
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|localTransport
argument_list|)
expr_stmt|;
name|log
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
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|Bridge
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
name|bridges
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|bridge
argument_list|)
expr_stmt|;
try|try
block|{
name|bridge
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
name|log
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
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|log
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
name|Bridge
name|bridge
init|=
operator|(
name|Bridge
operator|)
name|bridges
operator|.
name|get
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
return|return;
name|ServiceSupport
operator|.
name|dispose
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
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
name|this
operator|.
name|discoveryAgent
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|URI
name|getLocalUri
parameter_list|()
throws|throws
name|URISyntaxException
block|{
return|return
name|localURI
return|;
block|}
specifier|public
name|void
name|setLocalUri
parameter_list|(
name|URI
name|localURI
parameter_list|)
block|{
name|this
operator|.
name|localURI
operator|=
name|localURI
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
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Bridge
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
return|return
operator|new
name|DemandForwardingBridge
argument_list|(
name|localTransport
argument_list|,
name|remoteTransport
argument_list|)
block|{
specifier|protected
name|void
name|serviceRemoteException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|super
operator|.
name|serviceRemoteException
argument_list|(
name|error
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Notify the discovery agent that the remote broker failed.
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
block|{                 }
block|}
block|}
return|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
if|if
condition|(
name|discoveryAgent
operator|!=
literal|null
condition|)
block|{
name|discoveryAgent
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isFailover
parameter_list|()
block|{
return|return
name|failover
return|;
block|}
specifier|public
name|void
name|setFailover
parameter_list|(
name|boolean
name|reliable
parameter_list|)
block|{
name|this
operator|.
name|failover
operator|=
name|reliable
expr_stmt|;
block|}
block|}
end_class

end_unit

