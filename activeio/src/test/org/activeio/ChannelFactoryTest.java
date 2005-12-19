begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|AsyncToSyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|SyncToAsyncChannel
import|;
end_import

begin_import
import|import
name|org
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
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|aio
operator|.
name|AIOAsyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|aio
operator|.
name|AIOSyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|nio
operator|.
name|NIOAsyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|nio
operator|.
name|NIOAsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|vmpipe
operator|.
name|VMPipeAsyncChannelPipe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
operator|.
name|vmpipe
operator|.
name|VMPipeAsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
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
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|nio
operator|.
name|NIOSyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|nio
operator|.
name|NIOSyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|socket
operator|.
name|SocketSyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|socket
operator|.
name|SocketSyncChannelServer
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
name|CountDownLatch
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
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|ChannelFactoryTest
extends|extends
name|TestCase
block|{
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ChannelFactoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|boolean
name|aioDisabled
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"disable.aio.tests"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
name|ChannelFactory
name|factory
init|=
operator|new
name|ChannelFactory
argument_list|()
decl_stmt|;
specifier|private
name|SyncChannelServer
name|syncChannelServer
decl_stmt|;
specifier|private
name|SyncChannel
name|clientSynchChannel
decl_stmt|;
specifier|private
name|SyncChannel
name|serverSynchChannel
decl_stmt|;
specifier|private
name|AsyncChannelServer
name|asyncChannelServer
decl_stmt|;
specifier|private
name|AsyncChannel
name|clientAsyncChannel
decl_stmt|;
specifier|private
name|AsyncChannel
name|serverAsyncChannel
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSocket
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|createSynchObjects
argument_list|(
literal|"socket://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncChannelServer
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientSynchChannel
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverSynchChannel
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|createAsynchObjects
argument_list|(
literal|"socket://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|asyncChannelServer
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|SocketSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAIO
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|aioDisabled
condition|)
block|{
return|return;
block|}
name|createSynchObjects
argument_list|(
literal|"aio://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncChannelServer
operator|.
name|getAdapter
argument_list|(
name|AIOSyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientSynchChannel
operator|.
name|getAdapter
argument_list|(
name|AIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverSynchChannel
operator|.
name|getAdapter
argument_list|(
name|AIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|createAsynchObjects
argument_list|(
literal|"aio://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|asyncChannelServer
operator|.
name|getAdapter
argument_list|(
name|AIOSyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|AIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|AIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNIO
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|createSynchObjects
argument_list|(
literal|"nio://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncChannelServer
operator|.
name|getAdapter
argument_list|(
name|NIOSyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientSynchChannel
operator|.
name|getAdapter
argument_list|(
name|NIOSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverSynchChannel
operator|.
name|getAdapter
argument_list|(
name|NIOSyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|createAsynchObjects
argument_list|(
literal|"nio://localhost:0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|asyncChannelServer
operator|.
name|getAdapter
argument_list|(
name|NIOAsyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|NIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|NIOAsyncChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVMPipe
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|createSynchObjects
argument_list|(
literal|"vmpipe://localhost"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncChannelServer
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientSynchChannel
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelPipe
operator|.
name|PipeChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverSynchChannel
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelPipe
operator|.
name|PipeChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|createAsynchObjects
argument_list|(
literal|"vmpipe://localhost"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|asyncChannelServer
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelServer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|clientAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelPipe
operator|.
name|PipeChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|serverAsyncChannel
operator|.
name|getAdapter
argument_list|(
name|VMPipeAsyncChannelPipe
operator|.
name|PipeChannel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createSynchObjects
parameter_list|(
name|String
name|bindURI
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|syncChannelServer
operator|=
name|factory
operator|.
name|bindSyncChannel
argument_list|(
operator|new
name|URI
argument_list|(
name|bindURI
argument_list|)
argument_list|)
expr_stmt|;
name|syncChannelServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientSynchChannel
operator|=
name|factory
operator|.
name|openSyncChannel
argument_list|(
name|syncChannelServer
operator|.
name|getConnectURI
argument_list|()
argument_list|)
expr_stmt|;
name|serverSynchChannel
operator|=
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|syncChannelServer
operator|.
name|accept
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|serverSynchChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|clientSynchChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|syncChannelServer
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createAsynchObjects
parameter_list|(
name|String
name|bindURI
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|asyncChannelServer
operator|=
name|factory
operator|.
name|bindAsyncChannel
argument_list|(
operator|new
name|URI
argument_list|(
name|bindURI
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|accepted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|asyncChannelServer
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|AcceptListener
argument_list|()
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Channel
name|channel
parameter_list|)
block|{
name|serverAsyncChannel
operator|=
name|SyncToAsyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|channel
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|accepted
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|error
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|asyncChannelServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientAsyncChannel
operator|=
name|factory
operator|.
name|openAsyncChannel
argument_list|(
name|asyncChannelServer
operator|.
name|getConnectURI
argument_list|()
argument_list|)
expr_stmt|;
name|accepted
operator|.
name|await
argument_list|(
literal|1000
operator|*
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|clientAsyncChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|asyncChannelServer
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

