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
name|pool
package|;
end_package

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
name|LinkedList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|ConnectionFactory
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnection
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
name|util
operator|.
name|IOExceptionSupport
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|ObjectPoolFactory
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
name|pool
operator|.
name|impl
operator|.
name|GenericObjectPoolFactory
import|;
end_import

begin_comment
comment|/**  * A JMS provider which pools Connection, Session and MessageProducer instances  * so it can be used with tools like<a href="http://camel.apache.org/activemq.html">Camel</a> and Spring's<a  * href="http://activemq.apache.org/spring-support.html">JmsTemplate and MessagListenerContainer</a>.  *   *<b>NOTE</b> this implementation does not pool consumers. Pooling makes sense for seldom used  * resources that are expensive to create and can remain idle a minimal cost. like sessions and producers.  * Consumers on the other hand, will consume messages even when idle due to<a   * href="http://activemq.apache.org/what-is-the-prefetch-limit-for.html">prefetch</a>.  * If you want to consider a consumer pool, configure an appropriate prefetch and a pool  * allocation strategy that is inclusive. Also note that message order guarantees will be  * lost across the consumer pool.   *   * @org.apache.xbean.XBean element="pooledConnectionFactory"  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|PooledConnectionFactory
implements|implements
name|ConnectionFactory
implements|,
name|Service
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PooledConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ConnectionKey
argument_list|,
name|LinkedList
argument_list|<
name|ConnectionPool
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|ConnectionKey
argument_list|,
name|LinkedList
argument_list|<
name|ConnectionPool
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ObjectPoolFactory
name|poolFactory
decl_stmt|;
specifier|private
name|int
name|maximumActive
init|=
literal|500
decl_stmt|;
specifier|private
name|int
name|maxConnections
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|idleTimeout
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|long
name|expiryTimeout
init|=
literal|0l
decl_stmt|;
specifier|public
name|PooledConnectionFactory
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PooledConnectionFactory
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PooledConnectionFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnection
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|Connection
name|createConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PooledConnectionFactory is stopped, skip create new connection."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ConnectionKey
name|key
init|=
operator|new
name|ConnectionKey
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|LinkedList
argument_list|<
name|ConnectionPool
argument_list|>
name|pools
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|pools
operator|==
literal|null
condition|)
block|{
name|pools
operator|=
operator|new
name|LinkedList
argument_list|<
name|ConnectionPool
argument_list|>
argument_list|()
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|pools
argument_list|)
expr_stmt|;
block|}
name|ConnectionPool
name|connection
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pools
operator|.
name|size
argument_list|()
operator|==
name|maxConnections
condition|)
block|{
name|connection
operator|=
name|pools
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
comment|// Now.. we might get a connection, but it might be that we need to
comment|// dump it..
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
name|connection
operator|.
name|expiredCheck
argument_list|()
condition|)
block|{
name|connection
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|ActiveMQConnection
name|delegate
init|=
name|createConnection
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|connection
operator|=
name|createConnectionPool
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
name|pools
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
operator|new
name|PooledConnection
argument_list|(
name|connection
argument_list|)
return|;
block|}
specifier|protected
name|ConnectionPool
name|createConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
name|ConnectionPool
name|result
init|=
operator|new
name|ConnectionPool
argument_list|(
name|connection
argument_list|,
name|getPoolFactory
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|setIdleTimeout
argument_list|(
name|getIdleTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setExpiryTimeout
argument_list|(
name|getExpiryTimeout
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|ActiveMQConnection
name|createConnection
parameter_list|(
name|ConnectionKey
name|key
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|key
operator|.
name|getUserName
argument_list|()
operator|==
literal|null
operator|&&
name|key
operator|.
name|getPassword
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|key
operator|.
name|getUserName
argument_list|()
argument_list|,
name|key
operator|.
name|getPassword
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * @see org.apache.activemq.service.Service#start()      */
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|stopped
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|createConnection
argument_list|()
expr_stmt|;
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
literal|"Create pooled connection during start failed."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|IOExceptionSupport
operator|.
name|create
argument_list|(
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stop the PooledConnectionFactory, number of connections in cache: "
operator|+
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|stopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|LinkedList
argument_list|<
name|ConnectionPool
argument_list|>
argument_list|>
name|iter
init|=
name|cache
operator|.
name|values
argument_list|()
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
for|for
control|(
name|ConnectionPool
name|connection
range|:
name|iter
operator|.
name|next
argument_list|()
control|)
block|{
try|try
block|{
name|connection
operator|.
name|close
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
name|warn
argument_list|(
literal|"Close connection failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ObjectPoolFactory
name|getPoolFactory
parameter_list|()
block|{
if|if
condition|(
name|poolFactory
operator|==
literal|null
condition|)
block|{
name|poolFactory
operator|=
name|createPoolFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|poolFactory
return|;
block|}
comment|/**      * Sets the object pool factory used to create individual session pools for      * each connection      */
specifier|public
name|void
name|setPoolFactory
parameter_list|(
name|ObjectPoolFactory
name|poolFactory
parameter_list|)
block|{
name|this
operator|.
name|poolFactory
operator|=
name|poolFactory
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumActive
parameter_list|()
block|{
return|return
name|maximumActive
return|;
block|}
comment|/**      * Sets the maximum number of active sessions per connection      */
specifier|public
name|void
name|setMaximumActive
parameter_list|(
name|int
name|maximumActive
parameter_list|)
block|{
name|this
operator|.
name|maximumActive
operator|=
name|maximumActive
expr_stmt|;
block|}
comment|/**      * @return the maxConnections      */
specifier|public
name|int
name|getMaxConnections
parameter_list|()
block|{
return|return
name|maxConnections
return|;
block|}
comment|/**      * @param maxConnections the maxConnections to set      */
specifier|public
name|void
name|setMaxConnections
parameter_list|(
name|int
name|maxConnections
parameter_list|)
block|{
name|this
operator|.
name|maxConnections
operator|=
name|maxConnections
expr_stmt|;
block|}
specifier|protected
name|ObjectPoolFactory
name|createPoolFactory
parameter_list|()
block|{
return|return
operator|new
name|GenericObjectPoolFactory
argument_list|(
literal|null
argument_list|,
name|maximumActive
argument_list|)
return|;
block|}
specifier|public
name|int
name|getIdleTimeout
parameter_list|()
block|{
return|return
name|idleTimeout
return|;
block|}
specifier|public
name|void
name|setIdleTimeout
parameter_list|(
name|int
name|idleTimeout
parameter_list|)
block|{
name|this
operator|.
name|idleTimeout
operator|=
name|idleTimeout
expr_stmt|;
block|}
comment|/**      * allow connections to expire, irrespective of load or idle time. This is useful with failover      * to force a reconnect from the pool, to reestablish load balancing or use of the master post recovery      *       * @param expiryTimeout non zero in milliseconds      */
specifier|public
name|void
name|setExpiryTimeout
parameter_list|(
name|long
name|expiryTimeout
parameter_list|)
block|{
name|this
operator|.
name|expiryTimeout
operator|=
name|expiryTimeout
expr_stmt|;
block|}
specifier|public
name|long
name|getExpiryTimeout
parameter_list|()
block|{
return|return
name|expiryTimeout
return|;
block|}
block|}
end_class

end_unit

