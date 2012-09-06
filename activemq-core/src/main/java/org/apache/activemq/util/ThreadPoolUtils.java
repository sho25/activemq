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
name|util
package|;
end_package

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
name|TimeUnit
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
comment|/**  * Utility methods for working with thread pools {@link ExecutorService}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ThreadPoolUtils
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
name|ThreadPoolUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_SHUTDOWN_AWAIT_TERMINATION
init|=
literal|30
operator|*
literal|1000L
decl_stmt|;
comment|/**      * Shutdown the given executor service only (ie not graceful shutdown).      *      * @see java.util.concurrent.ExecutorService#shutdown()      */
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|doShutdown
argument_list|(
name|executorService
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Shutdown now the given executor service aggressively.      *      * @param executorService the executor service to shutdown now      * @return list of tasks that never commenced execution      * @see java.util.concurrent.ExecutorService#shutdownNow()      */
specifier|public
specifier|static
name|List
argument_list|<
name|Runnable
argument_list|>
name|shutdownNow
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|List
argument_list|<
name|Runnable
argument_list|>
name|answer
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|executorService
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Forcing shutdown of ExecutorService: {}"
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|answer
operator|=
name|executorService
operator|.
name|shutdownNow
argument_list|()
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
literal|"Shutdown of ExecutorService: {} is shutdown: {} and terminated: {}."
argument_list|,
operator|new
name|Object
index|[]
block|{
name|executorService
block|,
name|executorService
operator|.
name|isShutdown
argument_list|()
block|,
name|executorService
operator|.
name|isTerminated
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Shutdown the given executor service graceful at first, and then aggressively      * if the await termination timeout was hit.      *<p/>      * This implementation invokes the {@link #shutdownGraceful(java.util.concurrent.ExecutorService, long)}      * with a timeout value of {@link #DEFAULT_SHUTDOWN_AWAIT_TERMINATION} millis.      */
specifier|public
specifier|static
name|void
name|shutdownGraceful
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|doShutdown
argument_list|(
name|executorService
argument_list|,
name|DEFAULT_SHUTDOWN_AWAIT_TERMINATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Shutdown the given executor service graceful at first, and then aggressively      * if the await termination timeout was hit.      *<p/>      * Will try to perform an orderly shutdown by giving the running threads      * time to complete tasks, before going more aggressively by doing a      * {@link #shutdownNow(java.util.concurrent.ExecutorService)} which      * forces a shutdown. The parameter<tt>shutdownAwaitTermination</tt>      * is used as timeout value waiting for orderly shutdown to      * complete normally, before going aggressively.      *<p/>      * Notice if the given parameter<tt>shutdownAwaitTermination</tt> is negative, then a quick shutdown      * is commenced, by invoking the {@link java.util.concurrent.ExecutorService#shutdown()} method      * and then exit from this method (ie. no graceful shutdown is performed).      *      * @param executorService the executor service to shutdown      * @param shutdownAwaitTermination timeout in millis to wait for orderly shutdown, if the value if negative      *                                 then the thread pool is<b>not</b> graceful shutdown, but a regular shutdown      *                                 is commenced.      */
specifier|public
specifier|static
name|void
name|shutdownGraceful
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|,
name|long
name|shutdownAwaitTermination
parameter_list|)
block|{
name|doShutdown
argument_list|(
name|executorService
argument_list|,
name|shutdownAwaitTermination
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|doShutdown
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|,
name|long
name|shutdownAwaitTermination
parameter_list|,
name|boolean
name|quick
parameter_list|)
block|{
comment|// code from Apache Camel - org.apache.camel.impl.DefaultExecutorServiceManager
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|quick
condition|)
block|{
comment|// do not shutdown graceful, but just quick shutdown on the thread pool
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Quick shutdown of ExecutorService: {} is shutdown: {} and terminated: {}."
argument_list|,
operator|new
name|Object
index|[]
block|{
name|executorService
block|,
name|executorService
operator|.
name|isShutdown
argument_list|()
block|,
name|executorService
operator|.
name|isTerminated
argument_list|()
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|shutdownAwaitTermination
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ShutdownAwaitTermination must be a positive number, was: "
operator|+
name|shutdownAwaitTermination
argument_list|)
throw|;
block|}
comment|// shutting down a thread pool is a 2 step process. First we try graceful, and if that fails, then we go more aggressively
comment|// and try shutting down again. In both cases we wait at most the given shutdown timeout value given
comment|// (total wait could then be 2 x shutdownAwaitTermination)
name|boolean
name|warned
init|=
literal|false
decl_stmt|;
name|StopWatch
name|watch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|executorService
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Shutdown of ExecutorService: {} with await termination: {} millis"
argument_list|,
name|executorService
argument_list|,
name|shutdownAwaitTermination
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|awaitTermination
argument_list|(
name|executorService
argument_list|,
name|shutdownAwaitTermination
argument_list|)
condition|)
block|{
name|warned
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Forcing shutdown of ExecutorService: {} due first await termination elapsed."
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// we are now shutting down aggressively, so wait to see if we can completely shutdown or not
if|if
condition|(
operator|!
name|awaitTermination
argument_list|(
name|executorService
argument_list|,
name|shutdownAwaitTermination
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot completely force shutdown of ExecutorService: {} due second await termination elapsed."
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|warned
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Forcing shutdown of ExecutorService: {} due interrupted."
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
comment|// we were interrupted during shutdown, so force shutdown
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
comment|// if we logged at WARN level, then report at INFO level when we are complete so the end user can see this in the log
if|if
condition|(
name|warned
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}."
argument_list|,
operator|new
name|Object
index|[]
block|{
name|executorService
block|,
name|executorService
operator|.
name|isShutdown
argument_list|()
block|,
name|executorService
operator|.
name|isTerminated
argument_list|()
block|,
name|TimeUtils
operator|.
name|printDuration
argument_list|(
name|watch
operator|.
name|taken
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
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
literal|"Shutdown of ExecutorService: {} is shutdown: {} and terminated: {} took: {}."
argument_list|,
operator|new
name|Object
index|[]
block|{
name|executorService
block|,
name|executorService
operator|.
name|isShutdown
argument_list|()
block|,
name|executorService
operator|.
name|isTerminated
argument_list|()
block|,
name|TimeUtils
operator|.
name|printDuration
argument_list|(
name|watch
operator|.
name|taken
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Awaits the termination of the thread pool.      *<p/>      * This implementation will log every 5th second at INFO level that we are waiting, so the end user      * can see we are not hanging in case it takes longer time to shutdown the pool.      *      * @param executorService            the thread pool      * @param shutdownAwaitTermination   time in millis to use as timeout      * @return<tt>true</tt> if the pool is terminated, or<tt>false</tt> if we timed out      * @throws InterruptedException is thrown if we are interrupted during the waiting      */
specifier|public
specifier|static
name|boolean
name|awaitTermination
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|,
name|long
name|shutdownAwaitTermination
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// log progress every 5th second so end user is aware of we are shutting down
name|StopWatch
name|watch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
name|long
name|interval
init|=
name|Math
operator|.
name|min
argument_list|(
literal|5000
argument_list|,
name|shutdownAwaitTermination
argument_list|)
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
operator|&&
name|interval
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|interval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waited {} for ExecutorService: {} to shutdown..."
argument_list|,
name|TimeUtils
operator|.
name|printDuration
argument_list|(
name|watch
operator|.
name|taken
argument_list|()
argument_list|)
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
comment|// recalculate interval
name|interval
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|5000
argument_list|,
name|shutdownAwaitTermination
operator|-
name|watch
operator|.
name|taken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|done
return|;
block|}
block|}
end_class

end_unit

