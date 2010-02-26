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
name|nio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|CancelledKeyException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SelectionKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SocketChannel
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
name|transport
operator|.
name|nio
operator|.
name|SelectorManager
operator|.
name|Listener
import|;
end_import

begin_comment
comment|/**  * @author chirino  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SelectorSelection
block|{
specifier|private
specifier|final
name|SelectorWorker
name|worker
decl_stmt|;
specifier|private
specifier|final
name|Listener
name|listener
decl_stmt|;
specifier|private
name|int
name|interest
decl_stmt|;
specifier|private
name|SelectionKey
name|key
decl_stmt|;
specifier|private
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|public
name|SelectorSelection
parameter_list|(
specifier|final
name|SelectorWorker
name|worker
parameter_list|,
specifier|final
name|SocketChannel
name|socketChannel
parameter_list|,
name|Listener
name|listener
parameter_list|)
throws|throws
name|ClosedChannelException
block|{
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|worker
operator|.
name|addIoTask
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|SelectorSelection
operator|.
name|this
operator|.
name|key
operator|=
name|socketChannel
operator|.
name|register
argument_list|(
name|worker
operator|.
name|selector
argument_list|,
literal|0
argument_list|,
name|SelectorSelection
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInterestOps
parameter_list|(
name|int
name|ops
parameter_list|)
block|{
name|interest
operator|=
name|ops
expr_stmt|;
block|}
specifier|public
name|void
name|enable
parameter_list|()
block|{
name|worker
operator|.
name|addIoTask
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|key
operator|.
name|interestOps
argument_list|(
name|interest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancelledKeyException
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|disable
parameter_list|()
block|{
name|worker
operator|.
name|addIoTask
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|key
operator|.
name|interestOps
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancelledKeyException
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// guard against multiple closes.
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|worker
operator|.
name|addIoTask
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|key
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancelledKeyException
name|e
parameter_list|)
block|{                     }
name|worker
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onSelect
parameter_list|()
block|{
name|listener
operator|.
name|onSelect
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onError
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onError
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

