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
operator|.
name|zeroconf
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jmdns
operator|.
name|JmDNS
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jmdns
operator|.
name|ServiceEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jmdns
operator|.
name|ServiceInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jmdns
operator|.
name|ServiceListener
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
name|JMSExceptionSupport
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
name|MapHelper
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
comment|/**  * A {@link DiscoveryAgent} using<a href="http://www.zeroconf.org/">Zeroconf</a>  * via the<a href="http://jmdns.sf.net/">jmDNS</a> library  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ZeroconfDiscoveryAgent
implements|implements
name|DiscoveryAgent
implements|,
name|ServiceListener
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
name|ZeroconfDiscoveryAgent
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_SUFFIX
init|=
literal|"ActiveMQ-5."
decl_stmt|;
specifier|private
name|JmDNS
name|jmdns
decl_stmt|;
specifier|private
name|InetAddress
name|localAddress
decl_stmt|;
specifier|private
name|String
name|localhost
decl_stmt|;
specifier|private
name|int
name|weight
decl_stmt|;
specifier|private
name|int
name|priority
decl_stmt|;
specifier|private
name|DiscoveryListener
name|listener
decl_stmt|;
specifier|private
name|String
name|group
init|=
literal|"default"
decl_stmt|;
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|ServiceInfo
argument_list|>
name|serviceInfos
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ServiceInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|// DiscoveryAgent interface
comment|// -------------------------------------------------------------------------
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"You must specify a group to discover"
argument_list|)
throw|;
block|}
name|String
name|type
init|=
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|endsWith
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The type '"
operator|+
name|type
operator|+
literal|"' should end with '.' to be a valid Rendezvous type"
argument_list|)
expr_stmt|;
name|type
operator|+=
literal|"."
expr_stmt|;
block|}
try|try
block|{
comment|// force lazy construction
name|getJmdns
argument_list|()
expr_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Discovering service of type: "
operator|+
name|type
argument_list|)
expr_stmt|;
name|jmdns
operator|.
name|addServiceListener
argument_list|(
name|type
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to start JmDNS service: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|jmdns
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|ServiceInfo
argument_list|>
name|iter
init|=
name|serviceInfos
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
name|ServiceInfo
name|si
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|jmdns
operator|.
name|unregisterService
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
comment|// Close it down async since this could block for a while.
specifier|final
name|JmDNS
name|closeTarget
init|=
name|jmdns
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|JmDNSFactory
operator|.
name|onClose
argument_list|(
name|getLocalAddress
argument_list|()
argument_list|)
condition|)
block|{
name|closeTarget
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error closing JmDNS "
operator|+
name|getLocalhost
argument_list|()
operator|+
literal|". This exception will be ignored."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|jmdns
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|registerService
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ServiceInfo
name|si
init|=
name|createServiceInfo
argument_list|(
name|name
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
decl_stmt|;
name|serviceInfos
operator|.
name|add
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|getJmdns
argument_list|()
operator|.
name|registerService
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
comment|// ServiceListener interface
comment|// -------------------------------------------------------------------------
specifier|public
name|void
name|addService
parameter_list|(
name|JmDNS
name|jmDNS
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"addService with type: "
operator|+
name|type
operator|+
literal|" name: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onServiceAdd
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|jmDNS
operator|.
name|requestServiceInfo
argument_list|(
name|type
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeService
parameter_list|(
name|JmDNS
name|jmDNS
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removeService with type: "
operator|+
name|type
operator|+
literal|" name: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onServiceRemove
argument_list|(
operator|new
name|DiscoveryEvent
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|serviceAdded
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
block|{
name|addService
argument_list|(
name|event
operator|.
name|getDNS
argument_list|()
argument_list|,
name|event
operator|.
name|getType
argument_list|()
argument_list|,
name|event
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serviceRemoved
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
block|{
name|removeService
argument_list|(
name|event
operator|.
name|getDNS
argument_list|()
argument_list|,
name|event
operator|.
name|getType
argument_list|()
argument_list|,
name|event
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serviceResolved
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
block|{     }
specifier|public
name|void
name|resolveService
parameter_list|(
name|JmDNS
name|jmDNS
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|name
parameter_list|,
name|ServiceInfo
name|serviceInfo
parameter_list|)
block|{     }
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
specifier|public
name|int
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
specifier|public
name|void
name|setWeight
parameter_list|(
name|int
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
specifier|public
name|JmDNS
name|getJmdns
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|jmdns
operator|==
literal|null
condition|)
block|{
name|jmdns
operator|=
name|createJmDNS
argument_list|()
expr_stmt|;
block|}
return|return
name|jmdns
return|;
block|}
specifier|public
name|void
name|setJmdns
parameter_list|(
name|JmDNS
name|jmdns
parameter_list|)
block|{
name|this
operator|.
name|jmdns
operator|=
name|jmdns
expr_stmt|;
block|}
specifier|public
name|InetAddress
name|getLocalAddress
parameter_list|()
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
name|localAddress
operator|==
literal|null
condition|)
block|{
name|localAddress
operator|=
name|createLocalAddress
argument_list|()
expr_stmt|;
block|}
return|return
name|localAddress
return|;
block|}
specifier|public
name|void
name|setLocalAddress
parameter_list|(
name|InetAddress
name|localAddress
parameter_list|)
block|{
name|this
operator|.
name|localAddress
operator|=
name|localAddress
expr_stmt|;
block|}
specifier|public
name|String
name|getLocalhost
parameter_list|()
block|{
return|return
name|localhost
return|;
block|}
specifier|public
name|void
name|setLocalhost
parameter_list|(
name|String
name|localhost
parameter_list|)
block|{
name|this
operator|.
name|localhost
operator|=
name|localhost
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|ServiceInfo
name|createServiceInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|map
parameter_list|)
block|{
name|int
name|port
init|=
name|MapHelper
operator|.
name|getInt
argument_list|(
name|map
argument_list|,
literal|"port"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Registering service type: "
operator|+
name|type
operator|+
literal|" name: "
operator|+
name|name
operator|+
literal|" details: "
operator|+
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|ServiceInfo
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|name
operator|+
literal|"."
operator|+
name|type
argument_list|,
name|port
argument_list|,
name|weight
argument_list|,
name|priority
argument_list|,
literal|""
argument_list|)
return|;
block|}
specifier|protected
name|JmDNS
name|createJmDNS
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|JmDNSFactory
operator|.
name|create
argument_list|(
name|getLocalAddress
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|InetAddress
name|createLocalAddress
parameter_list|()
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
name|localhost
operator|!=
literal|null
condition|)
block|{
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
name|localhost
argument_list|)
return|;
block|}
return|return
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
return|;
block|}
specifier|public
name|void
name|setDiscoveryListener
parameter_list|(
name|DiscoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
literal|"_"
operator|+
name|group
operator|+
literal|"."
operator|+
name|TYPE_SUFFIX
return|;
block|}
specifier|public
name|void
name|serviceFailed
parameter_list|(
name|DiscoveryEvent
name|event
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: is there a way to notify the JmDNS that the service failed?
block|}
block|}
end_class

end_unit

