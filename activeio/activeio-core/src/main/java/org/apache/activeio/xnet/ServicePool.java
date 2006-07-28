begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|xnet
package|;
end_package

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
name|Executor
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
name|LinkedBlockingQueue
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
name|ThreadPoolExecutor
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
name|ThreadFactory
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
name|TimeUnit
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
name|Socket
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

begin_class
specifier|public
class|class
name|ServicePool
implements|implements
name|ServerService
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
name|ServicePool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ServerService
name|next
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|public
name|ServicePool
parameter_list|(
name|ServerService
name|next
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|threads
parameter_list|,
specifier|final
name|long
name|keepAliveTime
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|ThreadPoolExecutor
name|p
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|threads
argument_list|,
name|threads
argument_list|,
name|keepAliveTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|setThreadFactory
argument_list|(
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|volatile
name|int
name|id
init|=
literal|0
decl_stmt|;
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|arg0
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|arg0
argument_list|,
name|name
operator|+
literal|" "
operator|+
name|getNextID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|thread
return|;
block|}
specifier|private
name|int
name|getNextID
parameter_list|()
block|{
return|return
name|id
operator|++
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|=
name|p
expr_stmt|;
block|}
specifier|public
name|ServicePool
parameter_list|(
name|ServerService
name|next
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
specifier|public
name|void
name|service
parameter_list|(
specifier|final
name|Socket
name|socket
parameter_list|)
throws|throws
name|ServiceException
throws|,
name|IOException
block|{
specifier|final
name|Runnable
name|service
init|=
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
name|next
operator|.
name|service
argument_list|(
name|socket
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Security error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|socket
operator|!=
literal|null
condition|)
block|{
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error while closing connection with client"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|ClassLoader
name|tccl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Runnable
name|ctxCL
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|tccl
argument_list|)
expr_stmt|;
try|try
block|{
name|service
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|ctxCL
argument_list|)
expr_stmt|;
block|}
comment|/**      * Pulls out the access log information      *      * @param props      * @throws ServiceException      */
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do our stuff
comment|// Then call the next guy
name|next
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|ServiceException
block|{
comment|// Do our stuff
comment|// Then call the next guy
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|ServiceException
block|{
comment|// Do our stuff
comment|// Then call the next guy
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Gets the name of the service.      * Used for display purposes only      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**      * Gets the ip number that the      * daemon is listening on.      */
specifier|public
name|String
name|getIP
parameter_list|()
block|{
return|return
name|next
operator|.
name|getIP
argument_list|()
return|;
block|}
comment|/**      * Gets the port number that the      * daemon is listening on.      */
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|next
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
end_class

end_unit

