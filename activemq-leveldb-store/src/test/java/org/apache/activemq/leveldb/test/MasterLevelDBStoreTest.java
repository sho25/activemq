begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
operator|.
name|replicated
operator|.
name|MasterLevelDBStore
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
name|io
operator|.
name|FileUtils
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://www.christianposta.com/blog">Christian Posta</a>  */
end_comment

begin_class
specifier|public
class|class
name|MasterLevelDBStoreTest
block|{
name|MasterLevelDBStore
name|store
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
operator|*
literal|60
operator|*
literal|10
argument_list|)
specifier|public
name|void
name|testStoppingStoreStopsTransport
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
operator|new
name|MasterLevelDBStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|setReplicas
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ExecutorService
name|threads
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|threads
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
name|store
operator|.
name|start
argument_list|()
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
comment|//To change body of catch statement use File | Settings | File Templates.
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// give some time to come up..
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|String
name|address
init|=
name|store
operator|.
name|transport_server
argument_list|()
operator|.
name|getBoundAddress
argument_list|()
decl_stmt|;
name|URI
name|bindAddress
init|=
operator|new
name|URI
argument_list|(
name|address
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|Socket
name|socket
init|=
operator|new
name|Socket
argument_list|()
decl_stmt|;
try|try
block|{
name|socket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|bindAddress
operator|.
name|getHost
argument_list|()
argument_list|,
name|bindAddress
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should not have been able to connect..."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Good. We cannot bind."
argument_list|)
expr_stmt|;
block|}
name|threads
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
name|store
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|//To change body of catch statement use File | Settings | File Templates.
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
try|try
block|{
name|socket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|bindAddress
operator|.
name|getHost
argument_list|()
argument_list|,
name|bindAddress
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Can bind, so protocol server must have been shut down."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Server protocol port is still opened.."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|store
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

