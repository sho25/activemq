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
name|usecases
package|;
end_package

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
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
name|broker
operator|.
name|BrokerService
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
name|spring
operator|.
name|ConsumerBean
import|;
end_import

begin_comment
comment|/**  *   *   */
end_comment

begin_class
specifier|public
class|class
name|StartAndStopClientAndBrokerDoesNotLeaveThreadsRunningTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
interface|interface
name|Task
block|{
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|testStartAndStopClientAndBrokerAndCheckNoThreadsAreLeft
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// consumer
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ConsumerBean
name|listener
init|=
operator|new
name|ConsumerBean
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|// producer
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello World!"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertMessagesArrived
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|runTest
parameter_list|(
name|Task
name|task
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|before
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|task
operator|.
name|execute
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
comment|// need to wait for slow servers
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|int
name|after
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|int
name|diff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|before
operator|-
name|after
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should be at most one more thread. Diff = "
operator|+
name|diff
argument_list|,
name|diff
operator|+
literal|1
operator|<=
name|after
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

