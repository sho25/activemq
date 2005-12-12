begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *   * Copyright 2005 (C) Simula Labs Inc.  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|discovery
operator|.
name|multicast
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
name|DatagramPacket
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MulticastSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|DiscoveryListener
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * A {@link DiscoveryAgent} using<a href="http://www.zeroconf.org/">Zeroconf</a> via the<a  * href="http://jmdns.sf.net/">jmDNS</a> library  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|MulticastDiscoveryAgent
implements|implements
name|DiscoveryAgent
implements|,
name|Runnable
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
name|MulticastDiscoveryAgent
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DISCOVERY_URI_STRING
init|=
literal|"multicast://224.1.2.3:6155"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_SUFFIX
init|=
literal|"ActiveMQ-4."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ALIVE
init|=
literal|"alive."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEAD
init|=
literal|"dead."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DELIMITER
init|=
literal|"%"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BUFF_SIZE
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_IDLE_TIME
init|=
literal|500
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|HEARTBEAT_MISS_BEFORE_DEATH
init|=
literal|4
decl_stmt|;
specifier|private
name|int
name|timeToLive
init|=
literal|1
decl_stmt|;
specifier|private
name|boolean
name|loopBackMode
init|=
literal|false
decl_stmt|;
specifier|private
name|Map
name|services
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|brokers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|group
init|=
literal|"default"
decl_stmt|;
specifier|private
name|String
name|brokerName
decl_stmt|;
specifier|private
name|URI
name|discoveryURI
decl_stmt|;
specifier|private
name|InetAddress
name|inetAddress
decl_stmt|;
specifier|private
name|SocketAddress
name|sockAddress
decl_stmt|;
specifier|private
name|DiscoveryListener
name|discoveryListener
decl_stmt|;
specifier|private
name|String
name|selfService
decl_stmt|;
specifier|private
name|MulticastSocket
name|mcast
decl_stmt|;
specifier|private
name|Thread
name|runner
decl_stmt|;
specifier|private
name|long
name|keepAliveInterval
init|=
name|DEFAULT_IDLE_TIME
decl_stmt|;
specifier|private
name|long
name|lastAdvertizeTime
init|=
literal|0
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**      * Set the discovery listener      *       * @param listener      */
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
name|discoveryListener
operator|=
name|listener
expr_stmt|;
block|}
comment|/**      * register a service      */
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
name|this
operator|.
name|selfService
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|doAdvertizeSelf
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Get the group used for discovery      *       * @return the group      */
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
comment|/**      * Set the group for discovery      *       * @param group      */
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
comment|/**      * @return Returns the brokerName.      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|brokerName
return|;
block|}
comment|/**      * @param brokerName The brokerName to set.      */
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
if|if
condition|(
name|brokerName
operator|!=
literal|null
condition|)
block|{
name|brokerName
operator|=
name|brokerName
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|brokerName
operator|=
name|brokerName
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|brokerName
operator|=
name|brokerName
operator|.
name|replace
argument_list|(
literal|'%'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
block|}
comment|/**      * @return Returns the loopBackMode.      */
specifier|public
name|boolean
name|isLoopBackMode
parameter_list|()
block|{
return|return
name|loopBackMode
return|;
block|}
comment|/**      * @param loopBackMode      *            The loopBackMode to set.      */
specifier|public
name|void
name|setLoopBackMode
parameter_list|(
name|boolean
name|loopBackMode
parameter_list|)
block|{
name|this
operator|.
name|loopBackMode
operator|=
name|loopBackMode
expr_stmt|;
block|}
comment|/**      * @return Returns the timeToLive.      */
specifier|public
name|int
name|getTimeToLive
parameter_list|()
block|{
return|return
name|timeToLive
return|;
block|}
comment|/**      * @param timeToLive      *            The timeToLive to set.      */
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|int
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
comment|/**      * @return the discoveryURI      */
specifier|public
name|URI
name|getDiscoveryURI
parameter_list|()
block|{
return|return
name|discoveryURI
return|;
block|}
comment|/**      * Set the discoveryURI      *       * @param discoveryURI      */
specifier|public
name|void
name|setDiscoveryURI
parameter_list|(
name|URI
name|discoveryURI
parameter_list|)
block|{
name|this
operator|.
name|discoveryURI
operator|=
name|discoveryURI
expr_stmt|;
block|}
specifier|public
name|long
name|getKeepAliveInterval
parameter_list|()
block|{
return|return
name|keepAliveInterval
return|;
block|}
specifier|public
name|void
name|setKeepAliveInterval
parameter_list|(
name|long
name|keepAliveInterval
parameter_list|)
block|{
name|this
operator|.
name|keepAliveInterval
operator|=
name|keepAliveInterval
expr_stmt|;
block|}
comment|/**      * start the discovery agent      *       * @throws Exception      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|group
operator|==
literal|null
operator|||
name|group
operator|.
name|length
argument_list|()
operator|==
literal|0
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
if|if
condition|(
name|brokerName
operator|==
literal|null
operator|||
name|brokerName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"brokerName not set"
argument_list|)
expr_stmt|;
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
name|log
operator|.
name|warn
argument_list|(
literal|"The type '"
operator|+
name|type
operator|+
literal|"' should end with '.' to be a valid Discovery type"
argument_list|)
expr_stmt|;
name|type
operator|+=
literal|"."
expr_stmt|;
block|}
if|if
condition|(
name|discoveryURI
operator|==
literal|null
condition|)
block|{
name|discoveryURI
operator|=
operator|new
name|URI
argument_list|(
name|DEFAULT_DISCOVERY_URI_STRING
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|inetAddress
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|discoveryURI
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|sockAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|this
operator|.
name|inetAddress
argument_list|,
name|discoveryURI
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|mcast
operator|=
operator|new
name|MulticastSocket
argument_list|(
name|discoveryURI
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|mcast
operator|.
name|setLoopbackMode
argument_list|(
name|loopBackMode
argument_list|)
expr_stmt|;
name|mcast
operator|.
name|setTimeToLive
argument_list|(
name|getTimeToLive
argument_list|()
argument_list|)
expr_stmt|;
name|mcast
operator|.
name|joinGroup
argument_list|(
name|inetAddress
argument_list|)
expr_stmt|;
name|mcast
operator|.
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|keepAliveInterval
argument_list|)
expr_stmt|;
name|runner
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|runner
operator|.
name|setName
argument_list|(
literal|"MulticastDiscovery: "
operator|+
name|selfService
argument_list|)
expr_stmt|;
name|runner
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
name|doAdvertizeSelf
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * stop the channel      *       * @throws Exception      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|doAdvertizeSelf
argument_list|()
expr_stmt|;
name|mcast
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|group
operator|+
literal|"."
operator|+
name|TYPE_SUFFIX
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BUFF_SIZE
index|]
decl_stmt|;
name|DatagramPacket
name|packet
init|=
operator|new
name|DatagramPacket
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|doTimeKeepingServices
argument_list|()
expr_stmt|;
try|try
block|{
name|mcast
operator|.
name|receive
argument_list|(
name|packet
argument_list|)
expr_stmt|;
if|if
condition|(
name|packet
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|packet
operator|.
name|getData
argument_list|()
argument_list|,
name|packet
operator|.
name|getOffset
argument_list|()
argument_list|,
name|packet
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|processData
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|se
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"failed to process packet: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|processData
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|discoveryListener
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|payload
init|=
name|str
operator|.
name|substring
argument_list|(
name|getType
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|payload
operator|.
name|startsWith
argument_list|(
name|ALIVE
argument_list|)
condition|)
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|(
name|payload
operator|.
name|substring
argument_list|(
name|ALIVE
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|service
init|=
name|payload
operator|.
name|substring
argument_list|(
name|ALIVE
operator|.
name|length
argument_list|()
operator|+
name|brokerName
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|brokerName
operator|.
name|equals
argument_list|(
name|this
operator|.
name|brokerName
argument_list|)
condition|)
block|{
name|processAlive
argument_list|(
name|brokerName
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|(
name|payload
operator|.
name|substring
argument_list|(
name|DEAD
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|service
init|=
name|payload
operator|.
name|substring
argument_list|(
name|DEAD
operator|.
name|length
argument_list|()
operator|+
name|brokerName
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|brokerName
operator|.
name|equals
argument_list|(
name|this
operator|.
name|brokerName
argument_list|)
condition|)
block|{
name|processDead
argument_list|(
name|brokerName
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|doTimeKeepingServices
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|currentTime
operator|-
name|keepAliveInterval
operator|)
operator|>
name|lastAdvertizeTime
condition|)
block|{
name|doAdvertizeSelf
argument_list|()
expr_stmt|;
name|lastAdvertizeTime
operator|=
name|currentTime
expr_stmt|;
block|}
name|doExpireOldServices
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doAdvertizeSelf
parameter_list|()
block|{
if|if
condition|(
name|selfService
operator|!=
literal|null
condition|)
block|{
name|String
name|payload
init|=
name|getType
argument_list|()
decl_stmt|;
name|payload
operator|+=
name|started
operator|.
name|get
argument_list|()
condition|?
name|ALIVE
else|:
name|DEAD
expr_stmt|;
name|payload
operator|+=
name|DELIMITER
operator|+
name|brokerName
operator|+
name|DELIMITER
expr_stmt|;
name|payload
operator|+=
name|selfService
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|payload
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DatagramPacket
name|packet
init|=
operator|new
name|DatagramPacket
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|sockAddress
argument_list|)
decl_stmt|;
name|mcast
operator|.
name|send
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to advertise our service: "
operator|+
name|payload
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|processAlive
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|String
name|service
parameter_list|)
block|{
if|if
condition|(
name|selfService
operator|==
literal|null
operator|||
operator|!
name|service
operator|.
name|equals
argument_list|(
name|selfService
argument_list|)
condition|)
block|{
name|AtomicLong
name|lastKeepAlive
init|=
operator|(
name|AtomicLong
operator|)
name|services
operator|.
name|get
argument_list|(
name|service
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastKeepAlive
operator|==
literal|null
condition|)
block|{
name|lastKeepAlive
operator|=
operator|new
name|AtomicLong
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|services
operator|.
name|put
argument_list|(
name|service
argument_list|,
name|lastKeepAlive
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|put
argument_list|(
name|service
argument_list|,
name|brokerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|discoveryListener
operator|!=
literal|null
condition|)
block|{
name|DiscoveryEvent
name|event
init|=
operator|new
name|DiscoveryEvent
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|event
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|discoveryListener
operator|.
name|onServiceAdd
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|doAdvertizeSelf
argument_list|()
expr_stmt|;
block|}
name|lastKeepAlive
operator|.
name|set
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processDead
parameter_list|(
name|String
name|brokerName
parameter_list|,
name|String
name|service
parameter_list|)
block|{
if|if
condition|(
operator|!
name|service
operator|.
name|equals
argument_list|(
name|selfService
argument_list|)
condition|)
block|{
if|if
condition|(
name|services
operator|.
name|remove
argument_list|(
name|service
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|brokers
operator|.
name|remove
argument_list|(
name|service
argument_list|)
expr_stmt|;
if|if
condition|(
name|discoveryListener
operator|!=
literal|null
condition|)
block|{
name|DiscoveryEvent
name|event
init|=
operator|new
name|DiscoveryEvent
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|event
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|discoveryListener
operator|.
name|onServiceRemove
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|doExpireOldServices
parameter_list|()
block|{
name|long
name|expireTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
operator|(
name|keepAliveInterval
operator|*
name|HEARTBEAT_MISS_BEFORE_DEATH
operator|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|services
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|AtomicLong
name|lastHeartBeat
init|=
operator|(
name|AtomicLong
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastHeartBeat
operator|.
name|get
argument_list|()
operator|<
name|expireTime
condition|)
block|{
name|String
name|brokerName
init|=
operator|(
name|String
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|processDead
argument_list|(
name|brokerName
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|getBrokerName
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
name|int
name|start
init|=
name|str
operator|.
name|indexOf
argument_list|(
name|DELIMITER
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|>=
literal|0
condition|)
block|{
name|int
name|end
init|=
name|str
operator|.
name|indexOf
argument_list|(
name|DELIMITER
argument_list|,
name|start
operator|+
literal|1
argument_list|)
decl_stmt|;
name|result
operator|=
name|str
operator|.
name|substring
argument_list|(
name|start
operator|+
literal|1
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

