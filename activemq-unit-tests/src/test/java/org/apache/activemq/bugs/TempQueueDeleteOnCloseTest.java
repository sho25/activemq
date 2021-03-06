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
name|bugs
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
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|Session
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Demonstrates how unmarshalled VM advisory messages for temporary queues prevent other connections from being closed.  */
end_comment

begin_class
specifier|public
class|class
name|TempQueueDeleteOnCloseTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
comment|// create a connection and session with a temporary queue
name|Connection
name|connectionA
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connectionA
operator|.
name|setClientID
argument_list|(
literal|"ConnectionA"
argument_list|)
expr_stmt|;
name|Session
name|sessionA
init|=
name|connectionA
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
name|Destination
name|tempQueueA
init|=
name|sessionA
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|sessionA
operator|.
name|createConsumer
argument_list|(
name|tempQueueA
argument_list|)
decl_stmt|;
name|connectionA
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// start and stop another connection
name|Connection
name|connectionB
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connectionB
operator|.
name|setClientID
argument_list|(
literal|"ConnectionB"
argument_list|)
expr_stmt|;
name|connectionB
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionB
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|connectionA
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

