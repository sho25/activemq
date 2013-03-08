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
name|JMSExceptionSupport
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
name|KeyedObjectPool
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
name|KeyedPoolableObjectFactory
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
name|GenericKeyedObjectPool
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
comment|/**  * A JMS provider which pools Connection, Session and MessageProducer instances  * so it can be used with tools like<a href="http://camel.apache.org/activemq.html">Camel</a> and Spring's  *<a href="http://activemq.apache.org/spring-support.html">JmsTemplate and MessagListenerContainer</a>.  * Connections, sessions and producers are returned to a pool after use so that they can be reused later  * without having to undergo the cost of creating them again.  *  * b>NOTE:</b> while this implementation does allow the creation of a collection of active consumers,  * it does not 'pool' consumers. Pooling makes sense for connections, sessions and producers, which  * are expensive to create and can remain idle a minimal cost. Consumers, on the other hand, are usually  * just created at startup and left active, handling incoming messages as they come. When a consumer is  * complete, it is best to close it rather than return it to a pool for later reuse: this is because,  * even if a consumer is idle, ActiveMQ will keep delivering messages to the consumer's prefetch buffer,  * where they'll get held until the consumer is active again.  *  * If you are creating a collection of consumers (for example, for multi-threaded message consumption), you  * might want to consider using a lower prefetch value for each consumer (e.g. 10 or 20), to ensure that  * all messages don't end up going to just one of the consumers. See this FAQ entry for more detail:  * http://activemq.apache.org/i-do-not-receive-messages-in-my-second-consumer.html  *  * Optionally, one may configure the pool to examine and possibly evict objects as they sit idle in the  * pool. This is performed by an "idle object eviction" thread, which runs asynchronously. Caution should  * be used when configuring this optional feature. Eviction runs contend with client threads for access  * to objects in the pool, so if they run too frequently performance issues may result. The idle object  * eviction thread may be configured using the {@link setTimeBetweenExpirationCheckMillis} method.  By  * default the value is -1 which means no eviction thread will be run.  Set to a non-negative value to  * configure the idle eviction thread to run.  *  * @org.apache.xbean.XBean element="pooledConnectionFactory"  */
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PooledConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
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
specifier|final
name|GenericKeyedObjectPool
argument_list|<
name|ConnectionKey
argument_list|,
name|ConnectionPool
argument_list|>
name|connectionsPool
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|int
name|maximumActiveSessionPerConnection
init|=
literal|500
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
name|boolean
name|blockIfSessionPoolIsFull
init|=
literal|true
decl_stmt|;
specifier|private
name|long
name|expiryTimeout
init|=
literal|0l
decl_stmt|;
specifier|private
name|boolean
name|createConnectionOnStartup
init|=
literal|true
decl_stmt|;
comment|/**      * Creates new PooledConnectionFactory with a default ActiveMQConnectionFactory instance.      *<p/>      * The URI used to connect to ActiveMQ comes from the default value of ActiveMQConnectionFactory.      */
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
comment|/**      * Creates a new PooledConnectionFactory that will use the given broker URI to connect to      * ActiveMQ.      *      * @param brokerURL      *      The URI to use to configure the internal ActiveMQConnectionFactory.      */
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
comment|/**      * Creates a new PooledConnectionFactory that will use the given ActiveMQConnectionFactory to      * create new ActiveMQConnection instances that will be pooled.      *      * @param connectionFactory      *      The ActiveMQConnectionFactory to create new Connections for this pool.      */
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
name|this
operator|.
name|connectionsPool
operator|=
operator|new
name|GenericKeyedObjectPool
argument_list|<
name|ConnectionKey
argument_list|,
name|ConnectionPool
argument_list|>
argument_list|(
operator|new
name|KeyedPoolableObjectFactory
argument_list|<
name|ConnectionKey
argument_list|,
name|ConnectionPool
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|activateObject
parameter_list|(
name|ConnectionKey
name|key
parameter_list|,
name|ConnectionPool
name|connection
parameter_list|)
throws|throws
name|Exception
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|destroyObject
parameter_list|(
name|ConnectionKey
name|key
parameter_list|,
name|ConnectionPool
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Destroying connection: {}"
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
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
literal|"Close connection failed for connection: "
operator|+
name|connection
operator|+
literal|". This exception will be ignored."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ConnectionPool
name|makeObject
parameter_list|(
name|ConnectionKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|delegate
init|=
name|createConnection
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|ConnectionPool
name|connection
init|=
name|createConnectionPool
argument_list|(
name|delegate
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setIdleTimeout
argument_list|(
name|getIdleTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setExpiryTimeout
argument_list|(
name|getExpiryTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setMaximumActiveSessionPerConnection
argument_list|(
name|getMaximumActiveSessionPerConnection
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setBlockIfSessionPoolIsFull
argument_list|(
name|isBlockIfSessionPoolIsFull
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Created new connection: {}"
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|passivateObject
parameter_list|(
name|ConnectionKey
name|key
parameter_list|,
name|ConnectionPool
name|connection
parameter_list|)
throws|throws
name|Exception
block|{                 }
annotation|@
name|Override
specifier|public
name|boolean
name|validateObject
parameter_list|(
name|ConnectionKey
name|key
parameter_list|,
name|ConnectionPool
name|connection
parameter_list|)
block|{
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
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Connection has expired: {} and will be destroyed"
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Set max idle (not max active) since our connections always idle in the pool.
name|this
operator|.
name|connectionsPool
operator|.
name|setMaxIdle
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// We always want our validate method to control when idle objects are evicted.
name|this
operator|.
name|connectionsPool
operator|.
name|setTestOnBorrow
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectionsPool
operator|.
name|setTestWhileIdle
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the currently configured ConnectionFactory used to create the pooled Connections.      */
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|connectionFactory
return|;
block|}
comment|/**      * Sets the ConnectionFactory used to create new pooled Connections.      *<p/>      * Updates to this value do not affect Connections that were previously created and placed      * into the pool.  In order to allocate new Connections based off this new ConnectionFactory      * it is first necessary to {@link clear} the pooled Connections.      *      * @param connectionFactory      *      The factory to use to create pooled Connections.      */
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
annotation|@
name|Override
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
annotation|@
name|Override
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
name|ConnectionPool
name|connection
init|=
literal|null
decl_stmt|;
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
comment|// This will either return an existing non-expired ConnectionPool or it
comment|// will create a new one to meet the demand.
if|if
condition|(
name|connectionsPool
operator|.
name|getNumIdle
argument_list|(
name|key
argument_list|)
operator|<
name|getMaxConnections
argument_list|()
condition|)
block|{
try|try
block|{
comment|// we want borrowObject to return the one we added.
name|connectionsPool
operator|.
name|setLifo
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionsPool
operator|.
name|addObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Error while attempting to add new Connection to the pool"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// now we want the oldest one in the pool.
name|connectionsPool
operator|.
name|setLifo
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|connection
operator|=
name|connectionsPool
operator|.
name|borrowObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Error while attempting to retrieve a connection from the pool"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|connectionsPool
operator|.
name|returnObject
argument_list|(
name|key
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Error when returning connection to the pool"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|PooledConnection
argument_list|(
name|connection
argument_list|)
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
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Staring the PooledConnectionFactory: create on start = {}"
argument_list|,
name|isCreateConnectionOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|stopped
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCreateConnectionOnStartup
argument_list|()
condition|)
block|{
try|try
block|{
comment|// warm the pool by creating a connection during startup
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
literal|"Create pooled connection during start failed. This exception will be ignored."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopping the PooledConnectionFactory, number of connections in cache: {}"
argument_list|,
name|connectionsPool
operator|.
name|getNumActive
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopped
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|connectionsPool
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
block|{             }
block|}
block|}
comment|/**      * Clears all connections from the pool.  Each connection that is currently in the pool is      * closed and removed from the pool.  A new connection will be created on the next call to      * {@link createConnection}.  Care should be taken when using this method as Connections that      * are in use be client's will be closed.      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|connectionsPool
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the currently configured maximum number of sessions a pooled Connection will      * create before it either blocks or throws an exception when a new session is requested,      * depending on configuration.      *      * @return the number of session instances that can be taken from a pooled connection.      */
specifier|public
name|int
name|getMaximumActiveSessionPerConnection
parameter_list|()
block|{
return|return
name|maximumActiveSessionPerConnection
return|;
block|}
comment|/**      * Sets the maximum number of active sessions per connection      *      * @param maximumActiveSessionPerConnection      *      The maximum number of active session per connection in the pool.      */
specifier|public
name|void
name|setMaximumActiveSessionPerConnection
parameter_list|(
name|int
name|maximumActiveSessionPerConnection
parameter_list|)
block|{
name|this
operator|.
name|maximumActiveSessionPerConnection
operator|=
name|maximumActiveSessionPerConnection
expr_stmt|;
block|}
comment|/**      * Controls the behavior of the internal session pool. By default the call to      * Connection.getSession() will block if the session pool is full.  If the      * argument false is given, it will change the default behavior and instead the      * call to getSession() will throw a JMSException.      *      * The size of the session pool is controlled by the @see #maximumActive      * property.      *      * @param block - if true, the call to getSession() blocks if the pool is full      * until a session object is available.  defaults to true.      */
specifier|public
name|void
name|setBlockIfSessionPoolIsFull
parameter_list|(
name|boolean
name|block
parameter_list|)
block|{
name|this
operator|.
name|blockIfSessionPoolIsFull
operator|=
name|block
expr_stmt|;
block|}
comment|/**      * Returns whether a pooled Connection will enter a blocked state or will throw an Exception      * once the maximum number of sessions has been borrowed from the the Session Pool.      *      * @return true if the pooled Connection createSession method will block when the limit is hit.      * @see setBlockIfSessionPoolIsFull      */
specifier|public
name|boolean
name|isBlockIfSessionPoolIsFull
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockIfSessionPoolIsFull
return|;
block|}
comment|/**      * Returns the maximum number to pooled Connections that this factory will allow before it      * begins to return connections from the pool on calls to ({@link createConnection}.      *      * @return the maxConnections that will be created for this pool.      */
specifier|public
name|int
name|getMaxConnections
parameter_list|()
block|{
return|return
name|connectionsPool
operator|.
name|getMaxIdle
argument_list|()
return|;
block|}
comment|/**      * Sets the maximum number of pooled Connections (defaults to one).  Each call to      * {@link createConnection} will result in a new Connection being create up to the max      * connections value.      *      * @param maxConnections the maxConnections to set      */
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
name|connectionsPool
operator|.
name|setMaxIdle
argument_list|(
name|maxConnections
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the Idle timeout value applied to new Connection's that are created by this pool.      *<p/>      * The idle timeout is used determine if a Connection instance has sat to long in the pool unused      * and if so is closed and removed from the pool.  The default value is 30 seconds.      *      * @return idle timeout value (milliseconds)      */
specifier|public
name|int
name|getIdleTimeout
parameter_list|()
block|{
return|return
name|idleTimeout
return|;
block|}
comment|/**      * Sets the idle timeout  value for Connection's that are created by this pool in Milliseconds,      * defaults to 30 seconds.      *<p/>      * For a Connection that is in the pool but has no current users the idle timeout determines how      * long the Connection can live before it is eligible for removal from the pool.  Normally the      * connections are tested when an attempt to check one out occurs so a Connection instance can sit      * in the pool much longer than its idle timeout if connections are used infrequently.      *      * @param idleTimeout      *      The maximum time a pooled Connection can sit unused before it is eligible for removal.      */
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
comment|/**      * allow connections to expire, irrespective of load or idle time. This is useful with failover      * to force a reconnect from the pool, to reestablish load balancing or use of the master post recovery      *      * @param expiryTimeout non zero in milliseconds      */
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
comment|/**      * @return the configured expiration timeout for connections in the pool.      */
specifier|public
name|long
name|getExpiryTimeout
parameter_list|()
block|{
return|return
name|expiryTimeout
return|;
block|}
comment|/**      * @return true if a Connection is created immediately on a call to {@link start}.      */
specifier|public
name|boolean
name|isCreateConnectionOnStartup
parameter_list|()
block|{
return|return
name|createConnectionOnStartup
return|;
block|}
comment|/**      * Whether to create a connection on starting this {@link PooledConnectionFactory}.      *<p/>      * This can be used to warm-up the pool on startup. Notice that any kind of exception      * happens during startup is logged at WARN level and ignored.      *      * @param createConnectionOnStartup<tt>true</tt> to create a connection on startup      */
specifier|public
name|void
name|setCreateConnectionOnStartup
parameter_list|(
name|boolean
name|createConnectionOnStartup
parameter_list|)
block|{
name|this
operator|.
name|createConnectionOnStartup
operator|=
name|createConnectionOnStartup
expr_stmt|;
block|}
comment|/**      * Gets the Pool of ConnectionPool instances which are keyed by different ConnectionKeys.      *      * @return this factories pool of ConnectionPool instances.      */
name|KeyedObjectPool
argument_list|<
name|ConnectionKey
argument_list|,
name|ConnectionPool
argument_list|>
name|getConnectionsPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|connectionsPool
return|;
block|}
comment|/**      * Sets the number of milliseconds to sleep between runs of the idle Connection eviction thread.      * When non-positive, no idle object eviction thread will be run, and Connections will only be      * checked on borrow to determine if they have sat idle for too long or have failed for some      * other reason.      *<p/>      * By default this value is set to -1 and no expiration thread ever runs.      *      * @param timeBetweenExpirationCheckMillis      *      The time to wait between runs of the idle Connection eviction thread.      */
specifier|public
name|void
name|setTimeBetweenExpirationCheckMillis
parameter_list|(
name|long
name|timeBetweenExpirationCheckMillis
parameter_list|)
block|{
name|this
operator|.
name|connectionsPool
operator|.
name|setTimeBetweenEvictionRunsMillis
argument_list|(
name|timeBetweenExpirationCheckMillis
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the number of milliseconds to sleep between runs of the idle connection eviction thread.      */
specifier|public
name|long
name|setTimeBetweenExpirationCheckMillis
parameter_list|()
block|{
return|return
name|this
operator|.
name|connectionsPool
operator|.
name|getTimeBetweenEvictionRunsMillis
argument_list|()
return|;
block|}
comment|/**      * @return the number of Connections currently in the Pool      */
specifier|public
name|int
name|getNumConnections
parameter_list|()
block|{
return|return
name|this
operator|.
name|connectionsPool
operator|.
name|getNumIdle
argument_list|()
return|;
block|}
comment|/**      * Delegate that creates each instance of an ConnectionPool object.  Subclasses can override      * this method to customize the type of connection pool returned.      *      * @param connection      *      * @return instance of a new ConnectionPool.      */
specifier|protected
name|ConnectionPool
name|createConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|)
block|{
return|return
operator|new
name|ConnectionPool
argument_list|(
name|connection
argument_list|)
return|;
block|}
block|}
end_class

end_unit

