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
name|broker
operator|.
name|region
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|thread
operator|.
name|TaskRunnerFactory
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
name|usage
operator|.
name|SystemUsage
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
comment|/**  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTempRegion
extends|extends
name|AbstractRegion
block|{
specifier|private
specifier|static
name|int
name|TIME_BEFORE_PURGE
init|=
literal|60000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TempQueueRegion
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|CachedDestination
argument_list|,
name|Destination
argument_list|>
name|cachedDestinations
init|=
operator|new
name|HashMap
argument_list|<
name|CachedDestination
argument_list|,
name|Destination
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Timer
name|purgeTimer
decl_stmt|;
specifier|private
specifier|final
name|TimerTask
name|purgeTask
decl_stmt|;
comment|/**      * @param broker      * @param destinationStatistics      * @param memoryManager      * @param taskRunnerFactory      * @param destinationFactory      */
specifier|public
name|AbstractTempRegion
parameter_list|(
name|RegionBroker
name|broker
parameter_list|,
name|DestinationStatistics
name|destinationStatistics
parameter_list|,
name|SystemUsage
name|memoryManager
parameter_list|,
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|,
name|DestinationFactory
name|destinationFactory
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|destinationStatistics
argument_list|,
name|memoryManager
argument_list|,
name|taskRunnerFactory
argument_list|,
name|destinationFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|purgeTimer
operator|=
operator|new
name|Timer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|purgeTask
operator|=
operator|new
name|TimerTask
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|doPurge
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|purgeTimer
operator|.
name|schedule
argument_list|(
name|purgeTask
argument_list|,
name|TIME_BEFORE_PURGE
argument_list|,
name|TIME_BEFORE_PURGE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|purgeTimer
operator|!=
literal|null
condition|)
block|{
name|purgeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|Destination
name|doCreateDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|synchronized
name|Destination
name|createDestination
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Destination
name|result
init|=
name|cachedDestinations
operator|.
name|remove
argument_list|(
operator|new
name|CachedDestination
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|doCreateDestination
argument_list|(
name|context
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|final
specifier|synchronized
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
comment|//add to cache
name|cachedDestinations
operator|.
name|put
argument_list|(
operator|new
name|CachedDestination
argument_list|(
name|dest
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doDispose
parameter_list|(
name|Destination
name|dest
parameter_list|)
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|dest
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|dest
operator|.
name|stop
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
literal|"Failed to dispose of "
operator|+
name|dest
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|doPurge
parameter_list|()
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
name|cachedDestinations
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Set
argument_list|<
name|CachedDestination
argument_list|>
name|tmp
init|=
operator|new
name|HashSet
argument_list|<
name|CachedDestination
argument_list|>
argument_list|(
name|cachedDestinations
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CachedDestination
name|key
range|:
name|tmp
control|)
block|{
if|if
condition|(
operator|(
name|key
operator|.
name|timeStamp
operator|+
name|TIME_BEFORE_PURGE
operator|)
operator|<
name|currentTime
condition|)
block|{
name|Destination
name|dest
init|=
name|cachedDestinations
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
name|doDispose
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|static
class|class
name|CachedDestination
block|{
name|long
name|timeStamp
decl_stmt|;
name|ActiveMQDestination
name|destination
decl_stmt|;
name|CachedDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|timeStamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|destination
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|CachedDestination
condition|)
block|{
name|CachedDestination
name|other
init|=
operator|(
name|CachedDestination
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|destination
operator|.
name|equals
argument_list|(
name|this
operator|.
name|destination
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

