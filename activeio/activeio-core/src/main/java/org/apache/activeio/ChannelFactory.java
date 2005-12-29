begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
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
name|net
operator|.
name|URI
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|AsyncToSyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|SyncToAsyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|AsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|util
operator|.
name|FactoryFinder
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
name|SynchronousQueue
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A {@see ChannelFactory}uses the requested URI's scheme to determine the  * actual {@see org.apache.activeio.SynchChannelFactory}or  * {@see org.apache.activeio.AsyncChannelFactory}implementation to use to create it's  * {@see org.apache.activeio.Channel}s and {@see org.apache.activeio.ChannelServer}s.  *   * Each URI scheme that {@see ChannelFactory}object handles will have a  * properties file located at: "META-INF/services/org/apache/activeio/channel/{scheme}".  *   */
end_comment

begin_class
specifier|public
class|class
name|ChannelFactory
implements|implements
name|SyncChannelFactory
implements|,
name|AsyncChannelFactory
block|{
specifier|private
specifier|final
name|HashMap
name|syncChannelFactoryMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|HashMap
name|asyncChannelFactoryMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|static
specifier|public
specifier|final
name|Executor
name|DEFAULT_EXECUTOR
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|10
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|DEFAULT_EXECUTOR
operator|)
operator|.
name|setThreadFactory
argument_list|(
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|run
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|run
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|FactoryFinder
name|finder
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activeio/channel/"
argument_list|)
decl_stmt|;
specifier|public
name|SyncChannel
name|openSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|SyncChannelFactory
name|factory
init|=
name|getSynchChannelFactory
argument_list|(
name|location
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|openSyncChannel
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|public
name|SyncChannelServer
name|bindSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|SyncChannelFactory
name|factory
init|=
name|getSynchChannelFactory
argument_list|(
name|location
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|bindSyncChannel
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|public
name|AsyncChannel
name|openAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|AsyncChannelFactory
name|factory
init|=
name|getAsyncChannelFactory
argument_list|(
name|location
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|openAsyncChannel
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|public
name|AsyncChannelServer
name|bindAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|AsyncChannelFactory
name|factory
init|=
name|getAsyncChannelFactory
argument_list|(
name|location
operator|.
name|getScheme
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|factory
operator|.
name|bindAsyncChannel
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|private
name|SyncChannelFactory
name|getSynchChannelFactory
parameter_list|(
name|String
name|protocol
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|SyncChannelFactory
name|rc
init|=
operator|(
name|SyncChannelFactory
operator|)
name|syncChannelFactoryMap
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|rc
operator|=
operator|(
name|SyncChannelFactory
operator|)
name|finder
operator|.
name|newInstance
argument_list|(
name|protocol
argument_list|,
literal|"SyncChannelFactory."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|original
parameter_list|)
block|{
comment|// try to recovery by using AsyncChannelFactory and adapt
comment|// it to be sync.
try|try
block|{
name|AsyncChannelFactory
name|f
init|=
operator|(
name|AsyncChannelFactory
operator|)
name|finder
operator|.
name|newInstance
argument_list|(
name|protocol
argument_list|,
literal|"AsyncChannelFactory."
argument_list|)
decl_stmt|;
name|rc
operator|=
name|AsyncToSyncChannelFactory
operator|.
name|adapt
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Recovery strategy failed.. throw original exception.
throw|throw
name|original
throw|;
block|}
block|}
name|syncChannelFactoryMap
operator|.
name|put
argument_list|(
name|protocol
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Could not load a SyncChannelFactory for protcol: "
operator|+
name|protocol
operator|+
literal|", reason: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|AsyncChannelFactory
name|getAsyncChannelFactory
parameter_list|(
name|String
name|protocol
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|AsyncChannelFactory
name|rc
init|=
operator|(
name|AsyncChannelFactory
operator|)
name|asyncChannelFactoryMap
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|rc
operator|=
operator|(
name|AsyncChannelFactory
operator|)
name|finder
operator|.
name|newInstance
argument_list|(
name|protocol
argument_list|,
literal|"AsyncChannelFactory."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|original
parameter_list|)
block|{
comment|// try to recovery by using SynchChannelFactory and adapt it
comment|// to be async.
try|try
block|{
name|SyncChannelFactory
name|f
init|=
operator|(
name|SyncChannelFactory
operator|)
name|finder
operator|.
name|newInstance
argument_list|(
name|protocol
argument_list|,
literal|"SyncChannelFactory."
argument_list|)
decl_stmt|;
name|rc
operator|=
name|SyncToAsyncChannelFactory
operator|.
name|adapt
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Recovery strategy failed.. throw original exception.
throw|throw
name|original
throw|;
block|}
block|}
name|asyncChannelFactoryMap
operator|.
name|put
argument_list|(
name|protocol
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Could not load a AsyncChannelFactory for protcol: "
operator|+
name|protocol
operator|+
literal|", reason: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

