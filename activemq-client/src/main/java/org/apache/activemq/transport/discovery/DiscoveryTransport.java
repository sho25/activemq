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
name|transport
operator|.
name|discovery
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
name|CompositeTransport
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
name|TransportFilter
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
name|Suspendable
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
comment|/**  * A {@link ReliableTransportChannel} which uses a {@link DiscoveryAgent} to  * discover remote broker instances and dynamically connect to them.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|DiscoveryTransport
extends|extends
name|TransportFilter
implements|implements
name|DiscoveryListener
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
name|DiscoveryTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CompositeTransport
name|next
decl_stmt|;
specifier|private
name|DiscoveryAgent
name|discoveryAgent
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|URI
argument_list|>
name|serviceURIs
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|URI
argument_list|>
argument_list|()
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
name|DiscoveryTransport
parameter_list|(
name|CompositeTransport
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|"discoveryAgent not configured"
argument_list|)
throw|;
block|}
comment|// lets pass into the agent the broker name and connection details
name|discoveryAgent
operator|.
name|setDiscoveryListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|discoveryAgent
operator|.
name|start
argument_list|()
expr_stmt|;
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|ss
operator|.
name|stop
argument_list|(
name|discoveryAgent
argument_list|)
expr_stmt|;
name|ss
operator|.
name|stop
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|ss
operator|.
name|throwFirstException
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
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding new broker connection URL: "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URISupport
operator|.
name|applyParameters
argument_list|(
name|uri
argument_list|,
name|parameters
argument_list|,
name|DISCOVERED_OPTION_PREFIX
argument_list|)
expr_stmt|;
name|serviceURIs
operator|.
name|put
argument_list|(
name|event
operator|.
name|getServiceName
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|next
operator|.
name|add
argument_list|(
literal|false
argument_list|,
operator|new
name|URI
index|[]
block|{
name|uri
block|}
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
name|URI
name|uri
init|=
name|serviceURIs
operator|.
name|get
argument_list|(
name|event
operator|.
name|getServiceName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|remove
argument_list|(
literal|false
argument_list|,
operator|new
name|URI
index|[]
block|{
name|uri
block|}
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
block|}
specifier|public
name|void
name|setParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{
if|if
condition|(
name|discoveryAgent
operator|instanceof
name|Suspendable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Suspendable
operator|)
name|discoveryAgent
operator|)
operator|.
name|suspend
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|transportResumed
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{
if|if
condition|(
name|discoveryAgent
operator|instanceof
name|Suspendable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Suspendable
operator|)
name|discoveryAgent
operator|)
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|transportInterupted
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

