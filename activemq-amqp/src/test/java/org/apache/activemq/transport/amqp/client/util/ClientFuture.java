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
name|amqp
operator|.
name|client
operator|.
name|util
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
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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

begin_comment
comment|/**  * Asynchronous Client Future class.  */
end_comment

begin_class
specifier|public
class|class
name|ClientFuture
implements|implements
name|AsyncResult
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|completer
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ClientFutureSynchronization
name|synchronization
decl_stmt|;
specifier|private
specifier|volatile
name|Throwable
name|error
decl_stmt|;
specifier|public
name|ClientFuture
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ClientFuture
parameter_list|(
name|ClientFutureSynchronization
name|synchronization
parameter_list|)
block|{
name|this
operator|.
name|synchronization
operator|=
name|synchronization
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|latch
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|result
parameter_list|)
block|{
if|if
condition|(
name|completer
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|error
operator|=
name|result
expr_stmt|;
if|if
condition|(
name|synchronization
operator|!=
literal|null
condition|)
block|{
name|synchronization
operator|.
name|onPendingFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|()
block|{
if|if
condition|(
name|completer
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
name|synchronization
operator|!=
literal|null
condition|)
block|{
name|synchronization
operator|.
name|onPendingSuccess
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Timed wait for a response to a pending operation.      *      * @param amount      *        The amount of time to wait before abandoning the wait.      * @param unit      *        The unit to use for this wait period.      *      * @throws IOException if an error occurs while waiting for the response.      */
specifier|public
name|void
name|sync
parameter_list|(
name|long
name|amount
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|(
name|amount
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|failOnError
argument_list|()
expr_stmt|;
block|}
comment|/**      * Waits for a response to some pending operation.      *      * @throws IOException if an error occurs while waiting for the response.      */
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|failOnError
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|failOnError
parameter_list|()
throws|throws
name|IOException
block|{
name|Throwable
name|cause
init|=
name|error
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|cause
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

