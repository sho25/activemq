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
package|;
end_package

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
name|CopyOnWriteArraySet
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
name|ThreadPriorities
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
comment|/**  * Used to provide information on the status of the Connection  *   *   */
end_comment

begin_class
specifier|public
class|class
name|TransportStatusDetector
implements|implements
name|Service
implements|,
name|Runnable
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
name|TransportStatusDetector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TransportConnector
name|connector
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|TransportConnection
argument_list|>
name|collectionCandidates
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<
name|TransportConnection
argument_list|>
argument_list|()
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
specifier|private
name|Thread
name|runner
decl_stmt|;
specifier|private
name|int
name|sweepInterval
init|=
literal|5000
decl_stmt|;
name|TransportStatusDetector
parameter_list|(
name|TransportConnector
name|connector
parameter_list|)
block|{
name|this
operator|.
name|connector
operator|=
name|connector
expr_stmt|;
block|}
comment|/**      * @return Returns the sweepInterval.      */
specifier|public
name|int
name|getSweepInterval
parameter_list|()
block|{
return|return
name|sweepInterval
return|;
block|}
comment|/**      * The sweepInterval to set.      *       * @param sweepInterval      */
specifier|public
name|void
name|setSweepInterval
parameter_list|(
name|int
name|sweepInterval
parameter_list|)
block|{
name|this
operator|.
name|sweepInterval
operator|=
name|sweepInterval
expr_stmt|;
block|}
specifier|protected
name|void
name|doCollection
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|TransportConnection
argument_list|>
name|i
init|=
name|collectionCandidates
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
name|TransportConnection
name|tc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|tc
operator|.
name|isMarkedCandidate
argument_list|()
condition|)
block|{
if|if
condition|(
name|tc
operator|.
name|isBlockedCandidate
argument_list|()
condition|)
block|{
name|collectionCandidates
operator|.
name|remove
argument_list|(
name|tc
argument_list|)
expr_stmt|;
name|doCollection
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tc
operator|.
name|doMark
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|collectionCandidates
operator|.
name|remove
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|doSweep
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|connector
operator|.
name|getConnections
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
name|TransportConnection
name|connection
init|=
operator|(
name|TransportConnection
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|.
name|isMarkedCandidate
argument_list|()
condition|)
block|{
name|connection
operator|.
name|doMark
argument_list|()
expr_stmt|;
name|collectionCandidates
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|doCollection
parameter_list|(
name|TransportConnection
name|tc
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"found a blocked client - stopping: {}"
argument_list|,
name|tc
argument_list|)
expr_stmt|;
try|try
block|{
name|tc
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
name|error
argument_list|(
literal|"Error stopping {}"
argument_list|,
name|tc
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|doCollection
argument_list|()
expr_stmt|;
name|doSweep
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sweepInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to complete a sweep for blocked clients"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|runner
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"ActiveMQ Transport Status Monitor: "
operator|+
name|connector
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
name|setPriority
argument_list|(
name|ThreadPriorities
operator|.
name|BROKER_MANAGEMENT
argument_list|)
expr_stmt|;
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|started
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|runner
operator|!=
literal|null
condition|)
block|{
name|runner
operator|.
name|join
argument_list|(
name|getSweepInterval
argument_list|()
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

