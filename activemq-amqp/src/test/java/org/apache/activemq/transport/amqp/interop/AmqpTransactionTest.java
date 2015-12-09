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
name|interop
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertNotNull
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
name|assertTrue
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
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|QueueViewMBean
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
name|amqp
operator|.
name|client
operator|.
name|AmqpClient
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
name|amqp
operator|.
name|client
operator|.
name|AmqpClientTestSupport
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
name|amqp
operator|.
name|client
operator|.
name|AmqpConnection
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
name|amqp
operator|.
name|client
operator|.
name|AmqpMessage
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
name|amqp
operator|.
name|client
operator|.
name|AmqpReceiver
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
name|amqp
operator|.
name|client
operator|.
name|AmqpSender
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
name|amqp
operator|.
name|client
operator|.
name|AmqpSession
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

begin_comment
comment|/**  * Test various aspects of Transaction support.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransactionTest
extends|extends
name|AmqpClientTestSupport
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testBeginAndCommitTransaction
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|session
operator|.
name|isInTransaction
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testBeginAndRollbackTransaction
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|session
argument_list|)
expr_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|session
operator|.
name|isInTransaction
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSendMessageToQueueWithCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSendMessageToQueueWithRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testReceiveMessageWithCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|received
operator|.
name|accept
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testReceiveMessageWithRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
name|AmqpSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|AmqpMessage
name|message
init|=
operator|new
name|AmqpMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Test-Message"
argument_list|)
expr_stmt|;
name|sender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|begin
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|AmqpMessage
name|received
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|received
operator|.
name|accept
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queue
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|sender
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
