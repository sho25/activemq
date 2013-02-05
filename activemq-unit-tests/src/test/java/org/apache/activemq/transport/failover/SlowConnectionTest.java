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
name|failover
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
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
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|util
operator|.
name|Wait
import|;
end_import

begin_class
specifier|public
class|class
name|SlowConnectionTest
extends|extends
name|TestCase
block|{
specifier|private
name|CountDownLatch
name|socketReadyLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testSlowConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|MockBroker
name|broker
init|=
operator|new
name|MockBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|socketReadyLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|int
name|timeout
init|=
literal|1000
decl_stmt|;
name|URI
name|tcpUri
init|=
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:"
operator|+
name|broker
operator|.
name|ss
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"?soTimeout="
operator|+
name|timeout
operator|+
literal|"&trace=true&connectionTimeout="
operator|+
name|timeout
operator|+
literal|"&wireFormat.maxInactivityDurationInitalDelay="
operator|+
name|timeout
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|tcpUri
operator|+
literal|")"
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
operator|new
name|Thread
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Transport count: "
operator|+
name|count
operator|+
literal|", expected<= 1"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|Thread
operator|.
name|getAllStackTraces
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ActiveMQ Transport"
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
operator|==
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|broker
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
class|class
name|MockBroker
extends|extends
name|Thread
block|{
name|ServerSocket
name|ss
init|=
literal|null
decl_stmt|;
specifier|public
name|MockBroker
parameter_list|()
block|{
name|super
argument_list|(
literal|"MockBroker"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|List
argument_list|<
name|Socket
argument_list|>
name|inProgress
init|=
operator|new
name|ArrayList
argument_list|<
name|Socket
argument_list|>
argument_list|()
decl_stmt|;
name|ServerSocketFactory
name|factory
init|=
name|ServerSocketFactory
operator|.
name|getDefault
argument_list|()
decl_stmt|;
try|try
block|{
name|ss
operator|=
name|factory
operator|.
name|createServerSocket
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setSoTimeout
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|socketReadyLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|interrupted
argument_list|()
condition|)
block|{
name|inProgress
operator|.
name|add
argument_list|(
name|ss
operator|.
name|accept
argument_list|()
argument_list|)
expr_stmt|;
comment|// eat socket
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
name|expected
parameter_list|)
block|{             }
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
finally|finally
block|{
try|try
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
for|for
control|(
name|Socket
name|s
range|:
name|inProgress
control|)
block|{
try|try
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit
