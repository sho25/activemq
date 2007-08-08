begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|Hashtable
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|CopyOnWriteArrayList
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
name|MalformedObjectNameException
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
name|Service
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
name|jmx
operator|.
name|NetworkBridgeView
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
name|NetworkBridgeViewMBean
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
name|ActiveMQDestination
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
name|util
operator|.
name|JMXSupport
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NetworkConnector
extends|extends
name|NetworkBridgeConfiguration
implements|implements
name|Service
block|{
specifier|protected
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
specifier|protected
name|URI
name|localURI
decl_stmt|;
specifier|private
name|Set
name|durableDestinations
decl_stmt|;
specifier|private
name|List
name|excludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|dynamicallyIncludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|private
name|List
name|staticallyIncludedDestinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|ConnectionFilter
name|connectionFilter
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|ObjectName
name|objectName
decl_stmt|;
specifier|protected
name|ServiceSupport
name|serviceSupport
init|=
operator|new
name|ServiceSupport
argument_list|()
block|{
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|handleStart
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
name|handleStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/**      * @return Returns the durableDestinations.      */
specifier|public
name|Set
name|getDurableDestinations
parameter_list|()
block|{
return|return
name|durableDestinations
return|;
block|}
comment|/**      * @param durableDestinations The durableDestinations to set.      */
specifier|public
name|void
name|setDurableDestinations
parameter_list|(
name|Set
name|durableDestinations
parameter_list|)
block|{
name|this
operator|.
name|durableDestinations
operator|=
name|durableDestinations
expr_stmt|;
block|}
comment|/**      * @return Returns the excludedDestinations.      */
specifier|public
name|List
name|getExcludedDestinations
parameter_list|()
block|{
return|return
name|excludedDestinations
return|;
block|}
comment|/**      * @param excludedDestinations The excludedDestinations to set.      */
specifier|public
name|void
name|setExcludedDestinations
parameter_list|(
name|List
name|excludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|excludedDestinations
operator|=
name|excludedDestinations
expr_stmt|;
block|}
specifier|public
name|void
name|addExcludedDestination
parameter_list|(
name|ActiveMQDestination
name|destiantion
parameter_list|)
block|{
name|this
operator|.
name|excludedDestinations
operator|.
name|add
argument_list|(
name|destiantion
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the staticallyIncludedDestinations.      */
specifier|public
name|List
name|getStaticallyIncludedDestinations
parameter_list|()
block|{
return|return
name|staticallyIncludedDestinations
return|;
block|}
comment|/**      * @param staticallyIncludedDestinations The staticallyIncludedDestinations      *                to set.      */
specifier|public
name|void
name|setStaticallyIncludedDestinations
parameter_list|(
name|List
name|staticallyIncludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|staticallyIncludedDestinations
operator|=
name|staticallyIncludedDestinations
expr_stmt|;
block|}
specifier|public
name|void
name|addStaticallyIncludedDestination
parameter_list|(
name|ActiveMQDestination
name|destiantion
parameter_list|)
block|{
name|this
operator|.
name|staticallyIncludedDestinations
operator|.
name|add
argument_list|(
name|destiantion
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the dynamicallyIncludedDestinations.      */
specifier|public
name|List
name|getDynamicallyIncludedDestinations
parameter_list|()
block|{
return|return
name|dynamicallyIncludedDestinations
return|;
block|}
comment|/**      * @param dynamicallyIncludedDestinations The      *                dynamicallyIncludedDestinations to set.      */
specifier|public
name|void
name|setDynamicallyIncludedDestinations
parameter_list|(
name|List
name|dynamicallyIncludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|dynamicallyIncludedDestinations
operator|=
name|dynamicallyIncludedDestinations
expr_stmt|;
block|}
specifier|public
name|void
name|addDynamicallyIncludedDestination
parameter_list|(
name|ActiveMQDestination
name|destiantion
parameter_list|)
block|{
name|this
operator|.
name|dynamicallyIncludedDestinations
operator|.
name|add
argument_list|(
name|destiantion
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionFilter
name|getConnectionFilter
parameter_list|()
block|{
return|return
name|connectionFilter
return|;
block|}
specifier|public
name|void
name|setConnectionFilter
parameter_list|(
name|ConnectionFilter
name|connectionFilter
parameter_list|)
block|{
name|this
operator|.
name|connectionFilter
operator|=
name|connectionFilter
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|NetworkBridge
name|configureBridge
parameter_list|(
name|DemandForwardingBridgeSupport
name|result
parameter_list|)
block|{
name|List
name|destsList
init|=
name|getDynamicallyIncludedDestinations
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|dests
index|[]
init|=
operator|(
name|ActiveMQDestination
index|[]
operator|)
name|destsList
operator|.
name|toArray
argument_list|(
operator|new
name|ActiveMQDestination
index|[
name|destsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|result
operator|.
name|setDynamicallyIncludedDestinations
argument_list|(
name|dests
argument_list|)
expr_stmt|;
name|destsList
operator|=
name|getExcludedDestinations
argument_list|()
expr_stmt|;
name|dests
operator|=
operator|(
name|ActiveMQDestination
index|[]
operator|)
name|destsList
operator|.
name|toArray
argument_list|(
operator|new
name|ActiveMQDestination
index|[
name|destsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|setExcludedDestinations
argument_list|(
name|dests
argument_list|)
expr_stmt|;
name|destsList
operator|=
name|getStaticallyIncludedDestinations
argument_list|()
expr_stmt|;
name|dests
operator|=
operator|(
name|ActiveMQDestination
index|[]
operator|)
name|destsList
operator|.
name|toArray
argument_list|(
operator|new
name|ActiveMQDestination
index|[
name|destsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|setStaticallyIncludedDestinations
argument_list|(
name|dests
argument_list|)
expr_stmt|;
if|if
condition|(
name|durableDestinations
operator|!=
literal|null
condition|)
block|{
name|ActiveMQDestination
index|[]
name|dest
init|=
operator|new
name|ActiveMQDestination
index|[
name|durableDestinations
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|dest
operator|=
operator|(
name|ActiveMQDestination
index|[]
operator|)
name|durableDestinations
operator|.
name|toArray
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDurableDestinations
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|Transport
name|createLocalTransport
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|TransportFactory
operator|.
name|connect
argument_list|(
name|localURI
argument_list|)
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|serviceSupport
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
name|serviceSupport
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|protected
name|void
name|handleStart
parameter_list|()
throws|throws
name|Exception
block|{
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
name|log
operator|.
name|info
argument_list|(
literal|"Network Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Started"
argument_list|)
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
name|log
operator|.
name|info
argument_list|(
literal|"Network Connector "
operator|+
name|getName
argument_list|()
operator|+
literal|" Stopped"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
name|getObjectName
parameter_list|()
block|{
return|return
name|objectName
return|;
block|}
specifier|public
name|void
name|setObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
block|{
name|this
operator|.
name|objectName
operator|=
name|objectName
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
name|brokerService
return|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
specifier|protected
name|void
name|registerNetworkBridgeMBean
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|getBrokerService
argument_list|()
operator|.
name|isUseJmx
argument_list|()
condition|)
return|return;
name|MBeanServer
name|mbeanServer
init|=
name|getBrokerService
argument_list|()
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|!=
literal|null
condition|)
block|{
name|NetworkBridgeViewMBean
name|view
init|=
operator|new
name|NetworkBridgeView
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
try|try
block|{
name|ObjectName
name|objectName
init|=
name|createNetworkBridgeObjectName
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
name|mbeanServer
operator|.
name|registerMBean
argument_list|(
name|view
argument_list|,
name|objectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Network bridge could not be registered in JMX: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|unregisterNetworkBridgeMBean
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
block|{
if|if
condition|(
operator|!
name|getBrokerService
argument_list|()
operator|.
name|isUseJmx
argument_list|()
condition|)
return|return;
name|MBeanServer
name|mbeanServer
init|=
name|getBrokerService
argument_list|()
operator|.
name|getManagementContext
argument_list|()
operator|.
name|getMBeanServer
argument_list|()
decl_stmt|;
if|if
condition|(
name|mbeanServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ObjectName
name|objectName
init|=
name|createNetworkBridgeObjectName
argument_list|(
name|bridge
argument_list|)
decl_stmt|;
name|mbeanServer
operator|.
name|unregisterMBean
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Network bridge could not be unregistered in JMX: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|ObjectName
name|createNetworkBridgeObjectName
parameter_list|(
name|NetworkBridge
name|bridge
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|ObjectName
name|connectorName
init|=
name|getObjectName
argument_list|()
decl_stmt|;
name|Hashtable
name|map
init|=
name|connectorName
operator|.
name|getKeyPropertyList
argument_list|()
decl_stmt|;
return|return
operator|new
name|ObjectName
argument_list|(
name|connectorName
operator|.
name|getDomain
argument_list|()
operator|+
literal|":"
operator|+
literal|"BrokerName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"BrokerName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"Type=NetworkBridge,"
operator|+
literal|"NetworkConnectorName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"NetworkConnectorName"
argument_list|)
argument_list|)
operator|+
literal|","
operator|+
literal|"Name="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|bridge
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

