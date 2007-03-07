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
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NetworkConnector
extends|extends
name|ServiceSupport
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
name|String
name|brokerName
init|=
literal|"localhost"
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
name|boolean
name|dynamicOnly
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|conduitSubscriptions
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|decreaseNetworkConsumerPriority
decl_stmt|;
specifier|private
name|int
name|networkTTL
init|=
literal|1
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|"bridge"
decl_stmt|;
specifier|private
name|int
name|prefetchSize
init|=
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|dispatchAsync
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|boolean
name|bridgeTempDestinations
init|=
literal|true
decl_stmt|;
specifier|protected
name|ConnectionFilter
name|connectionFilter
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
comment|/**      * @return Returns the name.      */
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
name|name
operator|=
name|createName
argument_list|()
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
comment|/**      * @param name      *            The name to set.      */
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
name|getBrokerName
parameter_list|()
block|{
return|return
name|brokerName
return|;
block|}
comment|/**      * @param brokerName      *            The brokerName to set.      */
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
comment|/**      * @param durableDestinations      *            The durableDestinations to set.      */
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
comment|/**      * @return Returns the dynamicOnly.      */
specifier|public
name|boolean
name|isDynamicOnly
parameter_list|()
block|{
return|return
name|dynamicOnly
return|;
block|}
comment|/**      * @param dynamicOnly      *            The dynamicOnly to set.      */
specifier|public
name|void
name|setDynamicOnly
parameter_list|(
name|boolean
name|dynamicOnly
parameter_list|)
block|{
name|this
operator|.
name|dynamicOnly
operator|=
name|dynamicOnly
expr_stmt|;
block|}
comment|/**      * @return Returns the conduitSubscriptions.      */
specifier|public
name|boolean
name|isConduitSubscriptions
parameter_list|()
block|{
return|return
name|conduitSubscriptions
return|;
block|}
comment|/**      * @param conduitSubscriptions      *            The conduitSubscriptions to set.      */
specifier|public
name|void
name|setConduitSubscriptions
parameter_list|(
name|boolean
name|conduitSubscriptions
parameter_list|)
block|{
name|this
operator|.
name|conduitSubscriptions
operator|=
name|conduitSubscriptions
expr_stmt|;
block|}
comment|/**      * @return Returns the decreaseNetworkConsumerPriority.      */
specifier|public
name|boolean
name|isDecreaseNetworkConsumerPriority
parameter_list|()
block|{
return|return
name|decreaseNetworkConsumerPriority
return|;
block|}
comment|/**      * @param decreaseNetworkConsumerPriority      *            The decreaseNetworkConsumerPriority to set.      */
specifier|public
name|void
name|setDecreaseNetworkConsumerPriority
parameter_list|(
name|boolean
name|decreaseNetworkConsumerPriority
parameter_list|)
block|{
name|this
operator|.
name|decreaseNetworkConsumerPriority
operator|=
name|decreaseNetworkConsumerPriority
expr_stmt|;
block|}
comment|/**      * @return Returns the networkTTL.      */
specifier|public
name|int
name|getNetworkTTL
parameter_list|()
block|{
return|return
name|networkTTL
return|;
block|}
comment|/**      * @param networkTTL      *            The networkTTL to set.      */
specifier|public
name|void
name|setNetworkTTL
parameter_list|(
name|int
name|networkTTL
parameter_list|)
block|{
name|this
operator|.
name|networkTTL
operator|=
name|networkTTL
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
comment|/**      * @param excludedDestinations      *            The excludedDestinations to set.      */
specifier|public
name|void
name|setExcludedDestinations
parameter_list|(
name|List
name|exludedDestinations
parameter_list|)
block|{
name|this
operator|.
name|excludedDestinations
operator|=
name|exludedDestinations
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
comment|/**      * @param staticallyIncludedDestinations      *            The staticallyIncludedDestinations to set.      */
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
comment|/**      * @param dynamicallyIncludedDestinations      *            The dynamicallyIncludedDestinations to set.      */
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
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Bridge
name|configureBridge
parameter_list|(
name|DemandForwardingBridgeSupport
name|result
parameter_list|)
block|{
name|result
operator|.
name|setLocalBrokerName
argument_list|(
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setName
argument_list|(
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setNetworkTTL
argument_list|(
name|getNetworkTTL
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|result
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|result
operator|.
name|setPrefetchSize
argument_list|(
name|prefetchSize
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDispatchAsync
argument_list|(
name|dispatchAsync
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDecreaseNetworkConsumerPriority
argument_list|(
name|isDecreaseNetworkConsumerPriority
argument_list|()
argument_list|)
expr_stmt|;
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
name|result
operator|.
name|setBridgeTempDestinations
argument_list|(
name|bridgeTempDestinations
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
specifier|abstract
name|String
name|createName
parameter_list|()
function_decl|;
specifier|protected
name|void
name|doStart
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
name|doStop
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
name|boolean
name|isDispatchAsync
parameter_list|()
block|{
return|return
name|dispatchAsync
return|;
block|}
specifier|public
name|void
name|setDispatchAsync
parameter_list|(
name|boolean
name|dispatchAsync
parameter_list|)
block|{
name|this
operator|.
name|dispatchAsync
operator|=
name|dispatchAsync
expr_stmt|;
block|}
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|prefetchSize
return|;
block|}
specifier|public
name|void
name|setPrefetchSize
parameter_list|(
name|int
name|prefetchSize
parameter_list|)
block|{
name|this
operator|.
name|prefetchSize
operator|=
name|prefetchSize
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
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBridgeTempDestinations
parameter_list|()
block|{
return|return
name|bridgeTempDestinations
return|;
block|}
specifier|public
name|void
name|setBridgeTempDestinations
parameter_list|(
name|boolean
name|bridgeTempDestinations
parameter_list|)
block|{
name|this
operator|.
name|bridgeTempDestinations
operator|=
name|bridgeTempDestinations
expr_stmt|;
block|}
block|}
end_class

end_unit

