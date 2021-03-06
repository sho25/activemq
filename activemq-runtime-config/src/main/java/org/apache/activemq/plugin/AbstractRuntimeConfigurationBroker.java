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
name|plugin
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
name|ConcurrentLinkedQueue
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|broker
operator|.
name|Broker
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
name|BrokerFilter
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
name|ConnectionContext
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
name|region
operator|.
name|Destination
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
name|command
operator|.
name|ConnectionInfo
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

begin_class
specifier|public
class|class
name|AbstractRuntimeConfigurationBroker
extends|extends
name|BrokerFilter
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractRuntimeConfigurationBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|ReentrantReadWriteLock
name|addDestinationBarrier
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|ReentrantReadWriteLock
name|addConnectionBarrier
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|protected
name|Runnable
name|monitorTask
decl_stmt|;
specifier|protected
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
name|addDestinationWork
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
name|addConnectionWork
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ObjectName
name|objectName
decl_stmt|;
specifier|protected
name|String
name|infoString
decl_stmt|;
specifier|public
name|AbstractRuntimeConfigurationBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
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
name|super
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
if|if
condition|(
name|monitorTask
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|cancel
argument_list|(
name|monitorTask
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|letsNotStopStop
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cancel config monitor task"
argument_list|,
name|letsNotStopStop
argument_list|)
expr_stmt|;
block|}
block|}
name|unregisterMbean
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|registerMbean
parameter_list|()
block|{      }
specifier|protected
name|void
name|unregisterMbean
parameter_list|()
block|{      }
comment|// modification to virtual destinations interceptor needs exclusive access to destination add
annotation|@
name|Override
specifier|public
name|Destination
name|addDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|boolean
name|createIfTemporary
parameter_list|)
throws|throws
name|Exception
block|{
name|Runnable
name|work
init|=
name|addDestinationWork
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|work
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|addDestinationBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
do|do
block|{
name|work
operator|.
name|run
argument_list|()
expr_stmt|;
name|work
operator|=
name|addDestinationWork
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|work
operator|!=
literal|null
condition|)
do|;
return|return
name|super
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|createIfTemporary
argument_list|)
return|;
block|}
finally|finally
block|{
name|addDestinationBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|addDestinationBarrier
operator|.
name|readLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|createIfTemporary
argument_list|)
return|;
block|}
finally|finally
block|{
name|addDestinationBarrier
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// modification to authentication plugin needs exclusive access to connection add
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|Runnable
name|work
init|=
name|addConnectionWork
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|work
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|addConnectionBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
do|do
block|{
name|work
operator|.
name|run
argument_list|()
expr_stmt|;
name|work
operator|=
name|addConnectionWork
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|work
operator|!=
literal|null
condition|)
do|;
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|addConnectionBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|addConnectionBarrier
operator|.
name|readLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|addConnectionBarrier
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Apply the destination work immediately instead of waiting for      * a connection add or destination add      *      * @throws Exception      */
specifier|protected
name|void
name|applyDestinationWork
parameter_list|()
throws|throws
name|Exception
block|{
name|Runnable
name|work
init|=
name|addDestinationWork
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|work
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|addDestinationBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
do|do
block|{
name|work
operator|.
name|run
argument_list|()
expr_stmt|;
name|work
operator|=
name|addDestinationWork
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|work
operator|!=
literal|null
condition|)
do|;
block|}
finally|finally
block|{
name|addDestinationBarrier
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|filterPasswords
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoString
operator|!=
literal|null
condition|)
block|{
name|infoString
operator|+=
name|s
expr_stmt|;
name|infoString
operator|+=
literal|";"
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|info
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|filterPasswords
argument_list|(
name|s
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoString
operator|!=
literal|null
condition|)
block|{
name|infoString
operator|+=
name|s
expr_stmt|;
name|infoString
operator|+=
literal|", "
operator|+
name|t
expr_stmt|;
name|infoString
operator|+=
literal|";"
expr_stmt|;
block|}
block|}
name|Pattern
name|matchPassword
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"password=.*,"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|filterPasswords
parameter_list|(
name|Object
name|toEscape
parameter_list|)
block|{
return|return
name|matchPassword
operator|.
name|matcher
argument_list|(
name|toEscape
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"password=???,"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

