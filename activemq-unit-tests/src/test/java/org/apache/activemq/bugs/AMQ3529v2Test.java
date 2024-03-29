begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|ExecutorService
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
name|Executors
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
name|TimeUnit
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
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ3529v2Test
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ3529v2Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testRandomInterruptionAffects
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRandomInterruptionAffects
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testRandomInterruptionAffectsWithFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|connectionUri
operator|=
literal|"failover:("
operator|+
name|connectionUri
operator|+
literal|")"
expr_stmt|;
name|doTestRandomInterruptionAffects
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doTestRandomInterruptionAffects
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|ThreadGroup
name|tg
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"tg"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tg
operator|.
name|activeCount
argument_list|()
argument_list|)
expr_stmt|;
class|class
name|ClientThread
extends|extends
name|Thread
block|{
specifier|public
name|Exception
name|error
decl_stmt|;
specifier|public
name|ClientThread
parameter_list|(
name|ThreadGroup
name|tg
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|tg
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Context
name|ctx
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"org.apache.activemq.jndi.ActiveMQInitialContextFactory"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|connectionUri
argument_list|)
expr_stmt|;
name|ctx
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|ctx
operator|=
operator|new
name|InitialContext
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Destination
name|destination
init|=
operator|(
name|Destination
operator|)
name|ctx
operator|.
name|lookup
argument_list|(
literal|"dynamicTopics/example.C"
argument_list|)
decl_stmt|;
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expect an exception here from the interrupt.
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|trackException
argument_list|(
literal|"Consumer Close failed with"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|trackException
argument_list|(
literal|"Session Close failed with"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|trackException
argument_list|(
literal|"Connection Close failed with"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|trackException
argument_list|(
literal|"Connection Close failed with"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|trackException
parameter_list|(
name|String
name|s
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|e
expr_stmt|;
block|}
block|}
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ClientThread
argument_list|>
name|threads
init|=
operator|new
name|LinkedList
argument_list|<
name|ClientThread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|ClientThread
argument_list|(
name|tg
argument_list|,
literal|"Client-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// interrupt the threads at some random time
name|ExecutorService
name|doTheInterrupts
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|threads
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|doTheInterrupts
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{
name|ignored
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|doTheInterrupts
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"all interrupts done"
argument_list|,
name|doTheInterrupts
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ClientThread
name|thread
range|:
name|threads
control|)
block|{
if|if
condition|(
name|thread
operator|.
name|error
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Close error on thread: "
operator|+
name|thread
argument_list|,
name|thread
operator|.
name|error
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
index|[]
name|remainThreads
init|=
operator|new
name|Thread
index|[
name|tg
operator|.
name|activeCount
argument_list|()
index|]
decl_stmt|;
name|tg
operator|.
name|enumerate
argument_list|(
name|remainThreads
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Thread
name|t
range|:
name|remainThreads
control|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|.
name|isAlive
argument_list|()
operator|&&
operator|!
name|t
operator|.
name|isDaemon
argument_list|()
condition|)
name|assertTrue
argument_list|(
literal|"Thread completes:"
operator|+
name|t
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Remaining thread: "
operator|+
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|!
name|t
operator|.
name|isAlive
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ThreadGroup
name|root
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|root
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|root
operator|=
name|root
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|visit
argument_list|(
name|root
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// This method recursively visits all thread groups under `group'.
specifier|public
specifier|static
name|void
name|visit
parameter_list|(
name|ThreadGroup
name|group
parameter_list|,
name|int
name|level
parameter_list|)
block|{
comment|// Get threads in `group'
name|int
name|numThreads
init|=
name|group
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
operator|*
literal|2
index|]
decl_stmt|;
name|numThreads
operator|=
name|group
operator|.
name|enumerate
argument_list|(
name|threads
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Enumerate each thread in `group'
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
comment|// Get thread
name|Thread
name|thread
init|=
name|threads
index|[
name|i
index|]
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Thread:"
operator|+
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|" is still running"
argument_list|)
expr_stmt|;
block|}
comment|// Get thread subgroups of `group'
name|int
name|numGroups
init|=
name|group
operator|.
name|activeGroupCount
argument_list|()
decl_stmt|;
name|ThreadGroup
index|[]
name|groups
init|=
operator|new
name|ThreadGroup
index|[
name|numGroups
operator|*
literal|2
index|]
decl_stmt|;
name|numGroups
operator|=
name|group
operator|.
name|enumerate
argument_list|(
name|groups
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Recursively visit each subgroup
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numGroups
condition|;
name|i
operator|++
control|)
block|{
name|visit
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|,
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

